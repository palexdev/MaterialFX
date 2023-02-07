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