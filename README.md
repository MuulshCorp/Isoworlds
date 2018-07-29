## Isoworlds
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
###### Supports SpongeAPI 5/6/7 - Bukkit 1.7.10

### Description
Isoworlds is a large scale personal worlds manager originally designed for Isolonice servers to increase and control performances. Instead of one big laggy world which needs protection, everyone get his own and can add/remove anyone to his whitelist. 
 Griefs / banned items decreases, possibilities are huge as you can set specific option for one or many worlds. 
 
 Each world is a copy of a pattern so everyone can get the same world (preventing generation lag) and some modpack like In The Sea got their own generated map, so you can now offer those modpack to your community !

(Using Tick Dynamic for every Isoworlds to set them with X% of ms could be done)

### Command
| Command        | Permission           | Description  |
| ------------- |:-------------:| -----:|
| `/iw`      | Isoworlds.default | Main command, open a gui with all features |

### Why Isoworlds
- Prevents grief
- Less banned items for grief
- The same world no longer welcomes all players
- Avoids loading unused bases or the need to reset the map
- All gamerules can be applied each Isoworld
- Latency is easier to target
- In case of crash, you can move an Isoworld to inspect it
- You can give the Isoworlds folder to the player who owns it
- Templates: Random generation or pre-generated world, when Isoworlds create a world for a player it only copy a region folder. You can build anything or add items in a chest anywhere, all your players will get the same

### How it works
All players can own their own world. Each world has a name: "Player UUID" + "-Isoworld". 

- Storage system: If enabled, a tag system is present to tell the plugin if the world is present (region folder) or not on the server. The interest is not to store all Isoworlds on the game server so as not to run out of disk space. Indeed, an Isoworld can make from 50 to 300mo easily. It all depends on the size you suit. Every minute, a control system searches for inactive worlds (without player in) for 15 minutes by default (to be defined in the configuration file). If a world is inactive for 15 minutes, it is unloaded from the server and a tag is added (@PUSH), then the script takes over.

| Tag        | Folder name | Description  |
| ------------- | ------------- | ------------- |
| `@PUSH` | `uuid-Isoworld@PUSH` | If a world has been inactive for 15 minutes, preparing for push process |
| `@PUSHED` | `uuid-Isoworld@PUSHED` | If a region folder of the world has been pushed on the remote server, not anymore on world folder |
| `@PUSHED@PULL` | `uuid-Isoworld@PUSHED@PULL` | A player wants to go to this Isoworld, preparing for pull process |
| `No tag` | `uuid-Isoworld` | When world is ready, we set the it to his original name  |

###### To handle remote storage use this kind of system: https://github.com/Isolonice/Isoworlds-SAS

### Bukkit and Sponge differences
- The sponge plugin adds a unique ID to each Isoworld database, but also in the level.dat to avoid data loss with certain modded objects (cables...)

- The bukkit plugin does not have a system that moves the Isoworlds to another folder when the server starts to avoid loading them by other plugins, mods. That hasn't been necessary so far.
- World Border plugin is needed

### Features
- Time: Set time to day or night
- Weather: Set weather on sunny, rainy or stormy for 10 minutes, 30 minutes or 1 hour
- Biome: Change biome of chunks (8 actually)
- Trust system: Add, remove, list access (teleport to trusted Isoworlds)
- Automatic dimensions: Mining and exploration worlds available via the warp menu
- Fall protection if spawn point is empty or dangerous, by placing a dirt at 0,60,0 or above water/lava.
- Nether & End platform system (to prevent death)
- Border system based on permission: 3 sizes (Small, Medium, Large)
- Playtime
- UUID based
- Map templates (Normal, Void, Ocean, Flat)
- Charge system (consumption system)
- Cooldown system
- Storage: Move Isoworld folders on restart and automatic unload to the desired destination
- Automatic unload of unused Isoworld (counter)
- Automatic reset for Nether/End/Mining/Exploration
- French / English translation
- Prevent loading at start by moving Isoworld folders (mods like Astral Sorcery or GriefPrevention load them even if we set them to not load on startup)
- Full GUI

(Wiki WIP)
