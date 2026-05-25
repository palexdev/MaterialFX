## 25.6.4 - 25/05/2026 - 8a46ead5

### Refactoring

- <1c118c59> InsetsBuilder: fix oversight



## 25.6.3 - 25/05/2026 - 0b0a3309

### Refactoring

- <58625458> Review, move and rename InsetsBuilder utility



## 25.6.2 - 09/04/2026 - d68e6202

### Refactoring

- <d6f51108> KeyStroke: separate visual representation from actual key codes because modifiers are OS dependent
- <901bf93e> MFXMenuItem, MFXCheckMenuItem: do not run the action if the item is disabled



## 25.6.1 - 09/04/2026 - 978316b5

### Refactoring

- <19c7a1de> KeyStroke: return an unmodifiable set of modifiers, do not use + separator for Mac modifiers
- <f51aa49f> KeyModifier: handle SHORTCUT key, use symbols for Mac modifiers, reorder constants according to conventions



## 25.6.0 - 08/04/2026 - 6986acd6

### Features

- <9c69ffb8> MFXMenu: allow filtering mouse events
- <57f303f7> MFXMenu: allow retrieving all items from a menu by flattening the tree structure
- <8822b122> KeyStroke: add keyStroke(KeyCode...) factory method
- <4794a6c1> KeyModifier: add reverse resolution KeyCode -> KeyModifier
- <8dd2d05c> Implement ShortcutManager to make KeyStrokes handling automatic/less of a hassle
- <c8b7a76c> MFXMenu: allow show/hide via key events

### Bug Fixes

- <fb248847> MFXMenuItem: remove superfluous null check on the action which would break MFXCheckMenuItem behavior

### Refactoring

- <79206ca3> MFXMenuItem: hide menu when action is run only if it's available
- <c87adb7c> MFXMenuItem: restrict setMenu(...) visibility
- <aaab91a2> ShortcutManager: use MFXMenu.getAllItems and integrate into MFXMenu
- <0f3fb04c> MFXMenu: minor changes to sub menu creation and handling
- <0c42ae8e> KeyStroke: rename of(String) factory to fromString(String)
- <b750b8f8> MFXMenuItem: make runAction method public in behavior
- <f4ba8c8b> KeyMap: use plain event handlers instead of WhenEvent to avoid potential memory leaks
- <51cba3de> WhenEvent: minor cleanup
- <677f2a07> MFXMenu: improve focus handling

### Tests

- <15680427> When, WhenEvent: add GC tests



## 25.5.0 - 01/04/2026 - 4e4abed0

### Refactoring

- <05b869d5> Selectable: add convenience onSelected method and change onSelectionChanged to throw UnsupportedOperationException by default
- <f5da99bd> Major review of SelectionGroup



## 25.4.1 - 26-03-2026 - b4688c15

### Refactoring

- <05cb3d2f> MFXMenu: add missing delegate method to update root's stylesheets
- <516d4465> MFXMenu: properly propagate config to sub-menus and also propagate styleable parent config if not present
- <0e9fc948> MFXMenu: close if already showing when using anchor-based positioning
- <84209f78> MFXCheckMenuItem: allow specifying whether to close the menu when the action is run
- <823a278f> MFXCheckMenuItem: fix exceptions when selection property is bound, add hook for selection changes, set `:selected` pseudo-class on item accordingly
- <15c42b63> Split MenuBuilder adding CheckMenuBuilder for check menu items



## 25.4.0 - 01-03-2026 - 0e776ec6

### Removed

- <d820ac53> MFXMenu: revert 55e85a2c as the option was not doing anything and cannot be easily implemented the right way

### Features

- <a2aef59f> Implement selectable menu items as MFXCheckMenuItem
- <8b96f6b6> Major refactor of MFXMenu items, now they are concrete nodes
- <2ddb800a> StyleUtils: add method to initialize styleable properties without locking them (classic JavaFX shenanigans, :facepalm:)
- <55e85a2c> MFXMenu: allow preloading content's skin if necessary (rarely)

