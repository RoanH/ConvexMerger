# <img src="ConvexMerger/assets/assets/logo/64.png" width="40"/> Convex Merger [![](https://img.shields.io/github/release/RoanH/ConvexMerger.svg)](https://github.com/RoanH/ConvexMerger/releases)
Convex Merger is an area maximisation game based on the idea of merging convex shapes. The goal of the game is to claim as large an area as possible while competing against your opponents that try to do the same. To do this you can either claim new objects for your own or merge already claimed objects with other objects.

It is possible to play the game with up to 4 players of which some can be AIs. It is also possible to play online multiplayer

[Jump directly to downloads](#downloads)

## Example Game
A short example game between two AIs is shown below.
![example game](https://i.imgur.com/48W3hwE.gif)


### Task Assignment
Given the feedback we've decided to switch our main algorithm from a convex hull computation algorithm to a vertical decomposition computation algorithm. Hence the new ask distribution is as follows:
- Emiliyan: Vertical Decompositon
- Thiam-Wai: Playfield generation
- Roan: General game logic (everything without a comment mentioning an assignee)

## Downloads
_Requires Java 8 or higher_    
_Tested operating systems: Mac 10.11.6 & M1, Ubuntu Linux 16.04 LTS, Windows 7 & 8 & 10 & 11_    
- [Windows executable](https://github.com/RoanH/KeysPerSecond/releases/download/v8.5/KeysPerSecond-v8.5.exe)    
- [Runnable Java Archive](https://github.com/RoanH/KeysPerSecond/releases/download/v8.5/KeysPerSecond-v8.5.jar)

All releases: [releases](https://github.com/RoanH/KeysPerSecond/releases)    
GitHub repository: [here](https://github.com/RoanH/KeysPerSecond)    

### Credits


### Development
This is an [Eclipse](https://www.eclipse.org/) + [Gradle](https://gradle.org/) project. Development work can be done using the Eclipse IDE (already setup) or using any other Gradle compatible IDE (manual setup). CI will check that all source files use Unix style line endings (LF) and that all functions and fields have valid documentation. Compiled binaries can be downloaded from the [CI/CD pipelines tab](https://gitlab.com/RoanH/convexmerger/-/pipelines).

## History
Project development started: 20th of November, 2021.    
Project due date: 30th of January, 2022.
