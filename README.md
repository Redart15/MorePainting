# MorePainting
[![GitHub License](https://img.shields.io/github/license/Redart15/MorePainting?labelColor=7A5CC1&color=FFFFFF&cacheSeconds=120)](https://github.com/Redart15/MorePainting?tab=CC0-1.0-1-ov-file)
[![GitHub Release](https://img.shields.io/github/v/release/Redart15/MorePainting?labelColor=7A5CC1&color=FFFFFF&cacheSeconds=120)](https://github.com/Redart15/MorePainting/releases)
[![GitHub last commit](https://img.shields.io/github/last-commit/Redart15/MorePainting?labelColor=7A5CC1&color=FFFFFF&cacheSeconds=120)](https://github.com/Redart15/MorePainting/commits/8.0/)
[![Lines of Code](https://img.shields.io/endpoint?url=https%3A%2F%2Fghloc.vercel.app%2Fapi%2FRedart15%2FMorePainting%2Fbadge&labelColor=7A5CC1&color=FFFFFF)](https://ghloc.vercel.app/)

> Paintings are my favorite thing. I would convert so many from mods if I had the time/knowledge.

MorePainting is here to make this process as simple as possible.

<p align=center>
  <img width="747" height="628" alt="grafik" src="https://github.com/user-attachments/assets/7382493d-bf75-4bbf-98a5-5f382337f08d" />
</p>

## 🖌️ More Paintings 8.0
More Painting, does not add itself more painting.
Instead it reworks how the painting system in BTA works.
This allows asset packs to add more paintings.
Importantly the mod is required on both the client and server to work.

## 🖼️  Asset pack support
The mod mainly allows asset packs to add new paintings.
Define the configuration for the pantings, include the desired texture and package it as a valid texture pack.
The game will be able to load and unload these textures freely.
The config and all textured need to be places in `assets/*/textures/art/` and the config has to be named `painting.toml`.
The config consists of a list named `paintings`.
Each element of the list is an object with the following attributes:
 - `title` (optional), when left out displays ingame "Missing title"
 - `artist` (optional), when left out displays ingame "Unknown artist"
 - `texture` (required), defined as [namespace]:art/[filename], here the namespace is the mod id and the filename the name of the texture. 
 - `dimensions` (optinal), allow to define `width` & `height`. When left out the game calculates the size itself. `width` & `height` are measures in blocks. A 3x3 painting has width = 3 and height = 3
   `width` & `height` cannot be negative or exceed 100 blocks.
 - `scaled` (optional), when dimensions is not given the game uses the textures `width` & `height`. Picture only is scaled for valid `scaled` above 0. Scale is indended for high res texture.

Here is an example:
```toml
[[paintings]]
title = "Alpha"
artist = "Yapetto"
texture = "minecraft:art/alpha"
[painting.dimensions]
width = 1
height =  1

# painting is scaled down by 10
[[paintings]]
artist = "Yapetto"
texture = "minecraft:art/an_intruder"
scale = 10
[painting.dimensions]
width = 100
height =  100

# minimum vailable entry
[[paintings]]
texture = "minecraft:art/ancestor"
```


## 📁 Mod Support
Mod can still use the old class `ArtType` to add more painting through it.
All clients will agree on all `ArtType` paintings, e.i. two different client with different asset packs will see the same painting from `ArtType`.
Painting added via config in the `ressources/assets/` will be added after all pictures defined through in `ArtType`.
Keep this in mind when making paintings via modding.

## ❓ Q&A:

### Multiplayer
Q: Is this a client side mod?  
A: No, the mod is required both on server and client.  

Q: Can datapacks add more paintings?  
A: No, any additionally added painting are client side only  

Q: Client A has an asset pack with a custom painting of a burger. Client B does not have that asset pack.
   Server does not have that asset pack. Both clients and the server have the mod.  
   Can Client B see the painting from Client A's asset pack, even though Client B and the server both don't have the asset pack?
A: Client B sees either the default painting or some other painting.

### Making texture pack
Q: Help my painting arent loading  
A: Is the asset pack loaded?  

Q: Asset is loaded but the painting still does not appear.  
A: Check if the texture is in the correct location.  
For example a texture with the key `"battletower:art/golem"` should be at `assets/battletower/textures/art/`.  

Q:The texture is the correct location and the assets pack is loaded still nothing.  
A: Make sure that the configuration is in `assets/*/textures/art/`, so in either `assets/battletower/textures/art/` or `assets/minecraft/textures/art/`.  

Q: Is it what now?  
A: Is the entry defined in the configuration?  

Q: Hurray it loads but the texture is missing.  
A: Check if the texture is in the correct location and are named correctly.   

Q: None of the above.  
A: Check BTA related channel for this mod, ask in bta-modding channel or open a new issue on github.  

## :soccer:  Goals:
- Redesign the ScreenPaintingPicker gui.
  - add filter
  - search bar
- Fix the 16x16 painting size to allow much larger painting

:gear: Github:         <https://github.com/Redart15/MorePainting>  
:jigsaw: Modrinth:  https://modrinth.com/mod/morepainting/version/1.0.0+8.0.1  
