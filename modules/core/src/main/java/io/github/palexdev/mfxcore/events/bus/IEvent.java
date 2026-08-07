/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxcore.events.bus;

import io.github.palexdev.mfxcore.events.Event;

//@formatter:off fuck IntelliJ I guess
/// Functional interface representing a generic event that carries some generic data, used by [EventBus].
///
/// An example could be as follows:
/// ```java
/// public class App extends Application{
///   // While there is no limitation on how many buses you can create...
///   // Ideally you want a single instance for everything in your app
///   public static final EventBus bus = new SimpleEventBus();
///
///  @Override
///  void start(Stage stage) {
///    // Initialization code......
///    // When app is ready
///    bus.publish(new AppEvents.AppReadyEvent());
///    // Listen for close requests
///    bus.subscribe(AppEvents.AppCloseEvent.class, e -> {
///      // Maybe do something with e.data() which returns the Node that requested app close
///      stop();
///    });
///  }
///
///  void stop(){// Stop code}}
///
///  public class AnotherClass{
///    private final Node aNode = ...;
///
///    public AnotherClass() {
///      aNode.setOnClicked(e -> App.bus.publish(new AppEvents.AppCloseEvent(aNode)));
///      // When this node is clicked, the app will close
///      // Now, the example is a bit lame maybe. But what's important is for you to understand the concept.
///      // The potential of an event bus is that you can easily decouple code with events
///    }
///  }
///
///  public class AppEvents extends Event{
///    public AppEvents(){}
///
///    public AppEvents(Object data){super(data);}
///
///    public static class AppReadyEvent extends AppEvent{}
///
///    public static class AppCloseEvent extends AppEvent{
///      public AppCloseEvent(Node node){super(node);}
///
///      @Override
///      Node data(){return(Node)super.data();}
///    }
///}
///```
/// @see Event
//@formatter:on
@FunctionalInterface
public interface IEvent {

    /// @return the data carried by the event
    Object data();
}
