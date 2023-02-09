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

## [11.16.0] - 09-02-2023

## Added

- Introduced two new styleable properties to both MFXControl and MFXLabeled, to allow specifying the initial sizes of a
  component without relying on JavaFX's CSS properties since once they are set, they cannot be overwritten (bug or not
  it's a shitty behavior)
- Introduced a new method to both MFXControl and MFXLabeled to allow implementations to customize the SceneBuilder
  integration
- Introduced FABs and Extended FABs
- Added two new theming APIs to define components' variants

## Changed

- MFXButtonSkin: listener on the graphic property is not needed, let the BoundLabel handle it
- MFXButtonSkin: do not apply the listener on the content display property if the button is of type MFXFab

## Fixed

- MFXElevatedButton: fixed NullPointerException caused by the elevation property as in some occasions both the oldValue
  and newValue can be null

## [11.15.2] - 07-02-2023

## Changed

- Rename materialfx package to mfxcomponents, for consistency with other modules
- Make buttons use the new SceneBuilder integration API mentioned above

## [11.15.1] - 02-02-2023

## Added

- Implemented all variants of MFXButton

## Changed

- Implemented MFXButtonBehavior
- MFXButtonSkin: add ripple effect, adapt to changes made in SkinBase

## [11.15.0] - 26-01-2023

## Added

- Preliminary implementation of components hierarchy, the idea is to build the components from scratch making them
  extend either MFXControl or MFXLabeled
- Preliminary implementation of MFXButton and MFXElevatedButton variant

