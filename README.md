# hostprofiles

hostprofiles is a Minecraft plugin for various platforms that detects if a player joins the server on a specific address
and then logs them into a completely different game profile.

Here is a usage example:
If your server address were myserver.com, you could configure admin.myserver.com (the host) as a special host and map
the player with the UUID of Joe to be logged with another, predefined UUID, use Admin_Joe as his username and have a
completely different skin (the profile). You could then only give Admin_Joe administrator privileges. This means that if
Joe joins using myserver.com, he is logged in as Joe and could play as a normal player, but if he logs into
admin.myserver.com is logged in as Admin_Joe and can do administrative tasks.

You can configure:

- Which profile (UUID, name and skin) a player trying to log in (identified by his UUID) is going to have
- If a player gets kicked if there is no entry for his UUID
- A special MOTD for every host
- If a player should keep his original skin, or use a profile skin

## Platforms

Right now, the plugin is only available for 1.18.1. Supported platforms are Paper and Spigot.
**If you use Paper you MUST also use the build for Paper, the spigot build is NOT going to work.**

## Support

The Spigot and Paper builds are still very unstable, as they use various reflection tricks to change the game profile
and retrieve the hostname. Because the Paper API provides a method to set the game profile on join, there is one less
hack in there, so I would consider it preferable. The Velocity version uses only API, therefor is the most stable.

**Velocity > Paper > Spigot**

Problems or feature requests should be reported as issues on GitHub. If you have any questions or would like to receive
quick support, you can do so on the [CrushedPixel's Developer Den Discord Server](https://discord.gg/EwBqHqZWPJ).