### Bug Fixes

- <56249d83> MFXMenu: properly handle animation
- <92c5bd89> MFXPopover: show peer at the given x,y coordinates even if the position is set afterwards otherwise the popup might flicker
- <b5077511> KeyModifier: add missing toLowerCase()
- <4ba48587> PopupRoot: retrieve stylesheets upon construction

### Refactoring

- <2e722268> MFXMenu: propagate animation to submenus for convenience and consistency
- <4a5c76e3> MFXMenu: do not allow null contents
- <8b1888a8> Implement column-like alignment for menu icons too
- <8372675b> Text: minor changes to constructors
- <f7f4adf5> MFXMenu: change default submenus factory for a bit of offset and default placement
- <8af4e8fa> MFXPopover: use When.observe to reset translate properties (another JavaFX shenanigan btw :facepalm:)
- <66de84c8> MFXMenuContentSkin and MFXMenuEntry: mimic typical grid layout of menus, where the trailing nodes (either shortcuts or submenu icons) are aligned to the right and leading text aligned to the left. Also, honor padding during layout
- <6a44280b> MFXMenuEntry: move input handling to behavior class

### Documentation

- <12d97f92> Selectable: clarify API docs
- <b2076cf2> Fix javadocs

### Style

- <ed42d640> MFXMenuItem: extend and improve default stylying



## 25.3.2 - 28-01-2026 - adbfaf6a

### Refactoring

- <2d4a41ef> AbstractDragResizer: do not reset cursor on mouse release but rather check zone and update accordingly



## 25.3.2 - 12-10-2025 - 8d3e2311

### Misc

- <8275b5fe> Forgot export for notifications package



## 25.3.1 - 12-10-2025 - 83a8adcf

### Features

- <b5ceca97> Add an additional special state for MFXPopups to indicate if the popup is being auto-hidden by internal mechanisms



## 25.3.0 - 09-10-2025 - 80d05dc1

### Features

- <40aa8f29> MFXDialog: added convenience methods to enable movement and resizing of the window
- <c38bf31e> Implement WindowMover utility (for headless windows)

### Refactoring

- <d6f4cbe6> MFXPopup: move setContent logic to PopupRoot
- <3f28e6fd> MFXDialog: add root's bounds to the lock in place listener
- <e474a544> MFXPopups: remove constructors with style classes and make them more consistent across implementations

### Misc

- <b41f6b75> Move some utilities to the right package



## 25.2.0 - 08-10-2025 - e163a84b

### Features

- <3cc51c21> ISelectionModel: add selectAll convenience method
- <66f7b8d2> MFXDialog: add some delegate methods to the peer window

### Bug Fixes

- <17793693> MFXDialog: forgot code to hide the backdrop
- <0f8f495f> MFXTooltip: fix tooltip never closing because of flawed logic in hover check

### Refactoring

- <829f0cb3> SelectionModel: clamp IntegerRanges
- <ad134681> Minor refactors to the validation API, mainly for convenience
- <6f072288> Simplify the popups' positioning API while still maintaining the great flexibility it offers
- <452e7c8c> PopupRoot: do not set a style class as every popup implementation overrides it anyway, use the class as a selector for the reset CSS
- <b873b059> MFXPopups: extend styling capabilities through the `styleable parent` to all kinds of popups
- <11e0503b> MFXDialog: init the peer's owner on show
- <d77cd085> EventBusNetwork: return a map of active buses rather than a key set

### Tests

- <c65dff0d> Update/add tests



## 24.5.0 - 07-09-2025 - 87f73d00

### Features

- <b79f9a68> PseudoClasses: add OPEN pseudo class
- <bd133a10> Selectable: add convenience toggle() method to quickly flip the selection state
- <fb10bfba> Add a bunch of convenience methods to ISelectionModel and related classes
- <5161ce60> MFXLabeled: backport text opacity CSS property
- <e56e5553> Selectable: add optional selection changed callback
- <35104818> "Relax" MaterialFX components hierarchy by not passing the behavior and skin types as class generics

