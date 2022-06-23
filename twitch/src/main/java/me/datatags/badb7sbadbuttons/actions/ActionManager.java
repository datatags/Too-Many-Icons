package me.datatags.badb7sbadbuttons.actions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.helix.domain.CustomReward;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

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

    public Collection<Action> getActions() {
        return actions.values();
    }

    public Set<String> getActionNames() {
        return new HashSet<>(actions.keySet());
    }

    public void execute(ChannelPointsRedemption redemption) {
        for (Action action : actions.values()) {
            if (action.getReward().getId().equals(redemption.getReward().getId())) {
                if (Bukkit.getPlayer(targetUUID) == null) {
                    action.setStatus(redemption.getId(), RedemptionStatus.CANCELED);
                    plugin.getLogger().info("Canceled reward due to no target");
                    return;
                }
                if (action.acceptsInput() && !action.validateInput(redemption.getUserInput())) {
                    action.setStatus(redemption.getId(), RedemptionStatus.CANCELED);
                    plugin.getLogger().info("Canceled reward due to invalid input: " + redemption.getUserInput());
                    return;
                }
                triggerAction(action, redemption.getUser().getDisplayName(), redemption.getUserInput(), redemption.getId());
                if (action.autoFulfill()) {
                    action.setStatus(redemption.getId(), RedemptionStatus.FULFILLED);
                }
                return;
            }
        }
        // Must be a reward that we don't control, so ignore it.
    }

    public void triggerAction(Action action, String name, String input, String redemptionId) {
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            Bukkit.broadcastMessage(name + " just redeemed reward " + action.getTitle());
            Bukkit.getScheduler().runTask(plugin, () -> action.onAction(target, input, redemptionId));
        }
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

    public UUID getTargetUUID() {
        return targetUUID;
    }

    public void setTargetUUID(UUID uuid) {
        if (uuid == null) {
            targetUUID = BADB7;
        } else {
            targetUUID = uuid;
        }
    }

    public void updateAll(Function<CustomReward,CustomReward> func) {
        for (Action action : getActions()) {
            action.update(func);
        }
    }
}
