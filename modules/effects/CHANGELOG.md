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

## [11.0.5] - 06-04-2023

## Added

- Ported many of the Curves/Interpolators used by the Flutter framework for animations to JavaFX
- Animations: Added new method to TimelineBuilder
- Introduced Offset bean needed by some Flutter Curves
- Introduce new container capable of replicating the Container Transform animations as shown by Material 3 guidelines
- Imported and allow export of some beans and utilities from MFXCore

## Changed

- AnimationFactory: allow to build animations with a specific Interpolator or a default one
- Improved efficiency of MFXRippleGenerators when disabled through the visible property. This was needed especially
  because in CSS you cannot set the disable state but only the visibility
- Moved Curve to new base package
- MFXRippleGenerator: make sure the cached clip is updated when the region's layout bounds change
- MFXRippleGenerator: added mechanism to define how mouse events are managed, this is a way to fix components that
  were triggering pseudo states even when the mouse was outside it

## Removed

- Removed BezierEasing implementation class in favor of the implementation offered by Flutter, Cubic.java, which is much
  simpler and more performant

## [11.0.4] - 09-02-2023

## Changed

- ElevationLevel: hopefully for the last time, improve the shadows transition animation

## [11.0.3] - 02-02-2023

## Changed

- Slight change to shadow transitions

## [11.0.2] - 31-01-2023

## Changed

- Small refactors due to Animations API being moved from core module
- Adapted shadows to Material Design 3
- Added some methods to ElevationLevel enum

## [11.0.1] - 26-01-2023

## Added

- APIs extracted from core module, this way you can use the provided effects in any project without necessarily needing
  the core or components modules