### Bug Fixes

- <60cb6f13> MFXPopups: remove the onState() method from the builder because you would lose the reference to the When construct used to listen to the state property, leading to a potential memory leak
- <14051523> SelectionProperty: make the invalidated() method final because it was a source of errors due to forgetting to call super when overriding. Introduce onInvalidated() method for safer
  overrides

### Refactoring

- <cf0534f1> Review SelectionModel
- <5d5c2455> MFXSkinBase: clear the behavior when the skin is disposed
- <51ae88c5> MFXStyleable: extend from Styleable and add documentation
- <6b939667> MFXSkinBase replace getControlAs() with overridable getControl() method
- <230001f6> MFXBehavior: migrate callback from Consumer to Runnable
- <35e5bd45> MFXSkinnable relax bounds to allow for MFXLabeled
- <6c3f7dee> MFXBehavior remove generics from register(...) and add javadocs back
- <000dd87d> Make MFXControl and MFXLabeled implement MFXStyleable
- <b8d65908> SkinBase: rename initBehavior() to registerBehavior() and make it no-arg
- <303cbbda> BehaviorBase minor refactor
- <91b915f2> WhenEvent: rename process() to handle()
- <5c9f5bec> Rename KeyShortcut to KeyStroke and optimize fromEvent(KeyEvent)

### Documentation

- <6203a957> Fix javadocs

### Tests

- <70b5fe1d> Update tests

### Misc

- <da5ef303> Renamed Control, Labeled and SkinBase to MFXControl, MFXLabeled and MFXSkinBase respectively
- <525f22de> Renamed BehaviorBase to MFXBehavior
- <61d9d3fb> Move WhenEvent to input package

## 24.4.0 - 30-08-2025 - 80336482

### Removed

- <ee5eca54> Nuke custom bindings

### Features

- <ac53687c> Implement a simple notification system
- <d52519b5> Implement EventBusNetwork, a way to group multiple event buses under one "network", and reference specific ones via a simple tag system
- <177c8567> Implement dialogs capable of computing and returning a certain result
- <36281580> MFXDialog: implement showAndWait() functionality which can be enabled through the configuration
- <2d19045c> MFXPopup: allow setting content through supplier, convenient to create and configure nodes "on the go"
- <f3b220c7> Subscriber: allow composition and priority override through convenience methods
- <8c9619cc> EventBus: add APIs to clear subscriptions and check if the bus currently has subscribers
- <90edc0ec> Backport and improve selection models
- <623e117b> When: add new utility
- <a31bb21e> Implement a new utility for bidirectional bindings
- <46c93523> Add a bunch of utilities for collections

### Bug Fixes

- <8f72ecc3> NumberUtils: rewrite mapping utilities and prevent NaN/Infinite results
- <ae363947> OnChanged and OnInvalidated override the executeNow(...) method for constructs with prebuilt listeners, since there is no action there it would cause a NullPointerException

### Refactoring

- <3ef229ed> MFXMenu: move from protected constructor to a public factory to create submenus.
- <44278dc2> NoOpSelectionModel: avoid potential NullPointerExceptions
- <09231a43> SelectionEventHandler: remove selection state parameter as not needed
- <702d833c> MFXMenuItem: never null children list (avoids null checks everywhere)
- <645b707c> MFXMenuItem: remove withers and implement some sort of DSL to build menus; also rename subMenuItems to children
- <be6ca6d0> MappedBidirectionalBinding: implement Disposable
- <9e5945ec> MFXDialog: redirect hide events from external sources (like the OS) to our own hide logic (allows to always play animations for example)
- <2f579a2f> MFXDialog: use new When.observe utility for lock in place functionality
- <e206eeda> MFXDialog: allow setting modality until the dialog is shown the first time
- <849b7ace> MFXDialog: use default config like other popups
- <d8704c3c> SimpleEventBus: remove empty queues automatically
- <6496c128> SimpleEventBus: use prebuilt comparator for all buses
- <9f9269d1> PositionProperty and SizeProperty make use of PropUtils to build styleable properties
- <f6413efb> Reworked PropUtils to use the Builder pattern for more advanced configurations (maintains backwards compatibility!)
- <3840668e> SynchronizedProperties: do not use MFXBindins anymore
- <30e48f23> When: allow registering invalidating sources when the construct is already active

