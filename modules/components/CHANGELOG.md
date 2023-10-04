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

## [11.24.0] - 04-10-2023

### Added

- Theme: added new convenience method to set it as the global UA

### Changed

- MFXControl, MFXLabeled and MFXSkinBase base classes have been restructured to move them in the Core module

### Fixed

- MFXPopup and MFXTooltip: fix regression in hide() method. Attempting to close when the owner's windows had already
  been closed would result in a JavaFX exception. Altough, from my testings this issue seem to occur only in one
  specific case: when the app was closed through 'Platform.exit()' and the popup was open
- MFXPopupSkin: properly handle not animated popups

## [11.23.0] - 25-09-2023

### Added

- MFXIconButton: added support for animated icon switching
- MFXCheckbox: added support for animated state change as shown by M3 guidelines. Now the icon of SELECTED and
  INDETERMINATE state needs to be changed through two new properties since the skin has been modified as well for this
- MFXCheckboxSkin: implemented animations as shown by M3 guidelines. Now the icon is wrapped in a MFXIconWrapper
- MFXIconButtonSkin: the icon is now contained by a MFXIconWrapper which is also used to play the icon switch animation

### Changed

- Behavior implementations have been reviewed and adapted to the core API changes. In particular: all code
  related to retrieval of nodes in the skin, animations or layout computations, has been moved/removed
- MFXButtonBehaviorBase: removed ripple generator retrieval. Using callbacks in the skin now
- MFXCheckboxBehavior: removed icon retrieval. Using callbacks in the skin now
- MFXCheckboxBehavior: replace ifs with EnumUtils.next(...) utility. Also, now the states order has been changed as
  shown
  by M3 guidelines: UNSELECTED -> SELECTED -> INDETERMINATE
- MFXSelectableBehaviorBase: added documentation and minor adaptions to the aforementioned API changes
- MFXSkinBase API has been changed as a result of the Behavior API review. Now it's the skin to manage listeners, while
  still maintaining a delegate method to register EventHandlers on the control's behavior
- MFXCheckbox: moved TriState enumerator to separate class
- MFXFabBase has been deeply changed. First of all, now the text and icon properties are stored in a wrapping object,
  see class documentation on why. The behavior type has changed to MFXButtonBehaviorBase. Since animations related stuff
  has been moved from the behavior to its skin, there are new properties to control them
- In all skins, the disposal method has been reviewed. There were repetitions and unnecessary code that could be moved
  to
  the superclasses
- All skins now register listeners and handlers through the When and WhenEvent constructs, and make use of the new
  correlated methods in MFXSkinBase.
- Skins that previously were relying on the behavior class to perform some additional action on their building blocks
  now make use of callbacks, given by the reviewed Behavior API
- All skins inheriting from MFXLabeledSkin: make sure to use the getCachedTextWidth() and getCachedTextHeight() methods
  instead of using the tmCache object directly
- MFXFabSkin has been completely redone from scratch

### Removed

- MFXFabBehavior: animations and layout computations moved to the skin, as a result, the class remained empty, so it's
  been removed. Now FABs use generic MFXButtonBehaviorBase
- Removed EXTENDED constant from FABVariants as it was not used

### Fixed

- MFXCheckboxSkin and MFXIconButtonSkin: re-enabled ripple generation, oversight of recent update to Skin and Behavior
  APIs

## [11.22.0] - 24-08-2023

### Added

- Added tests for the reworked CheckBoxes

### Changed

- CheckBoxes have been reworked

## [11.21.0] - 01-06-2023

### Added

- Added deployment API to Theme interface. Now themes can specify a zip file of assets needed for them to work properly.
  This zip file can be extracted on the filesystem so that when merging the theme with UserAgentBuilder the resources
  will still work

### Changed

- MFXCheckbox: SceneBuilder integration set text to "Checkbox"
- MFXFabBase; SceneBuilder integration set text to "Floating Action Button"
- UserAgentBuilder: Implemented code to resolve @import statements and 'local' URL resources. These features will work
  only if the Theme deploys the needed assets on the disk first

