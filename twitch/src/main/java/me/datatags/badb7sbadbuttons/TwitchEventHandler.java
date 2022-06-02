package me.datatags.badb7sbadbuttons;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

public class TwitchEventHandler {
    private final BadB7sBadButtons plugin;
    public TwitchEventHandler(BadB7sBadButtons plugin) {
        this.plugin = plugin;
    }

    @EventSubscriber
    public void onReward(RewardRedeemedEvent e) {
        plugin.getActionManager().execute(e.getRedemption());
    }
}
