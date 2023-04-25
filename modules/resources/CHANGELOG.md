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

## Unreleased

## Changed

- Updates material palette due to recent changes in the specs, there are now more tokens to express the different
  elevations of surfaces
- Buttons shared: for better color fidelity the text and icon colors are both opaque, but the label is made
  transparent (FABs have been excluded from this change)
- Elevated buttons: updated style
- Outlined buttons: updated style
- FABs: there's no need to specify the style for extended state since it was changes from style class to pseudo class
- Surface FABs: updated style

## Fixed

- Buttons: all buttons' color has been corrected. The elevation controls only the shadow not the background. The
  button's color on a specific state (hover, press,...) is given by the mix of the "resting" color and the surface
  tint (there's now also a new utility just for that)

## [11.3.4] - 13-04-2023

## Changed

- MFXIconWrapper: turn some other methods to fluent API

## Fixed

- MFXIconWrapper: wrong value used in size listener

## [11.3.3] - 08-04-2023

## Fixed

- Themes were working partially as I forgot to import the fonts

## [11.3.2 and release] - 08-04-2023

## Changed

- MFXFontIcon: added new constructor
- MFXIconWrapper: make some methods return the wrapper itself for fluent programming

## Fixed

- MFXIconWrapper: fixed exception occurring when using enableRippleGenerator(...) methods as they caused the property to
  trigger and call theme again

## [11.3.1] - 06-04-2023

## Fixed

- Added missing exports

## [11.3.0] - 05-04-2023

## Added

- Added dark variant of purple theme (note how easy it is to derive from the light theme lol). This is experimental
  though!

## Changed

- Completely re-arranged the SASS structure, as well as code style (PascalCase from C#, why?...I felt like it, it's very
  clean actually for this purpose). The biggest pro of this change is that now generated themes are about halt the
  size (in line of codes) than before, which of course means faster parsing for the JavaFX CSS system, and also less
  repetition

## [11.2.0] - 09-02-2023

## Added

- Added two new properties for MFXFontIcons and MFXIconWrappers
- IconsProviders: added a new no-arg method to create a random MFXFontIcon
- Added stylesheets for FABs

## Changed

- Buttons: use the new init sizes properties instead of the JavaFX's ones (pref).
- Buttons: remove the onSurface parameter as it was unused

## [11.1.2] - 07-02-2023

## Changed

- Fix wrongly formatted stylesheet

## [11.1.1] - 02-02-2023

## Added

- Added styles for all the button variants
- Expose all the tokens in the generated CSS theme, so that user can easily extend a theme and have the palette and
  color scheme as look-ups

## Changed

- MFXFontIcon: use old properties name in CSS
- MFXIconWrapper: use the new ripple generator
- Fix fonts path and add a single CSS file which imports all of them
- Added some new utils to MD3 sass, some were moved

## [11.1.0] - 26-01-2023

## Added

- Define public API to define icon packs and icon descriptors
- Preliminary implementation of the new theme used for MaterialFX components, Material Design 3

## Changed

- Drop MFXResources icon pack in favor of FontAwesome 6 variants
- Improved MFXFontIcon
- Moved and improved MFXIconWrapper from core module