### Fixed

- MFXTooltip and MFXPopup: Partially revert commit d209dd275b9c1c87b8ee578f2362da2abaebfbda. If we don't call close()
  when the owner's window is not showing anymore terrible things happen, like the entire app crashing with problematic
  frame reports :\

## [11.20.0] - 29-05-2023

### Added

- Implemented checkboxes
- Added UserAgentBuilder: a mechanism that allows to merge a set of given Themes. The goal is to create a single
  stylesheet that can be set as the Application user-agent, allowing the styling of the entire app in every
  Window/Scene. Check javadocs for caveats!

### Changed

- Bump VirtualizedFX to version 11.9.4
- MFXIconButtonBehavior: button should still fire() if not selectable
- MFXSelectable, changeSelection(...) should not do anything by default
- MFXFab: SceneBuilder integration is not needed as it is already covered by the super class MFXButtonBase
- MFXTooltip, replace event handlers with a listener on the hover property
- MFXPopupSkin: since animations are reused, it is appropriate to use playFromStart() instead of play()
- Reviewed the theming API, this should be the final implementation. Added MaterialFX fonts as themes too, and also
  added the JavaFX default themes because...

### Fixed

- MFXTooltip: improve install for MFXControl and MFXLabeled components, make sure that the tooltip is also set on the
  mfxTooltipProperty()
- MFXTooltip: a nasty bug would leave the tooltip open when the mouse was not on the owner anymore. After a lot of
  stressful debugging I found the culprit, when the hover state changes to false, it's important to stop the 'delayer'
  animation

## [11.19.2] - 23-05-2023

### Changed

- Minor changes to some constructors. I think it's better to avoid calling the other constructors if a property is not
  needed to be initialized. JavaFX doesn't instantiate its properties until they are required
- MFXIconButton: make asToggle() an instance method with fluent API
- MFXButtonSkin: add null check for content display When
- MFXSegmentSkin: the content display When is not needed for this button
- MFXPlainContent and MFXRichContent replace Text with the new Label implementation offered by MFXCore

### Fixed

- MFXPopup and MFXTooltip: before calling getWindow() on the owner' scene, ensure that it is not null by wrapping the
  Window result in another Optional
- MFXPopup and MFXTooltip: it's not necessary to explicitly hide the popup if the owner's window has been closed before.
  Doing so would raise and exception

## [11.19.0] - 17-05-2023

### Added

- Implemented segmented buttons
- Implemented "helper" node to implement the concept of 'state layer' described by Material Design 3 guidelines, check
  MaterialSurface javadocs for more info
- Now MFXButton express its variants through the WithVariant API, added enumerator implementing Variant
- PseudoClasses: added new pseudo classes. Also added a method to check whether a pseudo class is currently active on a
  given Node

### Changed

- Completely reviewed the buttons class hierarchy. Now MFXButton extends from a common class for all kind of buttons
  MFXButtonBase.
  The consequences of this change are: 1) MFXSelectable now extends MFXButtonBase 2) MFXFabBase extends MFXButtonBase
  and thanks to generics it's not needed anymore to cast the behavior class 3) Behaviors have been reorganized as well.
  MFXButtonBehavior has become MFXButtonBehaviorBase and thanks to generics can be used as base for all kind of buttons
  that extend from MFXButtonBase.
- MFXIconButtonBehavior: most of the API has been moved to the base behavior for selectables MFXSelectableBehaviorBase
- MFXControl: use defaultLayoutStrategy() instead of LayoutStrategy.defaultStrategy() during property initialization
- MFXControl: forgot to override the defaultLayoutStrategy()
- MFXSelectable: since this now extends MFXButtonBase, the onAction property and fire() are not needed anymore
- MFXFabBase: no need for the getFabBehavior() method anymore as it now extends MFXButtonBase
- Components that have variants don't have static methods to set a variant anymore. Rather those methods have been
  made 'object methods', thanks to fluent API now the variant can be easily set after invoking the constructor, allowing
  for more customization in just one line
