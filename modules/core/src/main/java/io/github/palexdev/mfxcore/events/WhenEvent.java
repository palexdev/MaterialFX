/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.events;

import io.github.palexdev.mfxcore.base.TriConsumer;
import io.github.palexdev.mfxcore.behavior.DisposableAction;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * In the veins of the great and so useful {@link When} construct, this class, strongly inspired by it and its implementations,
 * allows to do pretty much the same things but on {@link Event}s.
 * <p>
 * This construct can be read as "When an event of a given type, occurs on a given node, then do this" or "Intercept events
 * of a given type on a given node, then do this".
 * <p></p>
 * Just like the {@link When} construct, you can specify an action on the intercept event, a condition under which process
 * or not the event, an action to perform if the condition was not met, and of course you can create a handler that is
 * one shot, in other words automatically disposed after the first time its triggered.
 * <p>
 * The one thing that is missing as of now is the possibility of run the set action immediately (the executeNow() functionality)
 * as the action needs an event, and the only way would be to generate synthetic events, which is not easy and may not work
 * as intended.
 * <p>
 * Another difference is that, handlers can be registered as filters too, you can specify such behavior using {@link #asFilter()}.
 * <p></p>
 * To activate this construct after you've set everything make sure to call {@link #register()}.
 * <p></p>
 * <pre>
 * {@code
 * // A full example could be...
 * MFXButton bnt = new MFXButton("Click me");
 * WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
 *     .condition(e -> e.getButton() == MouseButton.PRIMARY)
 *     .process(e -> System.out.println("Button was clicked"))
 *     .otherwise((w, e) -> {
 *         // What happens here is that, if the pressed mouse button was not the primary
 *         // then we print it to the console, and then we dispose the construct,
 *         // meaning that further events won't be processed
 *         // Note that the 'w' parameter in the lambda is a WeakReference to the construct,
 *         // so first we make sure it was not garbage collected (not null)
 *         System.out.println("Not the primary button!");
 *         WhenEvent<MouseEvent> we = w.get();
 *         if (we != null) we.dispose();
 *     })
 *     .asFilter()
 *     .oneShot()
 *     .register();
 *
 *     // More details
 *     // 1) Note that the asFilter functionality can be quite useful. In fact, you can even create
 *     // a filter that consumes the events, thus avoiding other constructs or handlers to process the same type of events
 *     // 2) This specific example I would call it as a "full one shot" haha. Check this, if you press the
 *     // PRIMARY button, the oneShot() will be taken into account, so the construct will only run once and then disposed
 *     // If you press any other button, you enter the "otherwise" action, and there it is also disposed.
 *     // So, this specific example will run once and only once
 * }
 * </pre>
 */
public class WhenEvent<T extends Event> implements DisposableAction {
	//================================================================================
	// Properties
	//================================================================================
	protected static final WhenEventsMap whens = new WhenEventsMap();
	private Node node;
	private EventType<T> eventType;
	private EventHandler<T> handler;
	private Consumer<T> action;
	private Function<T, Boolean> condition = e -> true;
	private BiConsumer<WeakReference<WhenEvent<T>>, T> otherwise = (w, e) -> {};
	private boolean oneShot = false;
	private boolean asFilter = false;
	private RegUnRegWrapper rurWrapper;
	private boolean active = false;

	//================================================================================
	// Constructors
	//================================================================================
	public WhenEvent(Node node, EventType<T> eventType) {
		this.node = node;
		this.eventType = eventType;
	}

	public static <T extends Event> WhenEvent<T> intercept(Node node, EventType<T> eventType) {
		return new WhenEvent<>(node, eventType);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the {@link Consumer} used to "process" any given event.
	 */
	public WhenEvent<T> process(Consumer<T> action) {
		this.action = action;
		return this;
	}

	/**
	 * Sets the condition under which an event will be passed to the action specified by {@link #process(Consumer)}.
	 *
	 * @see #otherwise(BiConsumer)
	 */
	public WhenEvent<T> condition(Function<T, Boolean> condition) {
		this.condition = condition;
		return this;
	}

	/**
	 * Allows you to specify an action to run for events that fails the check set by {@link #condition(Function)}.
	 */
	public WhenEvent<T> otherwise(BiConsumer<WeakReference<WhenEvent<T>>, T> otherwise) {
		this.otherwise = otherwise;
		return this;
	}

	/**
	 * Responsible for building the {@link EventHandler} with all the given parameters and then add it on the specified
	 * Node. This method won't run if the construct was disposed before, or if the handler is not null (meaning that it
	 * was already registered before).
	 */
	public WhenEvent<T> register() {
		if (isDisposed() || handler != null) return this;
		rurWrapper = new RegUnRegWrapper();

		if (oneShot) {
			handler = e -> {
				if (condition.apply(e)) {
					action.accept(e);
					dispose();
				} else {
					otherwise.accept(asWeak(), e);
				}
			};
		} else {
			handler = e -> {
				if (condition.apply(e)) {
					action.accept(e);
				} else {
					otherwise.accept(asWeak(), e);
				}
			};
		}
		doRegister();
		return this;
	}

	/**
	 * Invoked by {@link #register()} if everything went well. Here, the construct is added to a static Map that retains
	 * all the built constructs, the mapping is as follows: Node -> Set<WhenEvent<?>>.
	 * <p>
	 * Finally, the built {@link EventHandler} is added on the specified Node.
	 */
	protected void doRegister() {
		WeakHashSet<WhenEvent<?>> set = whens.computeIfAbsent(node, n -> new WeakHashSet<>());
		set.add(this);
		rurWrapper.reg();
		active = true;
	}

	/**
	 * @return whether the construct is "one-shot"
	 * @see #oneShot()
	 */
	public boolean isOneShot() {
		return oneShot;
	}

	/**
	 * Sets the construct as 'one-shot', meaning that once an event occurs the first time and the action is executed,
	 * the construct will automatically dispose itself.
	 */
	public WhenEvent<T> oneShot() {
		this.oneShot = true;
		return this;
	}

	/**
	 * @return whether the built {@link EventHandler} will be registered as a simple handler or filter
	 * @see #asFilter()
	 */
	public boolean isFilter() {
		return asFilter;
	}

	/**
	 * Sets a flag that will make the built {@link EventHandler} be registered as a filter.
	 */
	public WhenEvent<T> asFilter() {
		this.asFilter = true;
		return this;
	}

	/**
	 * Unregisters the {@link EventHandler} from the node, sets everything to null, and removes the construct from
	 * the "global" map.
	 */
	@Override
	public void dispose() {
		if (node != null) {
			if (handler != null) {
				rurWrapper.unReg();
				handler = null;
				rurWrapper = null;
			}
			handleMapDisposal();
			eventType = null;
			node = null;
			active = false;
		}
	}

	/**
	 * Calls {@link #dispose()} on the given {@code WhenEvent} construct.
	 */
	public static void dispose(WhenEvent<?> w) {
		if (w != null) w.dispose();
	}

	/**
	 * Calls {@link #dispose(WhenEvent)} on each of the given {@code WhenEvent} constructs.
	 */
	public static void dispose(WhenEvent<?>... whens) {
		for (WhenEvent<?> w : whens) w.dispose();
	}

	/**
	 * @return whether the construct is active and not disposed, the flag is set if {@link #doRegister()} run successfully
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @return whether this construct has been disposed before. By default, checks if the given {@link EventType} and node
	 * are null
	 */
	public boolean isDisposed() {
		return node == null &&
			eventType == null;
	}

	/**
	 * @return the total number of existing {@code WhenEvent} constructs for a given node
	 */
	public static int size(Node node) {
		return Optional.ofNullable(whens.get(node))
			.map(WeakHashSet::size)
			.orElse(0);
	}

	/**
	 * @return the total number of existing {@code WhenEvent} constructs for any registered {@link ObservableValue}
	 */
	public static int totalSize() {
		return whens.keySet().stream()
			.mapToInt(WhenEvent::size)
			.sum();
	}

	/**
	 * @return this construct wrapped in a {@link WeakReference}
	 */
	protected final WeakReference<WhenEvent<T>> asWeak() {
		return new WeakReference<>(this);
	}

	/**
	 * This is called when handling the construct's disposal.
	 * The aforementioned Map used to store the built {@code WhenEvent} constructs, uses this mapping:
	 * <pre>
	 * {@code
	 * [key -> value] = [Node -> WeakHashSet<WhenEvent<?>>]
	 * }
	 * </pre>
	 * This is because {@code WhenEvent} allows to register multiple constructs on a single node,
	 * for this reason, there are several things to consider on disposal:
	 * <p> 1) There is a non-null Set mapped to the current node
	 * <p> 2) The construct can be removed from the Set without any null check, but after the removal
	 * it's good to check whether the Set is now empty
	 * <p> 3) In such case, we can also remove the mapping from the Map.
	 */
	protected final void handleMapDisposal() {
		WeakHashSet<WhenEvent<?>> set = whens.get(node);
		if (set == null) return;
		set.remove(this);
		if (set.isEmpty()) whens.remove(node);
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	public static class WhenEventsMap extends WeakHashMap<Node, WeakHashSet<WhenEvent<?>>> {}

	/**
	 * Utility internal class that allows to remove some ifs when registering/unregistering the {@link EventHandler}
	 * on the node.
	 */
	protected class RegUnRegWrapper {
		private final TriConsumer<Node, EventType<T>, EventHandler<T>> reg;
		private final TriConsumer<Node, EventType<T>, EventHandler<T>> unReg;

		protected RegUnRegWrapper() {
			if (asFilter) {
				reg = Node::addEventFilter;
				unReg = Node::removeEventFilter;
			} else {
				reg = Node::addEventHandler;
				unReg = Node::removeEventHandler;
			}
		}

		public void reg() {
			reg.accept(node, eventType, handler);
		}

		public void unReg() {
			unReg.accept(node, eventType, handler);
		}
	}
}
