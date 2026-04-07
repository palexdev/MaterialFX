/*
 * Copyright (C) 2025 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.input;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.palexdev.mfxcore.base.Disposable;
import io.github.palexdev.mfxcore.base.TriConsumer;
import io.github.palexdev.mfxcore.collections.WeakHashSet;
import io.github.palexdev.mfxcore.observables.When;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;

/// In the veins of the great and so useful [When] construct, this class, strongly inspired by it and its implementations,
/// allows doing pretty much the same things but on [Events][Event].
///
/// This construct can be read as "When an event of a given type occurs on a given event target, then do this" or
/// "Intercept events of a given type on a given event target, then do this".
///
/// Just like the [When] construct, you can:
///  - Specify an action on the intercept event
///  - Specify a condition under which process or not the event
///  - Specify an action to perform if the condition was not met
///  - Make the handler one shot, in other words it is automatically disposed after the first time it's triggered.
///
/// The one thing missing as of now is the possibility of run the set action immediately (the executeNow() functionality)
/// as the action needs an event. The only way would be to generate synthetic events, which is not easy and may not work
/// as intended.
///
/// Another difference is that handlers can be registered as filters too, you can specify such behavior using [#asFilter()].
///
/// To activate this construct after you've set everything, make sure to call [#register()].
///
/// ```
/// // A full example could be...
/// Button bnt = new Button("Click me");
/// WhenEvent.intercept(btn, MouseEvent.MOUSE_CLICKED)
///   .condition(e -> e.getButton() == MouseButton.PRIMARY)
///   .process(e -> System.out.println("Button was clicked"))
///   .otherwise((w, e) -> {
///     // What happens here is that, if the pressed mouse button was not the primary
///     // then we print it to the console, and then we dispose the construct,
///     // meaning that further events won't be processed
///     // Note that the 'w' parameter in the lambda is a WeakReference to the construct,
///     // so first we make sure it was not garbage collected (not null)
///     System.out.println("Not the primary button!");
///     WhenEvent<MouseEvent> we = w.get();
///     if (we != null) we.dispose();
/// }).asFilter().oneShot().register();
/// // More details
/// // 1) Note that the asFilter functionality can be quite useful. In fact, you can even create
/// // a filter that consumes the events, thus avoiding other constructs or handlers to process the same type of events
/// // 2) This specific example I would call it as a "full one shot" haha. Check this: if you press the
/// // PRIMARY button, the oneShot() will be taken into account, so the construct will only run once and then disposed
/// // If you press any other button, you enter the "otherwise" action, and there it is also disposed.
/// // So, this specific example will run once and only once`
/// ```
///
/// @see EventTarget
public class WhenEvent<T extends Event> implements Disposable {
    //================================================================================
    // Properties
    //================================================================================
    protected static final WhenEventsMap whens = new WhenEventsMap();
    private EventTarget target;
    private EventType<T> eventType;
    private EventHandler<T> handler;
    private HandlerWrapper handlerWrapper;
    private Consumer<T> action;
    private Function<T, Boolean> condition = e -> true;
    private BiConsumer<WeakReference<WhenEvent<T>>, T> otherwise = (w, e) -> {};
    private boolean oneShot = false;
    private boolean asFilter = false;
    private boolean active = false;

    //================================================================================
    // Constructors
    //================================================================================
    public WhenEvent(EventTarget target, EventType<T> eventType) {
        this.target = target;
        this.eventType = eventType;
    }

    public static <T extends Event> WhenEvent<T> intercept(EventTarget target, EventType<T> eventType) {
        return new WhenEvent<>(target, eventType);
    }

    //================================================================================
    // Methods
    //================================================================================

    /// Sets the [Consumer] used to "process" any given event.
    public WhenEvent<T> handle(Consumer<T> action) {
        this.action = action;
        return this;
    }

    /// Sets the condition under which an event will be passed to the action specified by [#handle(Consumer)].
    ///
    /// @see #otherwise(BiConsumer)
    public WhenEvent<T> condition(Function<T, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    /// Allows you to specify an action to run for events that fails the check set by [#condition(Function)].
    public WhenEvent<T> otherwise(BiConsumer<WeakReference<WhenEvent<T>>, T> otherwise) {
        this.otherwise = otherwise;
        return this;
    }

    /// Responsible for building the [EventHandler] with all the given parameters and then add it on the specified
    /// event target. This method won't run if the construct was disposed before, or if the handler is not null
    /// (meaning that it was already registered before).
    public WhenEvent<T> register() {
        if (isDisposed() || handler != null) return this;
        handlerWrapper = new HandlerWrapper(asFilter);

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

    /// Invoked by [#register()] if everything went well. Here, the construct is added to a static Map that retains
    /// all the built constructs. The mapping is as follows: `EventTarget -> Set<WhenEvent<?>>`.
    ///
    /// Finally, the built [EventHandler] is added on the specified [EventTarget].
    protected void doRegister() {
        WeakHashSet<WhenEvent<?>> set = whens.computeIfAbsent(target, n -> new WeakHashSet<>());
        set.add(this);
        handlerWrapper.register();
        active = true;
    }

    /// @return whether the construct is "one-shot"
    /// @see #oneShot()
    public boolean isOneShot() {
        return oneShot;
    }

    /// Sets the construct as 'one-shot', meaning that once an event occurs the first time and the action is executed,
    /// the construct will automatically dispose itself.
    public WhenEvent<T> oneShot() {
        this.oneShot = true;
        return this;
    }

    /// @return whether the built [EventHandler] will be registered as a simple handler or filter
    /// @see #asFilter()
    public boolean isFilter() {
        return asFilter;
    }

    /// Sets a flag that will make the built [EventHandler] be registered as a filter.
    public WhenEvent<T> asFilter() {
        return asFilter(true);
    }

    /// Sets a flag that will make the built [EventHandler] be registered as a filter, to the given value.
    public WhenEvent<T> asFilter(boolean asFilter) {
        this.asFilter = asFilter;
        return this;
    }

    /// Unregisters the [EventHandler] from the event target, sets everything to null, and removes the construct from
    /// the "global" map.
    @Override
    public void dispose() {
        if (target != null) {
            if (handler != null) {
                handlerWrapper.unregister();
                handler = null;
                handlerWrapper = null;
            }
            handleMapDisposal();
            eventType = null;
            target = null;
            active = false;
        }
    }

    /// Calls [#dispose()] on the given `WhenEvent` construct.
    public static void dispose(WhenEvent<?> w) {
        if (w != null) w.dispose();
    }

    /// Calls [#dispose(WhenEvent)] on each of the given `WhenEvent` constructs.
    public static void dispose(WhenEvent<?>... whens) {
        for (WhenEvent<?> w : whens) w.dispose();
    }

    /// @return whether the construct is active and not disposed, the flag is set if [#doRegister()] run successfully
    public boolean isActive() {
        return active;
    }

    /// @return whether this construct has been disposed before. By default, checks if the given [EventType] and
    /// event target is null
    public boolean isDisposed() {
        return target == null &&
               eventType == null;
    }

    /// @return the total number of existing `WhenEvent` constructs for a given target
    public static int size(EventTarget target) {
        return Optional.ofNullable(whens.get(target))
            .map(WeakHashSet::size)
            .orElse(0);
    }

    /// @return the total number of existing `WhenEvent` constructs for any registered [ObservableValue]
    public static int totalSize() {
        return whens.keySet().stream()
            .mapToInt(WhenEvent::size)
            .sum();
    }

    /// @return this construct wrapped in a [WeakReference]
    protected final WeakReference<WhenEvent<T>> asWeak() {
        return new WeakReference<>(this);
    }

    /// This is called when handling the construct's disposal.
    /// The aforementioned Map used to store the built `WhenEvent` constructs, uses this mapping:
    /// `[key -> value] = [EventTarget -> WeakHashSet<WhenEvent<?>>]`
    ///
    /// This is because `WhenEvent` allows registering multiple constructs on a single event target,
    /// for this reason, there are several things to consider on disposal:
    ///  1) There is a non-null Set mapped to the current event target
    ///  2) The construct can be removed from the Set without any null check, but after the removal
    /// it's good to check whether the Set is now empty
    ///  3) In such case, we can also remove the mapping from the Map.
    protected final void handleMapDisposal() {
        WeakHashSet<WhenEvent<?>> set = whens.get(target);
        if (set == null) return;
        set.remove(this);
        if (set.isEmpty()) whens.remove(target);
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public static class WhenEventsMap extends WeakHashMap<EventTarget, WeakHashSet<WhenEvent<?>>> {}

    /// Utility internal class that allows to remove some ifs when registering/unregistering the [EventHandler]
    /// on the event target.
    class HandlerWrapper {
        private final TriConsumer<EventTarget, EventType<T>, EventHandler<T>> reg;
        private final TriConsumer<EventTarget, EventType<T>, EventHandler<T>> unReg;

        HandlerWrapper(boolean asFilter) {
            if (asFilter) {
                reg = EventTarget::addEventFilter;
                unReg = EventTarget::removeEventFilter;
            } else {
                reg = EventTarget::addEventHandler;
                unReg = EventTarget::removeEventHandler;
            }
        }

        public void register() {
            reg.accept(target, eventType, handler);
        }

        public void unregister() {
            unReg.accept(target, eventType, handler);
        }
    }
}
