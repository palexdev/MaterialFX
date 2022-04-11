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

## [11.13.5] - 11-04-2022

## Changed

- MFXCSSBridge: do not take into account the Region's user agent stylesheet. If it's needed to specify the user agent
  the popup's getUserAgentStylesheet() method should be overridden inline, as well as for any other component that mey
  require it (see MFXComboBoxSkin as an example) (Fix for #173)
- Adds a method to retrieve the selection values as a List (rather than getSelection().values() which returns a generic
  Collection). Also make MultipleSelectionManager use LinkedHashSet and LinkedHashMap (TreeMap was used, oversight
  sorry) to keep insertion order for selection, this also ensures that building the values List returns the selected
  values in the same exact order as in the selection Map (Enhancement for #161)

## Fixed

- MFXTextField: fixed TextFormatter not working. It must be added on the BoundTextField, for this reason, added a
  delegate property (Fix for #174)
- MFXComboBoxSkin, MFXFilterComboBoxSkin: fixed an issue that prevented the combo box's popup from being fully
  customizable with CSS (Fix for #173)
- MFXDatePickerSkin: initialize the picker's text if the initial date is not null (Fix for #172)

## [11.13.4] - 31-03-2022

### Fixed

- Fixed Maven POM
- Fixed MFXTextField bug which was causing improper text fill in specific occasions

## [11.13.3] - 10-03-2022

### Added

- MFXTextField: added a label to specify the unit of measure (optional, leave blank string to remove)

### Changed

- Update Gradle plugins
- Update VirtualizedFX to 11.2.5
- Improve ROADMAP

## [11.13.2] - 09-02-2022

### Added

- New control MFXMagnifierPane
- ColorUtils: added some new methods to convert Colors to Strings
- FunctionalStringConverter: added two new convenience methods
- New utils class SwingFXUtils (copied from javafx.embed.swing)
- Added fluent API builders for MaterialFX components and JavaFX Panes, as requested by #78
- Added resource bundles and API for internationalization
- Added new control, MFXSpinner

### Changed

- ColorUtils: changed some method to be null-safe
- MFXFilterPaneSkin: properly compute the minimum width
- MFXTableViewSkin: allow to drag the filter dialog
- MFXIconWrapper: added handler to acquire focus

### Fixed

- MFXComboBoxSkin: ensure the caret position is at 0 if the combo box is not selectable
- MFXTableViewSkin: ensure the dialog is on foreground
- MFXTextField and all subclasses: fixed an issue with CSS and :focused PseudoClass. It was being ignored in some cases,
  probably because the inner TextField was stealing the focus to the actual control. To fix this we use a new
  PseudoClass ":focus-within" to specify that the inner field is focused, so the control should be considered focused as
  well
- I18N: do not use URLClassLoader to load the ResourceBundles as using MaterialFX is other projects would lead to a
  MissingResourceException, instead change the bundle base name returned by getBundleBaseName() with the complete path
  to the bundles
- BoundTextField: remove text formatter binding as it was causing an exception, also it works anyway, so it was
  unnecessary
- MFXIconWrapper: fix NullPointerException when creating an empty wrapper
- ListChangeProcessor: fix computeRemoval() method returning negative indexes
- ListChangeProcessor: fix findShift() method by including the given index in the count too

## [11.13.0] - 22-01-2022

_This version won't follow the above scheme as the amount of changes and commits is simply too huge and there would be
no way to correctly show all the changes without making mistakes (duplicates, "overlapping" changes...), for this reason
I'll try to sum up only the major changes below._

- The demo has been completely remade
- Added new beans and properties
- Added new mechanisms for bindings
- Added new collections, in particular an ObservableList that combines the capabilities of JavaFX's FilteredList and
  SortedList. Also those two are read-only, but MaterialFX also offers a version that allows to directly make changes to
  the source list
- ReactFX, Flowless removed in favor of my own Virtual Flow implementation, VirtualizedFX. As a result all controls
  having lists have been reworked.
- The table view has been reworked as well to use a Virtual Flow (scrollable) so it's efficiency is now on a whole new
  level. There's also a paginated version of the table (like before) but it still uses a Virtual Flow, which of course
  makes it efficient
- MFXLabels have been deprecated and removed as now MaterialFX follows Material Design's text fields. The new
  MFXTextFields are the best thing you're gonna see on JavaFX. They have all the features defined by Material Design
  principles and also more. They now offer a floating label that can have 4 states: disabled, above, border, inline. Oh,
  they can also be set to behave like Labels, no need to have duplicate controls (Label and TextFields), since they
  offer the same functionalities duh.
- Many controls (such as combo boxes, date pickers) now extends MFXTextField, so they inherit all its features.
- Almost all controls have been reviewed/remade to make them fully functional (there were a lot of issues with CSS not
  working properly).
- The Filter API has been reviewed and now it's more powerful than ever with the new MFXFilterPane.
- The Validation API has been reviewed as well, it is as powerful as before thanks to JavaFX properties and observables,
  but it's much more flexible. It's up to the user now to decide when and how to validate a control. Also an important
  design choice has been made here. Many validation frameworks for JavaFX also offer a way to decorate a control, but I
  decided to not to that as it would violate the Single Responsibility Principle! Validation has nothing to do with UI,
  plus depending on the fanciness of your App it's up to you to decide how the validation controls will look like!
- Dialogs and Notifications have been reviewed as well. The dialogs have been simplified, and for notifications there
  are now two separate systems.
- The date picker is now on a whole new level, it's been remade from scratch and it's simply beautiful, powerful and
  versatile.
- There are also new components! MFXPopup is a PopupControl that actually works. Ever tried to style a PopupControl but
  no matter what the CSS would not work? Do not worry about that never again, just use MFXPopup it's super easy thanks
  to my custom MFXCSSBridge (check documentation would be too much to write here haha). MFXPagination has been made for
  MFXPaginatedTableViews, remade from scratch (meaning that doesn't extend Pagination) with a stunning modern look.
  MFXTooltip, an alternative to JavaFX's tooltip, much more versatile!
- MFXRippleGenerator has been deprecated. The ripple generation is organized to be a new API, meaning that there are now
  interfaces and a base abstract class from which you can implement new ripple generators. The new default
  implementation is MFXCircleRippleGenerator. The new API also allows you to create new Ripples by implementing the
  IRipple interface. It's a rather advanced API tbh, but hey, it's there, who knows maybe someday I'll need it to be
  like this.
- MFXHLoader and MFXVLoader are no more. The loading API has been "extracted" to be independent from UI. MFXLoader has
  the same capabilities as the aforementioned controls but it's not a Node. It's up to the user to decide how to manage
  the loaded views, and how to translate the loaded beans to a Node. (see the documentation and the DemoController for
  an example on how to easily create a nav-bar even with the new API)
- The Selection API has been reviewed as well. It also supports the "extend selection" behavior when "Shift" is pressed,
  like you would expect from a file manager.
- There is an insane amount of new utilities, for JavaFX as well as for Java

Again, let me **apologize** for this messy changelist, but I promise from next version changes will be tracked properly!














