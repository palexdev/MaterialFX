[![HitCount](http://hits.dwyl.com/PAlex404/MaterialFX.svg)](http://hits.dwyl.com/PAlex404/MaterialFX)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/palexdev/materialfx/Java%20CI%20with%20Gradle?label=github%20build&style=flat-square)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.palexdev/materialfx?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)
[![javadoc](https://javadoc.io/badge2/io.github.palexdev/materialfx/javadoc.svg?logo=java)](https://javadoc.io/doc/io.github.palexdev/materialfx)
![GitHub issues](https://img.shields.io/github/issues-raw/palexdev/materialfx?style=flat-square)
![GitHub pull requests](https://img.shields.io/github/issues-pr/palexdev/materialfx?style=flat-square)
![GitHub](https://img.shields.io/github/license/palexdev/materialfx?style=flat-square)
---

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/palexdev/MaterialFX">
    <img src=https://imgur.com/7NdnoFl.png" alt="Logo">
  </a>
</p>


<h3 align="center">MaterialFX</h3>

<p align="center">
    MaterialFX is an open source Java library which provides material design components for JavaFX
    <br />
    <a href="https://github.com/palexdev/MaterialFX/wiki"><strong>Explore the wiki »</strong></a>
    <br />
    <br />
    <a href="https://github.com/palexdev/MaterialFX/releases">Download Latest Demo</a>
    ·
    <a href="https://github.com/palexdev/MaterialFX/issues">Report Bug</a>
    ·
    <a href="https://github.com/palexdev/MaterialFX/issues">Request Feature</a>
</p>

<!-- TABLE OF CONTENTS -->

## Table of Contents

* [Important Notes!](#important-notes)
* [About the Project and History of JavaFX](#about-the-project-and-history-of-javafx)
* [About the Logo](#about-the-logo)
* [Some GIFs](#preview-gifs)
* [Getting Started](#getting-started)
    * [Build](#build)
    * [Usage](#usage)
        * [Gradle](#gradle)
        * [Maven](#maven)
* [Documentation](#documentation)
* [Changelog](#changelog)
* [Roadmap](#roadmap)
* [Theming System](#theming-system)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Donation](#donation)
* [Supporters](#supporters)

<!-- IMPORTANT NOTES -->

## Important notes

Please, **before** using this library and submitting an issue complaining that controls are **not styled and bugged** check how the **stilying system has changed** since
version 11.14.0

<!-- ABOUT THE PROJECT -->

## About The Project and History of JavaFX

JavaFX is a software platform intended to replace Swing in creating and delivering rich client applications that operate
consistently across diverse platforms. With the release of JDK 11 in 2018, Oracle has made JavaFX part of the OpenJDK
under the OpenJFX project in order to increase the pace of its development.

Key features:

- FXML and SceneBuilder, A designer can code in FXML or use JavaFX Scene Builder to interactively design the graphical
  user interface (GUI). Scene Builder generates FXML markup that can be ported to an IDE where a developer can add the
  business logic.
- Built-in UI controls and CSS, JavaFX provides all the major UI controls required to develop a full-featured
  application. Components can be skinned with standard Web technologies such as CSS.
- Self-contained application deployment model. Self-contained application packages have all the application resources
  and a private copy of the Java and JavaFX runtimes. They are distributed as native installable packages and provide
  the same installation and launch experience as native applications for that operating system. JavaFX is a software
  platform for creating and delivering desktop applications, as well as rich Internet applications (RIAs) that can run
  across a wide variety of devices.

Over the years the way of creating GUIs has often changed and JavaFX default appearance is still pretty much the same.
That's where this project comes in. The aim of my project is to bring components which follow as much as possible the
Google's material design guidelines to JavaFX. The second purpose is to provide a successor to the already
available [JFoenix](https://github.com/jfoenixadmin/JFoenix) library, which is a bit old and has a lot of issues.

In recent months the project has evolved a lot, to the point that it is no longer a simple substitute.  
To date MaterialFX offers not only restyled controls, but also: new and unique controls such as the Stepper,
controls completely redone from scratch such as ComboBoxes or TableViews (and many others),
and many utilities for JavaFX and Java (NodeUtils, ColorUtils, StringUtils ...).

<!-- ABOUT THE PROJECT -->

## About The Logo

MaterialFX v11.13.0 brought a lot of fixes and new features, but it also brought a new logo, something that is more
meaningful for me and that somewhat represents the new version.  
The new logo is a Phoenix, the immortal bird from Greek mythology, associated to regeneration/rebirth.
When a Phoenix dies it obtains new life by raising from its ashes.  
MaterialFX v11.13.0 fixed many critical bugs and broken features, I like to think that it is reborn from
the previous version, so I thought a new logo would have been a good idea.

<!-- PREVIEW GIFS -->

## Preview GIFs

#### Imgur Link: [Gallery](https://imgur.com/a/IrDirnI)

<i>
<details>
<summary>Buttons</summary>
<br>
<img src="https://imgur.com/jATdGFL.gif" alt="Buttons" border="0">
</details>
<p></p>

<details>
<summary>Check Boxes, Radio Buttons and Toggles</summary>
<br>
<img src="https://imgur.com/ArUhH58.gif" alt="Checkboxes" border="0">
</details>
<p></p>

<details>
<summary>Combo Boxes</summary>
<br>
<img src="https://imgur.com/BO0twpA.gif" alt="Comboboxes" border="0">
</details>
<p></p>

<details>
<summary>Dialogs</summary>
<br>
<img src="https://imgur.com/LsxGeJh.gif" alt="Dialogs" border="0">
</details>

<p></p>
<details>
<summary>Fields</summary>
<br>
<img src="https://imgur.com/XT2iVU7.gif" alt="Fields" border="0">
</details>
<p></p>

<details>
<summary>Lists</summary>
<br>
<img src="https://imgur.com/4Ckdn5z.gif" alt="Listviews" border="0">
</details>
<p></p>

<details>
<summary>Notifications</summary>
<br>
<img src="https://imgur.com/lgex2yO.gif" alt="Notifications" border="0">
</details>
<p></p>

<details>
<summary>Pickers</summary>
<br>
<img src="https://imgur.com/J3v3i9w.gif" alt="Pickers" border="0">
</details>
<p></p>

<details>
<summary>Progress</summary>
<br>
<img src="https://imgur.com/2E6X3uJ.gif" alt="Progress" border="0">
</details>
<p></p>

<details>
<summary>Scroll Panes</summary>
<br>
<img src="https://imgur.com/8Jxu3TM.gif" alt="Scrollpanes" border="0">
</details>
<p></p>

<details>
<summary>Sliders</summary>
<br>
<img src="https://imgur.com/nOrsa1n.gif" alt="Sliders" border="0">
</details>
<p></p>


<details>
<summary>Stepper</summary>
<br>
<img src="https://imgur.com/nEgV9F1.gif" alt="Stepper" border="0">
</details>
<p></p>

<details>
<summary>Tables</summary>
<br>
<img src="https://imgur.com/nj6xhUT.gif" alt="Tableviews" border="0">
</details>
<p></p>
</i>

<!-- GETTING STARTED -->

## Getting Started

In this section you can learn what do you need to use my library in your project or see a preview/demo which I'm
planning to release as runtime images here on github.

### Build

To build MaterialFX, execute the following command:

    gradlew build

To run the main demo, execute the following command:

    gradlew run

**NOTE**: MaterialFX requires **Java 11** and above.

### Usage

###### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.palexdev:materialfx:11.16.0'
}
```

###### Maven

```xml

<dependency>
    <groupId>io.github.palexdev</groupId>
    <artifactId>materialfx</artifactId>
    <version>11.16.0</version>
</dependency>
```

<!-- DOCUMENTATION -->

## Documentation

You can read MaterialFX's documentation at [javadoc.io](https://javadoc.io/doc/io.github.palexdev/materialfx)

<!-- CHANGELOG -->

## Changelog

See the [CHANGELOG](https://github.com/palexdev/MaterialFX/blob/main/CHANGELOG.md) file for a list of changes per
version.

<!-- ROADMAP -->

## Roadmap

See the [Open Issues](https://github.com/palexdev/MaterialFX/issues) for a list of proposed features (and known issues)
.  
See the [ROADMAP](https://github.com/palexdev/MaterialFX/blob/main/ROADMAP.md) for a list of implemented and upcoming
features.

<!-- THEMING SYSTEM -->

## Theming System

Since MaterialFX 11.14.0 the way controls are styles through CSS has drastically changed. Before telling you about the
new
Theming System, and about its pros and cons, let's talk a bit on the history of this project, the causes that brought to
this drastic change.

When I started developing MaterialFX I was a complete noob, I knew nothing about JavaFX. But I really wanted to use it
and
to make it look good. Competitors had broken libraries that made usage difficult for the end user, I didn't like them at
all.  
And so the journey begun, MaterialFX is born. Like any newbies, what do you do when you know nothing but want to
learn?  
You check others work and try to copy them but still make the changes you want to implement.  
This lead me to create controls that made use of the infamous `getUserAgentStylesheet()` method. For those of you that
do not
know about it, a developer of custom controls is supposed to override that method to provide a CSS stylesheet to define
the
author intended style for the custom control.  
Sounds great right, just the thing I need... Well, I'd say that if only it worked properly. This system has been the
root
cause of CSS issues right from the start of the project, with little I could do to fix it **properly**.  
_(Little secret that almost no one know: I actually sent a PR on the JavaFX repo to improve the system and make it
dynamic,
guess what, it's still there lol)_

The two most annoying issues caused by this system are:

1) The little buggers didn't think of nested custom controls. For example, if a custom control(parent) has a skin that
   uses other
   custom controls(children), the user agent of the parent will be **completely ignored** by the children, the result is
   a
   bunch of children that **cannot** be styled in any way unless you create a custom skin yourself. A fix I implemented
   for this
   in the past, was to override inline the `getUserAgentStylesheet()` method of each children node to use the one of the
   parent,
   and even this drastic solution was working half the time (didn't work in some user cases)
2) For some reason, sometimes stylesheets provided by the user were half or completely **ignored** leading to half/not
   styled(as intended) custom controls. This was the most annoying issue, as the causes would vary from case to case,
   not always
   there was a easy/feasible solution, a nightmare, really

**End of the rant**  
How can I fix it? I asked myself many many times.  
Recently I've been working on a rewrite of [MaterialFX](https://github.com/palexdec/MaterialFX/tree/rewrite), this new
version will have controls based on the new Material Design 3 Guidelines, will implement modular themes thanks to the
usage
of [SASS](https://sass-lang.com/) and a Theming API that will let user change themes, implement new ones, very easily.  
So the idea is to backport at least the concept on the main branch at least until the rewrite is done.

**The Theme API**  
An interface called `Theme` allows users to define custom themes entries. It defines the bare minimum for a theme,
its path and a way to load it.  
There are two implementations of this interface:

1) `Themes`: this enumerator defines the default themes of MaterialFX, there is the `DEFAULT` theme that includes the
   stylesheets
   of all MaterialFX controls, as well as dialogs, popups, menus, etc...
2) `Stylesheets`: this enumerator defines all the stylesheets of every single control, allowing the user to not use a
   theme
   (for whatever reason) and instead choose which component he wants to style

`MFXThemeManager` is a utility class that will help the user add/set themes and stylesheets (which implement `Theme`) on
nodes or scenes.

**Pros**

- The biggest pro is to have a more reliable styling system. With this users shouldn't hava any issue anymore while
  styling
  MaterialFX controls with their custom stylesheets. Of course, I consider the system _experimental_, I don't expect to
  not have even a single report about CSS bugs, but they should be way less and much easier to fix
- Another pro is to have less code duplication as now I don't need to override the infamous `getUserAgentStylesheet()`
  anymore anywhere
- This change should have also impacted on memory usage in a good way as now controls do not store the "url" to their
  stylesheet anymore

**Cons**

- One con is that now themes must be managed by the user. Since controls are not styled by default, the user must
  use the aforementioned manager or enumerators to load/add the themes on the App.  
  The preferred way to do so would be to add the themes/stylesheets on the root scene, like this:
  ```java
  public class App extends Application {
  
    @Override
    public void start(Stage stage) {
     ...
     Scene scene = ...;
     MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
    }
  }
  ```
- Another con that derives from the above example are dialogs/popups or any separate stage/scene.
  Since you are applying the themes on the primary stage's scene, it means that all other scenes will be un-styled.
  **You have to add the Themes on every separate scene**.
  To simplify things, MaterialFX automatically applies the Themes on its dialogs and popups, but since now they
  are added to the `getStylesheets()` list it's easy to remove them and define your own
- ~~The last con I can think of is SceneBuilder. As of now there is no support for it, I have some ideas on how to style
  controls inside of it though. The issue is that even if I figure out a way,~~ I doubt the system will be flexible
  enough.
  ~~What I mean is, I can probably set the default themes on the SceneBuilder' scene,~~ but it's very unlikely there
  will
  be a way to choose which themes/stylesheets will be applied.  
  Since version 11.15.0, MaterialFX controls are capable of detecting if they are being used in SceneBuilder and can
  automatically
  style themselves. From my little testings, it seems that this doesn't break the styling system in any way, I was able
  to style a button
  by adding a custom stylesheet on itself or on its parent. There's also an emergency system to completely shut down the
  SceneBuilder integration, more info
  here: [Themable](https://github.com/palexdev/MaterialFX/blob/main/materialfx/src/main/java/io/github/palexdev/materialfx/controls/base/Themable.java)

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->

## License

Distributed under the GNU LGPLv3 License. See `LICENSE` for more information.

<!-- CONTACT -->

## Contact

Alex - alessandro.parisi406@gmail.com  
[![Discord](https://img.shields.io/discord/771702793378988054?label=Discord&style=flat-square)](https://discord.com/invite/zFa93NE)
<br /><br />
Project Link: [https://github.com/palexdev/MaterialFX](https://github.com/palexdev/MaterialFX)

<!-- DONATION -->

#### Donation

It's been more than a year since I started developing MaterialFX. Implementing cool looking, fully functional controls,
introducing new components and features as well as providing many utilities for JavaFX and Java is really hard,
especially considering that developing for JavaFX also means to deal with its closeness, its bugs, its annoying
design decisions. Many times I've honestly been on the verge of giving up because sometimes it's really too much
stress to handle.  
**But**, today MaterialFX is a great library, supported by many people and I'm proud of it.
If you are using MaterialFX in your projects and feel like it, I recently
activated [GitHub Sponsors](https://github.com/sponsors/palexdev) so
you can easily donate/sponsor.

<!-- SUPPORTERS -->

# Supporters:

(If you want your github page to be linked here and you didn't specify your username in the donation, feel free to
contact me by email and tell me. Also contact me if for some some reason you don't want to be listed here)

- Alaa Abu Zidan
- Alex Hawk
- Aloento
- Mauro de Wit
- Mohammad Chaudhry (thank you very much for the huge donation, YOU are the legend)
- Jtpatato21
- Sourabh Bhat
- stefanofornari (thank you very much for the big donation!)
- Ultraviolet-Ninja
- Yahia Rehab
- Yiding He
- *Your name can be here by supporting me at this link, [GitHub Sponsors](https://github.com/sponsors/palexdev)*

Thank you very very much to all supporters, to all people who contribute to the project, to all people that thanked me,
you really made my day