### Documentation

- <08c08fca> Add documentation for selection models

### Tests

- <46db4a05> Update tests

### Misc

- <d4e94354> Move PseudoClasses from components to core
- <0e34235c> Move CubicCurve to utils and make it public
- <15f6b05e> Rename DisposableAction to Disposable and move to base package
- <228b21cb> Update module-info.java
- <98e93a2a> Rename IEventBus to EventBus
- <51315a3b> Moved WeakLinkedHashMap to the 'collections' package



## 24.3.0 - 31-07-2025 - 10f296ba

### Features

- <34794612> Allow specifying the action to perform when styleable properties generated by PositionProperty and SizeProperty become invalid
- <91a99f3e> Finally found a workaround to the shitty JavaFX CSS system allowing to set sizes and positions without the need to wrap the values in quotes
- <f247481c> Memoizer: implement caching supplier
- <48e10a2e> MFXMenuItem: allow disabling items via JavaFX boolean expressions
- <01da43c2> MFXMenuContent: implement placeholder capabilities
- <2b20546b> Add skins preload capabilities

### Bug Fixes

- <33631fb0> MFXTooltip: fix out timer duration
- <85fa070a> MFXPopup: avoid exception when setting a null content
- <88690beb> MFXPopover: revert JavaFX bullshit

### Refactoring

- <9dffd0d0> MFXMenuEntry: add surface Region for interaction states
- <53d37523> MFXMenuContent: unwrap entries by removing the VBox and doing manual layout, add custom spacing property
- <0e57b8a8> Minor refactor of SubMenuHandler and move out of MFXMenuEntry. This is to allow custom implementations of MFXMenuContent
- <ac423906> MFXMenu: make the hoveredItem property a fake read-only. This is to allow custom implementations of MFXMenuContent
- <8394b977> MFXPopups: allow setting MFXMenu content

### Documentation

- <b2fe7641> Fix javadoc warnings

### Tests

- <86872794> Update tests



## 24.2.0 - 17-07-2025 - 535406ed

### Features

- <a009b2f3> MFXMenu: implement navigation with arrow keys
- <142e9946> Implement my own menus API
- <60784802> Implement my own Key input API
- <28259c10> Backport OSUtils from another project of mine
- <a84470fa> Add convenience configuration methods to MFXPopups
- <1dd748df> Remade AnchorHandlers for maximum flexibility

### Bug Fixes

- <8ab203d8> Apply reset stylesheet to all popovers
- <4604213b> MFXTooltip: do not call hide from the TooltipTracker property if the new value is the same as the old one
- <40404c53> MFXTooltip: reset the tracker in the peer's hide method to catch auto-hide calls
- <eb28a340> AnchorHandlers: fix ADJACENT_INWARDS positioning mode for BOTTOM anchors
- <a51d82f0> MFXPopover: resetting styles on root inline prevents the values from being overridden. Apply a CSSFragment (stylesheet) instead

### Refactoring

- <201e8001> Make MFXMenuContent a Control to allow custom implementations (will implement virtualized menu content in components module)
- <65d4c1da> MFXMenuCell: override pref sizes rather than min
- <d7036b72> Add MFXMenu to MFXPopups builder
- <e9bd5b15> MFXMenu: improve key, navigation and focus handling
- <541a29e6> MFXPopup: expose the root as a generic Parent node
- <a28c4d18> MFXPopups: make config builders build the default config
- <75205521> MFXTooltip: throw error if tooltip is already installed
- <2a15a253> MFXPopups: fix configure(...) methods
- <a1301782> MFXTooltip: reorder some properties and remove local config as not necessary
- <42a02009> WhenEvent: add asFilter(boolean) convenience method
- <4a00dd60> Adapt popups to AnchorHandlers changes and add Alignment parameter to anchor-based positioning
- <1fce5d8a> MFXPopups: use Position for offsetting rather than Insets because it's more intuitive and easy to use



