<!--TODO WIP-->

## Table of Contents

* [Introduction](#introduction)
    * [What is Sass and Why?](#what-is-sass-and-why)
    * [Old System vs New System](#old-system-vs-new-system)
* [Organization](#organization)
    * [The 7-1 Pattern](#the-7-1-pattern)
    * [Themes Definition](#themes-definition)
    * [Components Styling](#components-styling)
    * [How is the System intended to work?](#how-is-the-system-intended-to-work)
* [Performance](#performance)
* [Setup](#setup)

## Introduction

## What is Sass and Why?

Sass (Syntactically Awesome Style Sheets) is a preprocessor scripting language that is interpreted or compiled into CSS.
SassScript is the scripting language itself. Sass helps keep large stylesheets well-organized and makes it easy to share
design within and across projects. It provides several advanced features such as variables, nesting, mixins,
inheritance, and functions, which enhance the capabilities of plain CSS and promote better code management.
In this system, we use the SCSS syntax, a popular and widely-used syntax for Sass that is fully compatible with CSS,
making it easier to integrate into existing projects.
By allowing you to write cleaner and more structured stylesheets, Sass can significantly streamline your development
workflow and improve the maintainability of your CSS code.

Thanks to its advanced features, Sass makes it possible to extend the capabilities of JavaFX CSS since it uses a very
limited subset of it. With Sass, you can create more dynamic and reusable styles while still generating stylesheets that
are fully compatible with the JavaFX CSS system.  
Not only that, Sass is quite useful for the recent transition of MaterialFX to global User Agent themes.  
It allows us to still have a modular system (one SCSS file for each component),
which is easier to manage, while being able to easily assemble all of them in a single CSS file.

## Old System vs New System

I discovered CSS preprocessors only 'recently', specifically Sass. I really liked its features and capabilities, and so
I took some notes on it and started playing with it, which eventually led to the old theming system.  
That system had two main issues from my perspective:

1) It felt like I was not using Sass features enough. Declaring variables somewhere and then using them elsewhere felt
   kinda lame. Sass has so much to offer: mixins, functions, collections!  
   Although, I have to say that there isn't really a standard in Sass on _how to do things properly._
   It really depends on the context.
2) One day I asked myself a bunch of questions:  
   'What if in the future I want to implement other themes besides Material Design?'  
   'Can the current system handle it?'  
   'How would I go about it?'  
   The immediate answer to the second and third questions were: 'No' and 'I have no idea'.

So, the goals of the new system are: keep and improve the modular structure, make more use of Sass features and more
importantly, make the system more flexible.  
However, it's important to state that all of this comes at the cost of a noticeably increased complexity.

## Organization

## The 7-1 Pattern

As I already mentioned previously, there is not really a way to _do things properly_ in Sass.  
However, this project uses a very popular set of guidelines written by Kitty Giraudel which you can read
[here](https://sass-guidelin.es/).  
Among the many good paragraphs, there is one in particular that talks about architecture, code organization.  
The [7-1 Pattern](https://sass-guidelin.es/#the-7-1-pattern) simply organizes the various aspects of the system in seven
directories.

1) `abstracts`: contains helpers and tools that can be useful project-wise
2) `base`: contains boilerplate code, reset files, typography rules
3) `components`: contains partials for styling specific 'widgets' (buttons, toggles, lists, etc...)
4) `themes`: contains anything related to the actual themes. Meaning it can contain partials that hold common variables,
   tools for a specific design, but also SCSS files that are actually compiled in CSS themes

- Note 1: this project only needs the aforementioned four directories
- Note 2: since components and themes can get quite complex, these two directories can have subdirectories for a better
  organization. Components that have one or more variants are usually placed in a subdirectory, and then a SCSS file
  ending in `-all.scss` is placed in the top-level directory. Each design system/color scheme actually has its own
  subdirectory in `themes`. For example, material themes and all the various accents live under `themes\material`.

## Themes Definition

Assembling the various modules in a single CSS theme can be more challenging than it seems in Sass, because of its
[Module System](https://sass-lang.com/blog/the-module-system-is-launched/), which more often than not it's just a huge
pain in the ass. Perhaps it's just a skill issue 🤷🏻.

The old system allowed defining a theme's `dark` variant very easily as shown by the following pseudocode:

```scss
/* In the 'light' variant SCSS file */
@forward 'theme' with (
// Set $variant to 'light'
// Define $palette and $scheme
);
@use 'theme' as Theme;

// Expose tokens
// Import fonts
// Import components

/* In the 'dark' variant SCSS file */
@forward 'my-theme-light' with (
  // Set $variant to 'dark'
);
```

That's it, three lines of code were enough to define the dark variant. **Important**: this is possible with
Material Design themes and likely not with other designs. This is because of the amazing work done by the Google team.
Their documentation is simply outstanding, telling you exactly how to build the scheme from the palette,
how to apply every token, how to and how not to use the components, and much much more.
(Yes, this is also intended to be a little rant, I'd like one day to port themes like Nord, Catppuccin and such,
but their documentation is either crap or non-existent!)

The goal for the new system is to maintain this simplicity, but as a consequence of the reviewed architecture, the
themes definition process has also changed a little bit. Here's the pseudocode:

```scss
/* In the 'light' variant SCSS file */
// Define variant as $scheme-variant
// Define palette as $colorname-palette
@use 'md-theme' as MDTheme;
@use 'abstracts/variables' with (
  // Assign $scheme-variant to $variant
  // Assign $colorname-palette to $palette
  // Generate the $scheme with MDTheme.GenerateScheme($colorname-palette, $variant)
);
@use 'theme' as Theme;

// Expose tokens
// Import fonts
// Import components

/* In the 'dark' variant SCSS file */
@forward 'my-theme-light' with (
  // Set $scheme-variant to 'dark'
);
```

As you can see it's a bit more laborious, blame the module system, this was the best flow I could come up with.
On the plus side, defining dark variants is still very easy, no code duplication whatsoever.

The `theme` module offer services and tools that can be used by any design system, like retrieving palette and scheme
colors, apply rules and expose the tokens in the `root` element in a way that is compatible with JavaFX CSS system
(this is important to state, in fact, only color tokens can be exposed!).  
Specific design systems then can define their specific services and tools, like in the above examples, the `md-theme`
module is capable of generating the Material Design 3 color scheme given the palette and variant.

## Components Styling

How components are styles is the thing that changed the most in the new system, and it's the most complex part of it
because of the flexibility it tries to offer.  
The idea is to make heavy use of `maps` and `mixins`, here's how they work in the system:

#### Maps

To be precise, the system makes use of _nested maps_.
Each key of the top-level map is another map and corresponds to a `Node`.
When the rules have to be applied to the component itself the key is named `root`,
otherwise the name of its children is used (not enforced, just a guideline).  
Inside each sub-map there can be rules, but also other maps. These are used to specify the node's style when one or more
states/pseudo-classes are active.

Last but not least, the system tries to make writing rules less verbose and more pleasant, because you see,
there is a little issue with our environment. Not only JavaFX uses a very limited subset of CSS, but also
all properties are prefixed with `-fx` (
See [Mozilla](https://developer.mozilla.org/en-US/docs/Glossary/Vendor_Prefix)).  
To achieve such goals, the system allows writing 'shortened' rules which are then automatically mapped at compile time.

- **Note 1:** to make this possible, the system stores the rules into two maps. The first one contains JavaFX
  properties,
  some of them may be missing at the time of writing this, I'll expand the map as soon as I encounter new properties.
  The other one contains custom properties, mostly from MaterialFX.
- **Note 2:** to make this mapping system more flexible, in case a property is missing, it's possible to use it anyway
  specifying the pair `[property: value]` as follows: `name: (-custom-prop, value)`.
  The system will automatically unwrap the list.

#### Mixins

Mixins in Sass are a powerful feature that allows you to create reusable chunks of CSS.
A mixin can contain any number of CSS declarations, and you can include them in your stylesheets wherever needed.
They help in reducing redundancy and improving the maintainability of your code by keeping it DRY.
Mixins can also accept arguments, enabling you to pass values to the mixin to customize its output,
making them even more flexible and powerful.

This system uses mixins to apply the styles defined in the aforementioned maps to the components. So, while maps define
how the component will look like, mixins' goal is to work with the component's structure.  
For a better comprehension, let's consider a button which contains an icon. I want the icon's color to change when
the button is pressed. Here's what the styles map could look like:

```scss
$styles: (
  icon: (
    color: blue,
    pressed: (
      color: red,
    ),
  ),
);
```

See the little inconvenience? The `pressed` state is inside the icon map because the styles are to be applied to the
icon.
However, the state refers to the button node. The mixins must take care of this, applying the styles as follows:

```scss
@mixin StyleIt() {
  .button (
    > .icon {
      @include ApplyStyles($styles, icon);
    }
  
    // When the button is pressed (not the icon!)
    &:pressed > .icon {
      @include ApplyStyles($styles, icon, selected);
    }
  )
};
```  

`ApplyStyles` is a simple mixin that takes as input the styles map and the path for the styles we want to apply as a
varargs list. Once the path is resolved, a simple for-loop will apply all the styles defined in the desired sub-map.
This operation will also execute the mapping for the shortened and custom properties.

Last but not least, mixins actually do much more with maps:

1) They always accept a map as input (optional!) which can be used to override the predefined styles.
2) Before actually applying the styles, all maps (base styles, component's specific styles, overrides) are merged into
   a single map with `map.deep-merge` which is then passed to the `ApplyStyles` mixin.

## How is the system intended to work?

_Q: 'Ok, that's a lot to process, care to explain how this much complexity actually increases flexibility in practice?'_

Yes, I do realize how hard it can be to understand the system, but I also believe it is not that intricate and a simple
example can help to shed some light on it. Before diving into some code, keep in mind these two core concepts:

1) Maps define the styles, how the component will look
2) Mixins apply them appropriately, according to the component's structure

As usual, let's take a simple button as our example subject.  
Let's first define its structure: the _button_ itself is a container for the _label_ and possibly an _icon_, hierarchy:

- Button (root)
    - Label
        - Icon (yes, the label is also a container for the icon)

In CSS this hierarchy is simply translated as: ` .button > .label > .icon`.

Now let's assume we want three styles for our button:

1) Rounded rectangle, blu background, white text and icon.
2) Same as 1 but, yellow background, blue text and icon.
3) Transparent background, purple border, black text and icon. On hover, purple background, white text and icon

```scss
// Since all buttons have the same shape/layout we can define a common map for these properties
$base-styles: (
  root: (
    bg-radius: 10px,
    bd-radius: 10px,
    pref-width: 140px,
    pref-height: 50px,
  ),
  icon: (
    size: 16px,
  ),
) !default;

// Now let's define the styles for variant 1
$styles-1: (
  root: (
    bg-color: blue,
  ),
  label: (
    fg-color: white, // (fg-color and text-fill can be used interchangeably)
  ),
  icon: (
    mfx-color: white,
  ),
) !default;

// Here's the mixin for variant 1
@mixin StyleItV1($overrides: ()) {
  $styles: DeepMerge($base-styles, $styles-1, $overrides);
  
  .button {
    @include ApplyStyles($styles, root);
    
    > .label {
        @include ApplyStyles($styles, label);
      > .icon {
          @include ApplyStyles($styles, icon);
      }
    }
  }
}

// Here's the mixin for variant 2
// Since V2 is very similar to V1 we can simply call StyleItV2 with the right overrides
@mixin StyleItV2($overrides: ()) {
  $v1-overrides: (
    root: (
      bg-color: yellow,
    ),
    label: (
      fg-color: blue,
    ),
    icon: (
      mfx-color: blue,
    ),
  );
  @include StyleItV1(DeepMerge($v1-overrides, $overrides)); // We still have to deep merge the two overrides!
}

// V3 is different, we could probably reuse StyleItV1 but the most efficient way is to make separate maps and mixin
// When I say efficient I mean a smaller compiled CSS, but maybe this example is not perfect to show the performance
// implications...
$styles-3: (
  root: (
    bd-color: purple,
    hover: (
      bg-color: purple,
    ),
    label: (
      hover: (
        fg-color: white,
      ),
    ),
    icon: (
      hover: (
        mfx-color: white,
      ),
    ),
  ),
) !default;

// Here's the mixin for variant 3
@mixin StyleItV3($overrides: ()) {
  $styles: DeepMerge($base-styles, $styles-3, $overrides);
  
  .button {
    @include ApplyStyles($styles, root);
    
    // Hover state
    &:hover {
      @include ApplyStyles($styles, root, hover);
      
      > .label {
        @include ApplyStyles($styles, label, hover);
      }
      
      > .icon {
        @include ApplyStyles($styles, icon, hover);
      }
    }
  }
}

// Now depending on what we want in our theme we can simply import the button's module...
@use 'buttons' as Buttons;
//...and include the desired style
@include Buttons.StyleItV1();
//@include Buttons.StyleItV2();
//@include Buttons.StyleItV3();
```  

**A few notes on the above examples**

1) I wanted to show how maps are merged, but technically since the base styles are the same for all variants, I could
   have also made a `StyleItBase` mixin. Of course, this is not always possible; it really depends on the context.
2) The `DeepMerge` function is simply a custom-made function that uses Sass' `map.deep-merge`, but it can take more than
   two maps as input. They are merged in sequence.
3) As said in point 1, context is all. Of course, when you know what you want to achieve, it's easier to optimize the
   code. My system, however, is made to work in any context. It's hard to know how different designs style their
   components, especially considering the lack of documentation. The system is made to be flexible, easy to adapt.
   Optimization and performance are less important.

