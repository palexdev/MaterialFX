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

## [Unreleased] - 14-03-2023

## Added

- BoundLabel: Added a mechanism to detect and retrieve the node responsible for displaying the label's text. This way
  custom controls that rely on this can have full control on the actual text node rather than the label as a whole. This
  mechanism is achieved through overrides rather than listeners, as a consequence there's no performance nor memory
  overhead
- Added a new method responsible for registering the When construct. This is responsible for avoiding duplicates in the
  map (subsequent put) that would probably lead to a memory leak and unexpected behavior

## Changed

- Added method to retrieve the component's behavior object, thus creating a bridge between the Control the View(skin)
  and the Behavior
- Moved executeNow(...) methods to the base class for a better fluent API

## Fixed

- When: Implemented fix from #212

## [11.2.4] - 09-02-2023

## Added

- SceneBuilderIntegration: added new method for debugging while in SceneBuilder

## [11.2.3] - 07-02-2023

## Added

- Introduce convenience API for components developers that want to easily integrate with SceneBuilder

## [11.2.2] - 02-02-2023

## Changed

- Added/Updated documentation where needed

## [11.2.1] - 31-01-2023

## Changed

- Moved animations API to effects module
- SkinBase: pass the behavior object to initialize as argument to initBehavior()

## [11.2.0] - 26-01-2023

## Added

- Added object property to contain Node instances
- Preliminary implementation of the new Behavior API, this may remain the same as I'm pretty satisfied with it
- Preliminary implementation of a base skin to use with controls using the new Behavior API
- Implemented invalidating functionality for When constructs
- Added new utilities to LayoutUtils
- Added missing utility to TextUtils

## Changed

- Moved BoundLabel to core module
- TextUtils: do not take graphic text gap into consideration if the graphic is null when computing a Label's width