- Labeled components, that make use of skins of type MFXLabeledSkin, can now leverage a cache to retrieve the text sizes
  which updates only when the text or the font change, making layout computations much faster
- Components that need to visually distinguish between the various interaction states (hover, focus, press,...) now make
  use of the aforementioned MaterialSurface region

### Fixed

- MFXFabBehavior: fixed the icon switch animation for extended FABs as the width and label displacement were computed
  wrongly

## [11.8.0] - 07-05-2023

### Added

- Implementing Popups and Tooltips
- Add MFXTooltip support to both MFXControl and MFXLabeled
- Define API for animated popups, IMFXPopupSkin
- MFXPopup now supports the new anchor based show mechanism for Window owners too
- Implemented the two types of popup contents shown by M3 guidelines

### Changed

- Moved MFXPopupSkin to Skins Package
- MFXPopupBase: position computation methods have been slightly refactored to support the above case too
- MFXPopupRoot: make sure the animated property is also being initialized on creation
- MFXTooltip: allow changing owner
- MFXPopupSkin: slightly improved open animation
- MFXTooltip: use MOUSE_MOVED handler instead of MOUSE_ENTER for a more reliable interaction

## [11.7.0] - 03-05-2023

### Added

- Implemented IconButtons
- Implemented base classes for the new Selection API from core module. To be used with components such as checks,
  radios, switches,...
- MFXButtonBehavior: added key events handling. By default now, by pressing ENTER if the button is focused, the behavior
  will trigger the ripple generation at the center of the button, as well as firing an ActionEvent just like for mouse
  clicks
- MFXFabBase: added delegate methods for the icon property
- Added PseudoClasses for selection
- Added theme files for the new IconButtons

### Changed

- Minor refactor and improvements to the ButtonsPlayground demo
- MFXButtonBehavior: change ripple generation method to not take the generator as a parameter. Rather, try to get the
  generator instance from the button' skin and then cache it
- MFXFabBehavior: Improved change icon animation for extended FABs
- MFXButtonSkin and MFXFabSkin: adapt to behavior changes mentioned above

## [11.16.4] - 12-04-2023

### Fixed

- Emergency fix for components, the behavior was initialized twice

## [11.16.3] - 11-04-2023

### Changed

- Adapt to new When API from MFXCore

### Fixed

- Fixed a bug that lead to the behavior not being initialized in specific occasions, also added unit test
- Fixed label positioning for buttons

## [11.16.2] - 06-04-2023

### Fixed

- Added missing exports

## [11.16.1] - 05-04-2023

### Added

- Added purple dark theme variant to MFXThemeManager (experimental, theme needs adjustments in MFXResources, M3
  guidelines changed recently)
- Added tests to check that FABs' label is always positioned correctly even when changing text or icon
- Added tests to check that init sizes are correctly applied to small and large FABs, honestly the tests pass, but there
  may still be issues (dunno why though, my guess is that it depends on the container)

### Changed

- MFXFabBehavior: make getLabelNode() return an Optional and adapt code accordingly
- MFXFabBehavior: improve the label displacement computation, the targetWidth is only useful for the animation. To
  compute the displacement we actually want the bound size of the FAB
- MFXTonalFilledButton: change style class to "filled-tonal"
- MFXFab, MFXFabBase: turns out that managinfg the "extended" state is much easier with a PseudoClass rather than
  changing the style classes (edge cases may happen, better not handle the mess and just adding a PseudoClass)
- Create a base skin for all MFXLabeled controls, make MFXButtonSkin extend it, and adapt MFXFabSkin accordingly
- ButtonsPlayground: implement theme switching (preliminary)
- ButtonsPlayground: add extended FAB to test icon change animation

### Fixed

- MFXFabSkin: fixed misplacement of the label when its width changed (text or icon changes), in such case the
  displacement must be re-computed

## [Unreleased] - 14-03-2023

### Added

