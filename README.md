# Jarona
(I didn't have a better name for this, bare with me)

I've been wanting to make more minigames recently, but frankly I don't want to keep copying the same stuff over and over to new plugins. I've decided to just have one plugin (with dependencies) to have all the systems and whatnot that I can use for the minigames I plan on making.

~~Feel free to look around, there's not much currently other than a Condition system and barebones Game stuff. I'll be adding more when I need it and I'm active.~~

Okay, way more now. A queue system, a condition system (meant more for cooldowns and general displays on the actionbar), game registry, map registry, tablist stuff, it's in a better position now to use.

Though, I would not recommend using this API with any other minigame plugins (other than the ones that depend on it, of course) because it may or may not override some aspects of those other plugins.

And this plugin is not meant to be used on survival servers. **Your items/potion effects will be lost if you queue into a minigame using this API!! Be warned!!**

### Running on Minecraft 26.2
### Running on [PaperMC](https://papermc.io) (No plans to support Spigot)
### Requires [PacketEvents](https://modrinth.com/plugin/packetevents)
### Requires [UnlimitedNameTags](https://github.com/alexdev03/UnlimitedNametags)