![](https://img.shields.io/badge/targets-Android%2FJVM-informational)
![](https://img.shields.io/github/v/release/Woody230/GW2Manager)
![](https://img.shields.io/github/license/Woody230/GW2Manager)

<a href='https://play.google.com/store/apps/details?id=com.bselzer.gw2.manager.android'>
    <img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width='250'/>
</a>

# Pack Yak

Currently focused on [World vs. World](https://wiki.guildwars2.com/wiki/World_versus_World), the manager can be used to observe state changes in the match of a designated
world. As such, the name of the app comes from the use of [dolyaks](https://wiki.guildwars2.com/wiki/Pack_Dolyak) to supply objectives.

The Android version is currently the dedicated platform for this application. 

A desktop version (via JVM and for Windows only) is able to be ran but there are multiple problems that still need to be resolved: 
* Tailoring the UI to better fit desktop and large screen sizes.
* Support of [MapCompose](https://github.com/p-lr/MapCompose/issues/1) with [compose-jb](https://github.com/JetBrains/compose-jb).
* [Crash](https://github.com/JetBrains/compose-jb/issues/1111) when opening a dialog that is using a lazy column.

Most backend logic within this project is hidden behind by the [GW2Wrapper](https://github.com/Woody230/GW2Wrapper) project.

## Pre-v1.0.0 Test Run

[2021-12-10](https://www.youtube.com/watch?v=9VRWnw46moY) using an in-progress version of the app