- Added specific behavior for FABs as shown by the M3 guidelines
- MFXLabeled: added property that allows to control the text opacity (see recent changes to BoundLabel in core module)
- Implement specific skin for FABs
- Added EXTENDED variant for FABs
- Added preliminary implementation of MFXThemeManager (atm just to simplify dev)
- Added a new class that is capable of generating CSS stylesheets via code and it's super useful. It even allows you to
  use multiple selectors and pseudo classes
- New API to allow MaterialFX controls to easily change their sizing/layout strategy by simply setting a property
  instead of creating custom skins, custom components or overriding methods inline
- Added tests for the new LayoutStrategy and MFXResizable APIs

### Changed

- MFXButtonBehavior: fire an action only on PRIMARY mouse clicks by default
- Moved SkinBase from core module and renamed it to MFXSkinBase
- MFXSkinBase now doesn't keep the reference to the current behavior object, instead it is stored in the component base
  class (MFXControl or MFXLabeled)
- MFXControl and MFXLabeled will now automatically handle behavior creation and initialization
- For the above change, they now enforce the use of buildSkin() to create the default skin, and make createDefaultSkin()
  a final method, this is needed for a reliable initialization of the behavior
- MFXControl and MFXLabeled now override all the size methods to be public
- MFXFabBase now has a property which allows to setup the FAB as a standard one or an extended one
- MFXFabBase: since now FABs have their own specific behavior, to avoid code duplication a method has been added to
  retrieve the FAB behavior reference, it returns an Optional since the behavior could be null or could not be an
  instance of MFXFabBehavior
- MFXButtonSkin: bind text node opacity to the new property of MFXLabeled
- MFXButtonSkin: no need for the listener on the behaviorProvider property (good for memory and performance)
- Make both MFXControl and MFXLabeled implement the new MFXResizable API for integration with LayoutStrategy
- MFXSkinBase: make size computation methods public for integration with MFXResizable and LayoutStrategy
- Reworked and renamed applyInitSizes(...) to onInitSizesChanged(). Init sizes now are taken into account by the new
  LayoutStrategy API
- MFXFabSkin: do not take into account the init width as it is used by the LayoutStrategy now
- MFXFabBehavior: moved extended variant management in the component class, behaviors should not change the component
  state
- WithVariants: added method to remove variants from a component

### Removed

- MFXExtendedFab has been removed to avoid code duplication

### Fixed

- Fixed critical bug related to the initWidth and initHeight new styleable properties. For some reason they were causing
  the CSS to be reapplied continuously, causing memory leaks and a huge performance hit over time. Override
  invalidated() instead on set(...)

## [11.16.0] - 09-02-2023

### Added

- Introduced two new styleable properties to both MFXControl and MFXLabeled, to allow specifying the initial sizes of a
  component without relying on JavaFX's CSS properties since once they are set, they cannot be overwritten (bug or not
  it's a shitty behavior)
- Introduced a new method to both MFXControl and MFXLabeled to allow implementations to customize the SceneBuilder
  integration
- Introduced FABs and Extended FABs
- Added two new theming APIs to define components' variants

### Changed

- MFXButtonSkin: listener on the graphic property is not needed, let the BoundLabel handle it
- MFXButtonSkin: do not apply the listener on the content display property if the button is of type MFXFab

### Fixed

- MFXElevatedButton: fixed NullPointerException caused by the elevation property as in some occasions both the oldValue
  and newValue can be null

## [11.15.2] - 07-02-2023

### Changed

- Rename materialfx package to mfxcomponents, for consistency with other modules
- Make buttons use the new SceneBuilder integration API mentioned above

## [11.15.1] - 02-02-2023

### Added

- Implemented all variants of MFXButton

### Changed

- Implemented MFXButtonBehavior
- MFXButtonSkin: add ripple effect, adapt to changes made in SkinBase

## [11.15.0] - 26-01-2023

### Added

- Preliminary implementation of components hierarchy, the idea is to build the components from scratch making them
  extend either MFXControl or MFXLabeled
- Preliminary implementation of MFXButton and MFXElevatedButton variant

