# <img src="ConvexMerger/assets/assets/logo/64.png" width="40"/> Convex Merger [![](https://img.shields.io/github/release/RoanH/ConvexMerger.svg)](https://github.com/RoanH/ConvexMerger/releases)
Convex Merger is an area maximisation game based on the idea of merging convex shapes. The goal of the game is to claim as large an area as possible while competing against your opponents that try to do the same. To do this you can either claim new objects for your own or merge already claimed objects with other objects. It is possible to play the game with up to 4 players of which some can be AIs. It is also possible to play online multiplayer

[Jump directly to downloads](#downloads)

## Example Game
A short example game between two AIs is shown below.
![example game](https://i.imgur.com/48W3hwE.gif)

## Rules
The goal of the game is to maximise the area of the playfield you own by claiming and merging objects into new convex objects. In every turn you can do the following:
1. Click an unowned object to claim it for yourself.
2. Click an object you already own and then select a second object either owned by you or unowned. If there are no objects on what will become the boundary of the new convex object the merge will succeed. Objects fully contained in the newly created convex object will be stolen from their current owner.

The game ends when the players whose turn it is has no possible moves available. You can visualise merging two objects as spanning an elastic band around both objects, the resulting shape is the new convex object. A merge of two objects is shown below:    
![merge](https://i.imgur.com/6ofU8Ys.gif)

## Playfield Options
When generating the playfield you can control the object size, density and spacing. The exact function of these parameters is as follows:
- **Object size**: Controls the size of the playfield objects.
- **Density**: Controls how much of the playfield is initially covered by objects.
- **Spacing**: Controls how much space there is between the objects. The spacing also affects the object size, with a larger spacing resulting in smaller objects.

## Downloads
_Requires Java 8 or higher_    
- [Windows executable](https://github.com/RoanH/ConvexMerger/releases/download/v1.0/ConvexMerger-v1.0.exe)    
- [Runnable Java Archive](https://github.com/RoanH/ConvexMerger/releases/download/v1.0/ConvexMerger-v1.0.jar)

All releases: [releases](https://github.com/RoanH/ConvexMerger/releases)    
GitHub repository: [here](https://github.com/RoanH/ConvexMerger)

## Online Multiplayer
The game has built in support for playing online multiplayer. Here one player will act as the host and all other plays will connect to this host. If the host and all other players are on the same local network (e.g. WiFi) then players can connect to the host using the local IPv4 address of the host. If you want to play with remote players, then it is required for the host to portforward port 11111 and players can then connect using the external IP of the host. Please make sure you know what you are doing if you set this up and only play with people you trust.

## Credits
- [Roan](https://github.com/RoanH): Game Design & Implementation
- [RockRoller](https://github.com/RockRoller01): UI Design & Logo
- [Thiam-Wai](https://github.com/CTW121): Playfield Generation
- [Emiliyan](https://github.com/Kroasana): Vertical Decomposition
- [Phosphor Icons](https://phosphoricons.com/): UI Icons
- [Cadson Demak](https://fonts.google.com/specimen/Pridi): Pridi Font

## Development
This is an [Eclipse](https://www.eclipse.org/) + [Gradle](https://gradle.org/) project with [Util](https://github.com/RoanH/Util) as the only dependency. Development work can be done using the Eclipse IDE (already setup) or using any other Gradle compatible IDE (manual setup). CI will check that all source files use Unix style line endings (LF) and that all functions and fields have valid documentation.

## History
Project development started: 20th of November, 2021.    
Project due date: 30th of January, 2022.