## Performance

Although I haven't conducted formal benchmarks or direct comparisons, it is clear that the new system is less performant
in terms of compile-time efficiency. The extensive use of maps, merges, and loops undoubtedly adds complexity and
overhead to the compilation process.

However, the resulting CSS is identical to what was produced previously, ensuring that the runtime performance of
applications remains unchanged. Since my system uses Sass solely to generate static themes, the impact of
compile-time performance is negligible. In essence, while the new system may take longer to compile,
it does not affect the end-user experience or the application's runtime efficiency.

## Setup

In this section, I'm going to briefly describe what are the requirements to compile Sass themes as well as additional
operation performed on the generated CSS themes.

First of all you need Sass, here's how you can get it:

1) Using **npm** (Recommended)  
   `npm install -g sass`
2) Using **Chocolatey** (for Windows)  
   `choco install sass`
3) Using **brew** (for macOS)  
   `brew install sass/sass/sass`
4) Download the standalone version [here](https://github.com/sass/dart-sass/releases)

Now can compile Sass themes with the following command:  
`sass --no-source-map theme_path/theme_name.scss output_path/output_name.css`  
Here's a concrete example:  
`sass --no-source-map themes/material/md-indigo-light.scss themes/material/md-indigo-light.css`

I don't really like the output of `sass` sometimes, and for this reason themes here needs to be post-processed.  
Two tools are used:

1) [clean-css-cli](https://www.npmjs.com/package/clean-css-cli): responsible for optimizing the generated CSS file and
   for the first stage of beautification.  
   You can install it with this command:  
   `npm install -g clean-css-cli`  
   And here's how you use it:  
   `cleancss -O1 'all:on;specialComments:all' --format 'beautify' input_css_file -o output_css_file`
2) [cssbeautify-cli](https://www.npmjs.com/package/@palexdev/cssbeautify-cli): responsible for further beautifying the
   output of `cleancss`. Note that for some reason it's important to tell `cleancss` to perform the formatting,
   otherwise
   the processing done by this command will not produce the expected result.  
   You can install it with this command:  
   `npm install -g @palexedev/cssbeautify-cli`  
   And here's how you use it:  
   `cssbeautify-cli -i "  " -f input_css_file -w output_css_file`

To make the process less tedious, I made a `bash` script that automatically performs the above actions.  
Here's how you can use it:  
`./process_theme.sh --compile input_scss_theme output_css_file`  
Here's a concrete example:  
`./process_theme.sh -c themes/material/md-indigo-light.scss themes/material/md-indigo-light.css`

_(`-c` and `--compile` are the same option)_