[![HitCount](http://hits.dwyl.com/PAlex404/MaterialFX.svg)](http://hits.dwyl.com/PAlex404/MaterialFX)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/palexdev/materialfx/Java%20CI%20with%20Gradle?label=github%20build&style=flat-square)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.palexdev/materialfx?server=https%3A%2F%2Foss.sonatype.org&style=flat-square)
![GitHub issues](https://img.shields.io/github/issues-raw/palexdev/materialfx?style=flat-square)
![GitHub pull requests](https://img.shields.io/github/issues-pr/palexdev/materialfx?style=flat-square)
![GitHub](https://img.shields.io/github/license/palexdev/materialfx?style=flat-square)
---

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/palexdev/MaterialFX">
    <img src=https://imgur.com/qBOvrWD.png" alt="Logo" width="720">
  </a>
</p>


<h3 align="center">MaterialFX</h3>

<p align="center">
    MaterialFX is an open source Java library which provides material components for JavaFX
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

* [About the Project and History of JavaFX](#about-the-project-and-history-of-javafx)
* [Some GIFs](#preview-gifs)
* [Getting Started](#getting-started)
    * [Build](#build)
    * [Usage](#usage)
        * [Gradle](#gradle)
        * [Maven](#maven)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Donation](#donation)
* [Supporters](#supporters)

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
To date MaterialFX offers not only restyled controls, but also: new and unique controls such as the Stepper, controls completely redone from scratch such as ComboBoxes or TableViews (and many others), and many utilities for JavaFX and Java (NodeUtils, ColorUtils, StringUtils ...).

<!-- PREVIEW GIFS -->

## Preview GIFs

#### Imgur Link: [Gallery](https://gifyu.com/album/cU3)

<i>
<details>
<summary>Buttons</summary>
<br>
<img src="https://s6.gifyu.com/images/Buttons.gif" alt="Buttons" border="0">
</details>
<p></p>

<details>
<summary>CheckBoxes</summary>
<br>
<img src="https://s6.gifyu.com/images/Checkboxes.gif" alt="Checkboxes" border="0">
</details>
<p></p>

<details>
<summary>ComboBoxes</summary>
<br>
<img src="https://s6.gifyu.com/images/Comboboxes.gif" alt="Comboboxes" border="0">
</details>
<p></p>

<details>
<summary>DatePickers</summary>
<br>
<img src="https://s6.gifyu.com/images/Datepickers.gif" alt="Datepickers" border="0">
</details>
<p></p>

<details>
<summary>Dialogs</summary>
<br>
<img src="https://s6.gifyu.com/images/Dialogs843efeb715dfd031.gif" alt="Dialogs" border="0">
</details>
<p></p>

<details>
<summary>Labels</summary>
<br>
<img src="https://s6.gifyu.com/images/Labels.gif" alt="Labels" border="0">
</details>
<p></p>

<details>
<summary>ListViews</summary>
<br>
<img src="https://s6.gifyu.com/images/Listviews.gif" alt="Listviews" border="0">
</details>
<p></p>

<details>
<summary>Notifications</summary>
<br>
<img src="https://s6.gifyu.com/images/Notifications.gif" alt="Notifications" border="0">
</details>
<p></p>

<details>
<summary>ProgressBars</summary>
<br>
<img src="https://s6.gifyu.com/images/Progressbars.gif" alt="Progressbars" border="0">
</details>
<p></p>

<details>
<summary>ProgressSpinners</summary>
<br>
<img src="https://s6.gifyu.com/images/Progressspinners.gif" alt="Progressspinners" border="0">
</details>
<p></p>

<details>
<summary>RadioButtons</summary>
<br>
<img src="https://s6.gifyu.com/images/Radiobuttons.gif" alt="Radiobuttons" border="0">
</details>
<p></p>

<details>
<summary>ScrollPanes</summary>
<br>
<img src="https://s6.gifyu.com/images/Scrollpanes.gif" alt="Scrollpanes" border="0">
</details>
<p></p>

<details>
<summary>Sliders</summary>
<br>
<img src="https://s6.gifyu.com/images/Sliders.gif" alt="Sliders" border="0">
</details>
<p></p>


<details>
<summary>Stepper</summary>
<br>
<img src="https://s6.gifyu.com/images/Stepper.gif" alt="Stepper" border="0">
</details>
<p></p>

<details>
<summary>TableViews</summary>
<br>
<img src="https://s6.gifyu.com/images/Tableviews.gif" alt="Tableviews" border="0">
</details>
<p></p>

<details>
<summary>TextFields</summary>
<br>
<img src="https://s6.gifyu.com/images/Textfields.gif" alt="Textfields" border="0">
</details>
<p></p>

<details>
<summary>Toggles</summary>
<br>
<img src="https://s6.gifyu.com/images/Toggles.gif" alt="Toggles" border="0">
</details>
<p></p>

<details>
<summary>FontResources</summary>
<br>
<img src="https://s6.gifyu.com/images/Fontresources.gif" alt="Radiobuttons" border="0">
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

**NOTE** : MaterialFX requires **Java 11** and above.

### Usage

###### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
implementation 'io.github.palexdev:materialfx:11.11.1'
}
```

###### Maven

```xml
<dependency>
  <groupId>io.github.palexdev</groupId>
  <artifactId>materialfx</artifactId>
  <version>11.11.1</version>
</dependency>
```

<!-- ROADMAP -->

## Roadmap

See the [open issues](https://github.com/palexdev/MaterialFX/issues) for a list of proposed features (and known issues).  
See the [ROADMAP](https://github.com/palexdev/MaterialFX/blob/main/ROADMAP.md) for a list of implemented and upcoming features.

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

Ever since I was a kid I have always liked programming, I find it interesting and often funny too, however it can also
be a difficult and stressful job at times. This is my first public project, and I'm dedicating a lot of time to it. This
is an open source library of course and everyone can use it for free, but if you feel like it you can make a small
donation here. [![Donate](https://img.shields.io/badge/$-support-green.svg?style=flat-square)](https://bit.ly/31XB8zD)

<!-- SUPPORTERS -->

# Supporters:
(If you want your github page to be linked here and you didn't specify your username in the donation, feel free to contact me by email and tell me. Also contact me if for some some reason you don't want to be listed here)

- Alex Hawk
- Yiding He
- Alaa Abu Zidan
- *Your name can be here by supporting me at this link, [Support](https://bit.ly/31XB8zD)*

Thank you very very much to all supporters, to all people who contribute to the project, to all people that thanked me, you really made my day
