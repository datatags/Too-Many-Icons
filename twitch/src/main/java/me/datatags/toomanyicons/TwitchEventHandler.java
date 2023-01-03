package me.datatags.toomanyicons;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import me.datatags.toomanyicons.actions.ActionManager;

public class TwitchEventHandler {
    private final ActionManager am;

    public TwitchEventHandler(TooManyIcons plugin) {
        this.am = plugin.getActionManager();
    }

    @EventSubscriber
    public void onReward(RewardRedeemedEvent e) {
        am.execute(e.getRedemption());
    }
}
