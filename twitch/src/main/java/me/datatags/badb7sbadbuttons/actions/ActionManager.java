package me.datatags.badb7sbadbuttons.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.datatags.badb7sbadbuttons.BadB7sBadButtons;

public class ActionManager {
    private static final UUID BADB7 = UUID.fromString("32002aff-f136-4a9a-9406-2944ddf657aa");
    private final Map<String,Action> actions = new HashMap<>();
    private final BadB7sBadButtons plugin;
    private boolean setup = false;
    private UUID targetUUID = BADB7;
    public ActionManager(BadB7sBadButtons plugin) {
        this.plugin = plugin;
    }

    public Set<String> getActionNames() {
        return new HashSet<>(actions.keySet());
    }

    public void execute(ChannelPointsRedemption redemption) {
        for (Action action : actions.values()) {
            if (action.reward.getId().equals(redemption.getReward().getId())) {
                if (Bukkit.getPlayer(targetUUID) == null) {
                    setStatus(redemption, RedemptionStatus.CANCELED);
                    return;
                }
                triggerAction(action, redemption.getUser().getDisplayName());
                setStatus(redemption, RedemptionStatus.FULFILLED);
                return;
            }
        }
        plugin.getLogger().warning("Received unknown reward ID: " + redemption.getReward().getId());
    }

    public void triggerAction(Action action, String name) {
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            Bukkit.broadcastMessage(name + " just redeemed reward " + action.getReward().getTitle());
            Bukkit.getScheduler().runTask(plugin, () -> action.onAction(target));
        }
    }

    private void setStatus(ChannelPointsRedemption redemption, RedemptionStatus status) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> 
            plugin.getTwitchClient().getHelix().updateRedemptionStatus(null, plugin.getBroadcasterId(), redemption.getReward().getId(), Arrays.asList(redemption.getId()), status).execute()
        );
    }

    public Action getByName(String name) {
        return actions.get(name);
    }

    public void registerAction(Action action) {
        actions.put(action.internalName, action);
        if (setup) {
            action.register();
        }
    }

    public void setup() {
        if (setup) return;
        setup = true;
        for (Action action : actions.values()) {
            action.register();
        }
    }

    public void setTargetUUID(UUID uuid) {
        if (uuid == null) {
            targetUUID = BADB7;
        } else {
            targetUUID = uuid;
        }
    }
}
