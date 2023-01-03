# Too Many Icons
This is a Twitch-Minecraft Integration plugin for making *things* happen when channel points are redeemed. This project is affectionately called [Too Many Icons](https://www.youtube.com/watch?v=Dd0IaBX8cEU&t=1586s), primarily because I thought it was a funny name. I wrote it just as a fun project, so the code probably isn't the highest quality.

Too Many Icons automatically handles refreshing authentication tokens (which is not something that Twitch4J natively supports [as of this writing](https://github.com/twitch4j/twitch4j/issues/175)) and will instantly refund spent channel points if the chosen target for the actions is not online. (Ideally it would 'pause' the rewards if the target is offline, but I'll leave that up to you for reasons mentioned below.) Some fun actions are built-in, but you're encouraged to write your own.

The project is divided into two separate plugins, `twitch` and `modules`. `twitch` is the core of the plugin and handles the interactions with, well, Twitch. All the channel point rewards are created in `modules`. This split allows you to hotswap the rewards part of the plugin (using [BileTools](https://www.spigotmc.org/resources/54823/), for example) which allows for quick testing without having to restart the whole server each time.

I no longer have access to a Twitch affiliate account to test this with, so it's unlikely I will ever update this. I'm releasing it here in case anybody else (like you!) wants a handy framework for writing these sorts of things. If you do use this, please create an issue or something to link me to your channel; I'd love to see how you're using it!

-----

## Twitch Developer Applications
To obtain a `client_id` and `client_secret` to populate in `config.yml`, you can create an app [here](https://dev.twitch.tv/console/apps/create).

Alternatively, you can provide `oauth_token`, which can be generated [here](https://www.twitchtokengenerator.com/) if you do not wish to host the authentication process yourself.

At least one of these must be provided to interact with the Helix API, which is used to track follows and stream state.
