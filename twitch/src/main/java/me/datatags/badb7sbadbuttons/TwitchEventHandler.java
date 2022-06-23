package me.datatags.badb7sbadbuttons;

import org.bukkit.Bukkit;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.Arrays;

import me.datatags.badb7sbadbuttons.actions.ActionManager;

public class TwitchEventHandler {
    private final ActionManager am;
    private final String minecraftGameId;

    public TwitchEventHandler(BadB7sBadButtons plugin) {
        this.am = plugin.getActionManager();
        this.minecraftGameId = plugin.getTwitchClient().getHelix().getGames(null, null, Arrays.asList("Minecraft")).execute().getGames().get(0).getId();
        plugin.getLogger().info("Found Minecraft game ID: " + minecraftGameId);
    }

    @EventSubscriber
    public void onReward(RewardRedeemedEvent e) {
        am.execute(e.getRedemption());
    }

    @EventSubscriber
    public void onLive(ChannelGoLiveEvent e) {
        updateRewardStatus(e.getStream().getGameId());
    }

    @EventSubscriber
    public void onGameUpdate(ChannelChangeGameEvent e) {
        updateRewardStatus(e.getGameId());
    }

    private void updateRewardStatus(String gameId) {
        boolean enabled = gameId.equals(minecraftGameId);
        am.updateAll(r -> r.withIsEnabled(enabled));
        if (enabled) {
            Bukkit.getLogger().info("Enabling minecraft rewards due to update to " + gameId);
        } else {
            Bukkit.getLogger().info("Disabling minecraft rewards due to update to " + gameId);
        }
    }
}
