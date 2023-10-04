# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).  
(Date format is dd-MM-yyyy)

## Type of Changes

- **Added** for new features.
- **Changed** for changes in existing functionality.
- **Deprecated** for soon-to-be removed features.
- **Removed** for now removed features.
- **Fixed** for any bug fixes.

[//]: ##[Unreleased]

## [11.8.0] - 04-10-2023

### Added

- Added 'java.prefs' module
- With the new Behavior API, and the recent acknowledgement on how to build UI components following the MVC pattern, I
  decided to bring the base classes here from the Components module
  Now any project that wants to implement custom components like this, and doesn't want to rely on the Components module
  can just add this as a dependency
- Label: added new useful property to observe for text truncation
- Implemented simple event bus. Imported from my personal recent projects
- Implemented abstraction over Java's Preferences API. This also is imported from my recent personal projects

## [11.7.0] - 25-09-2023

### Added

- Label: added a way to completely disable the text truncation functionality. This is yet again one of the things JavaFX
  imposes, now if you use my extension you have this possibility too
- Implemented equivalent of When constructs for Events: the WhenEvent construct
- Added tests for the new WhenEvent construct

### Changed

- The Behavior API has been reviewed. Now the base class (BehaviorBase) acts like an interface by offering a bunch of
  methods that covers most of the input event types in JavaFX. This adds a level of abstraction that allows skin to bind
  to the behavior without knowing anything about it. Not only that, the main issue I was facing is that sometimes when
  an event occurs after calling the behavior code, we may want to execute some additional action in the view (this is
  one of the differences with the standard MVC pattern). To keep the level of modularity we need, the new methods allow
  passing an additional argument, which is a callback that gives access to the event, this way the skin can register
  some additional actions to perform on its building blocks without adding any dependency on the behavior side.
  The other important change is that now the behavior allows registering only EventHandlers. Listeners for properties
  have been moved to MFXSkinBase
- Minor changes to When, OnChanged and OnInvalidated, to align with the new WhenEvent construct

### Removed

- For the Behavior review, the action package and classes have been removed

## [11.6.4] - 24-08-2023

### Changed

- SelectionProperty: reviewed invalidation logic. The core part has been moved to the invalidated() method, this allows
  bindings to work, as well as simplifying the process
- SelectionGroup: reviewed for the above changes

### Fixed

- SelectionGroup: fixed MultipleSelectionHandler logic

## [11.6.3] - 01-06-2023

### Changed

- Resettable properties now notify about change when reset by default

### Fixed

- Resettable properties now use Objects.equals(...) to check for equality as it is null safe

## [11.6.2] - 29-05-2023

### Changed

- CSSFragment added methods to support Application.setUserAgentStylesheet(...)

## [11.6.1] - 23-05-2023

### Added

- Added simple extension of Label, to allow setting the wrapping width (rather than the max width directly), setting the
  font smoothing algorithm (without the need of selecting the text node in CSS), and to allow the retrieval of the
  Label's Text node as well as executing an action when retrieved

### Changed

- BoundLabel: move Text node detection code to the new base class, Label

## [11.6.0] - 17-05-2023

### Added

- Added init method to BehaviorBase
- Added observable implementation of the CircularQueue
- Added method to ColorUtils that returns a Color with the desired opacity
- Added many convenient methods to CSSFragment, now you don't necessarily have to type the whole JavaFX property but
  just the value
- Added tests for the ObservableCircularQueue
- Implemented cache util to measure the sizes of a text node only when needed. Can vastly improve the performance of
  components relying on text sizes computation
- TextUtils: added method to compute both the text width and height in one go

### Changed

- CircularQueue: renamed size to capacity, moved setter and added getter
- ObservableStack: do not allow changing the backing data structure

## [11.5.0] - 07-05-2023

### Added

- Add to both Position and Size beans, methods that return empty or invalid states
- Implement StyleableProperty for Position beans, works the same as StyleableSizeProperty
- Added convenience method to LayoutUtils to quickly create an empty Bounds object
- Added extension of Text to set the wrapping width in CSS

### Changed

- Make CSSFragment smarted and add support for multiple selector

## [11.4.0] - 03-05-2023

### Added

- Introducing the Selection API that will be used by components such as checkboxes, radio buttons, switches and such.
  However, the API has been developed to be as general purpose as possible, meaning that it can be used with anything
  implementing the needed interface
- Added tests for the new Selection API

### Changed

- Moved CSSFragment from components

## [11.3.1] - 11-04-2023

### Added

- Added new collection, WeakHashSet

### Changed

- When API v2! Now allowing again multiple Whens on the same ObservableValue
- Synchronized properties: avoid using When for the waiting property for two reasons: 1) it can't be disposed easily 2)
  overriding the set method of the property avoids the need of listeners, improving memory usage and performance
- Validation: adapt to new When API

## [11.3.0] - 06-04-2023

### Added

- BoundLabel: Added a mechanism to detect and retrieve the node responsible for displaying the label's text. This way
  custom controls that rely on this can have full control on the actual text node rather than the label as a whole. This
  mechanism is achieved through overrides rather than listeners, as a consequence there's no performance nor memory
  overhead
- Added a new method responsible for registering the When construct. This is responsible for avoiding duplicates in the
  map (subsequent put) that would probably lead to a memory leak and unexpected behavior
- When: mention in documentation that only a When construct per Observable can be active at anytime
- When: Added method to force the activation/listening by disposing any other When on the given Observable (if one
  exists already)
- When: added method to check if a construct has been disposed before
- When: added non-static method for disposal
- Added some tests for When constructs

### Changed

- Added method to retrieve the component's behavior object, thus creating a bridge between the Control the View(skin)
  and the Behavior
- Moved executeNow(...) methods to the base class for a better fluent API
- OnChanged, OnInvalidated: also set the Observable to null on disposal
- LayoutUtils: optimize some of the algorithms by not computing the snapped margins if they are EMPTY
- LayoutUtils: snap the computed sizes by default before any other calculation as this could lead to wrong pixel
  positions

### Removed

- Removed SkinBase, moved to components as MFXSkinBase

### Fixed

- When: Implemented fix from #212
- BoundLabel: fixed text node detection algorithm
- Tests on Bindings where failing due to recent changes on When constructs. Since a Bindings may need to add/register a
  listener more than once on a same observable avoid using When constructs

## [11.2.4] - 09-02-2023

### Added

- SceneBuilderIntegration: added new method for debugging while in SceneBuilder

## [11.2.3] - 07-02-2023

### Added

- Introduce convenience API for components developers that want to easily integrate with SceneBuilder

## [11.2.2] - 02-02-2023

### Changed

- Added/Updated documentation where needed

## [11.2.1] - 31-01-2023

### Changed

- Moved animations API to effects module
- SkinBase: pass the behavior object to initialize as argument to initBehavior()

## [11.2.0] - 26-01-2023

### Added

- Added object property to contain Node instances
- Preliminary implementation of the new Behavior API, this may remain the same as I'm pretty satisfied with it
- Preliminary implementation of a base skin to use with controls using the new Behavior API
- Implemented invalidating functionality for When constructs
- Added new utilities to LayoutUtils
- Added missing utility to TextUtils

### Changed

- Moved BoundLabel to core module
- TextUtils: do not take graphic text gap into consideration if the graphic is null when computing a Label's width