## 24.1.0 - 03-07-2025 - cc69b56a

### Features

- <712ebf4e> Add PivotUtils
- <c666ff4b> Add Corner enumeration
- <e5033d8b> MFXPopups: add onState(...) method
- <fb310d3a> CSSFragment: add appendSelector(...) method
- <68f3dbb2> AnchorHandlers: add ADJACENT_INWARDS position mode and make it default for popovers (technically for Node owners)
- <29fbbc4d> Implement convenience/facade API for easily show any kind of MFXPopup
- <52da6cb3> Add builder methods starting from given config to all MFXPopups
- <dd125b8e> Store and allow retrieval of applied config on MFXPopups
- <f9713573> Add Peer interface for MFXPopups' peers
- <49ecf06c> Add reposition mechanism to all MFXPopups and lock in place feature to MFXDialog
- <6c5ee750> Add always on top options for MFXDialogs
- <55f5068a> Add animation capabilities to MFXPopups
- <fb2397a8> MFXPopup: add convenience method to add a listener on the state property
- <995ff1a5> Add offset capabilities to MFXPopups
- <5193d452> Implemented MFXTooltip
- <ce663e20> Implemented MFXPopover
- <63e96298> Implemented MFXDialog
- <b6880422> Backport MFXBackdrop from effects module
- <9c9bb4c1> Implement position computation utility
- <9ee17cbe> Implementing the MFXPopup API
- <8b2931b7> CSSFragment: add method to append PseudoClasses
- <4f01f2cf> Implement measurement cache specifically for Labels
- <c16b298b> Complete review of Label
- <33e0fae0> Reviewed Size, Position and relative properties, as well as adding units support for StyleableSizeProperty and StyleablePositionProperty
- <b3e5df13> Replace TransformableLists with RefineList
- <3f99633e> Introduce the MFXSkinnable API, making the MVC pattern strategy more consistent
- <127d5b94> SkinBase: add convenience method to cast skinnable for skins that deal with subclasses of a control
- <82c34c4a> CSSFragment: add convenience method for transition duration
- <ecc63110> Backport MFXStyleable into this module with a few more utilities

### Bug Fixes

- <fd7c4d41> MFXPopover: set method was still overridden by CSS, use inline style to remove background and padding
- <10872c6c> AnchorHandler: fix positioning of dialogs in owner windows by using the owner's content bounds rather than the window bounds
- <6b7a92f9> MFXPopover: for some reason sometimes the state property is not set to HIDDEN, preventing it from showing. Fixed by adding a one-shot listener on the peer's showing property
- <65a6bb67> Label: fix text node detection and bind fontSmoothing
- <b880cda2> Styleable: fix UnsupportedOperationException

### Refactoring

- <82b5d0dd> MFXPopups: use root node for computing the position
- <bbe8edad> MFXDialog: bring to front if already showing
- <f44a735b> MFXPopover redirect auto-hide events to MFXPopup hide logic
- <72f0af1c> Label: do nothing when 'disableTruncation' is activated and the text Node is still null
- <3c705a2a> WithBehavior: make some methods default
- <fdf7e42f> CSSFragment: select class simple name if styleclasses list is empty
- <0e687a84> Label: change to onSetTextNode to a BiConsumer accepting the old text node too
- <aac2e612> Control and Labeled: add init() method to avoid boilerplate code
- <3e60831e> SkinBase: remove size computations shortcuts (it complicated in a way custom skin development) and make initBehavior() not abstract to avoid boilerplate code
- <dd44fc63> Selectable: make some methods defaults and add an optional isSelectable() method

### Documentation

- <bf8f94c3> Label: update documentation