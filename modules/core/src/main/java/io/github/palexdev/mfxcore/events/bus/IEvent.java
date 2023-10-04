package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.events.Event;

/**
 * Functional interface representing a generic event that carries some generic data, used by {@link IEventBus}.
 * <p>
 * An example could be as follows:
 * <pre>
 * {@code
 * public class App extends Application {
 *     // While there is no limitation on how many buses you can create...
 *     // Ideally you want a single instance for everything in your app
 *     public static final IEventBus bus = new SimpleEventBus();
 *
 *     @Override
 *     public void start(Stage stage) {
 *         // Initialization code
 *         ...
 *         ...
 *
 *         // When app ready
 *         bus.publish(new AppEvents.AppReadyEvent());
 *
 *         // Listen for close requests
 *         bus.subscribe(AppEvents.AppCloseEvent.class, e -> {
 *             // Maybe do something with e.data() which returns the Node that requested app close
 *             stop();
 *         });
 *     }
 *
 *     @Override
 *     public void stop() {
 *         // Stop code
 *     }
 * }
 *
 * public class AnotherClass {
 *     private final Node aNode = ...;
 *
 *     public AnotherClass() {
 *         aNode.setOnClicked(e -> App.bus.publish(new AppEvents.AppCloseEvent(aNode)));
 *         // When this node is clicked, the app will close
 *         // Now, the example is a bit lame maybe. But what's important is for yoy to understand the concept
 *         // The potential of an event bus is that you can easily decouple code with events
 *     }
 * }
 *
 * public class AppEvents extends Event {
 *  public AppEvents() {}
 *  public AppEvents(Object data) {super(data);}
 *
 *  public static class AppReadyEvent extends AppEvent {}
 *  public static class AppCloseEvent extends AppEvent {
 *      public AppCloseEvent(Node node) {super(node);}
 *
 *      @Override
 *      public Node data() {
 *          return (Node) super.data();
 *      }
 *  }
 * }
 * }
 * </pre>
 *
 * @see Event
 */
@FunctionalInterface
public interface IEvent {

	/**
	 * @return the data carried by the event
	 */
	Object data();
}
