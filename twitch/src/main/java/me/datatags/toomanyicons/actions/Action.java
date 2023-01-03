package me.datatags.toomanyicons.actions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;
import com.github.twitch4j.helix.domain.CustomReward;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import me.datatags.toomanyicons.TooManyIcons;

public abstract class Action {
    protected final TooManyIcons plugin;
    protected final String internalName;
    protected final String name;
    protected final String description;
    protected final ConfigurationSection section;
    protected CustomReward reward;

    public Action(String name, String description) {
        this.plugin = TooManyIcons.getInstance();
        this.internalName = name.replaceAll("[^a-zA-Z]", "").toLowerCase();
        this.name = name;
        this.description = description;
        ConfigurationSection actions = plugin.getConfig().getConfigurationSection("actions");
        if (actions == null) {
            actions = plugin.getConfig().createSection("actions");
        }
        if (actions.isConfigurationSection(name)) {
            section = actions.getConfigurationSection(name);
        } else {
            section = actions.createSection(name);
        }
        plugin.saveConfig();
    }

    public void register() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::pullInfo);
    }

    private void pullInfo() {
        String id = section.getString("id");
        if (id != null) {
            List<CustomReward> list = plugin.getTwitchClient().getHelix().getCustomRewards(null, plugin.getBroadcasterId(), Arrays.asList(id), true).execute().getRewards();
            if (list.size() > 0) {
                reward = list.get(0);
            }
        }

        if (reward == null) {
            plugin.getLogger().info("Creating reward " + name);
            reward = new CustomReward().withTitle(getDisplayTitle()).withPrompt(description).withIsUserInputRequired(acceptsInput()).withCost(1000).withIsEnabled(false);
            // Replace with the updated reward, which includes an ID, among other things
            reward = plugin.getTwitchClient().getHelix().createCustomReward(null, plugin.getBroadcasterId(), reward).execute().getRewards().get(0);
            section.set("id", reward.getId());
            plugin.saveConfig();
        } else {
            CustomReward updatedReward = reward;
            if (!updatedReward.getTitle().equals(getDisplayTitle())) {
                plugin.getLogger().info("Updating title for " + name);
                updatedReward = updatedReward.withTitle(getDisplayTitle());
            }
            if (!updatedReward.getPrompt().equals(description)) {
                plugin.getLogger().info("Updating prompt for " + name);
                updatedReward = updatedReward.withPrompt(description);
            }

            if (updatedReward != reward) {
                update(updatedReward);
            }
        }
    }

    public CustomReward getReward() {
        return reward;
    }

    public void update(CustomReward newReward) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> reward = plugin.getTwitchClient().getHelix().updateCustomReward(null, plugin.getBroadcasterId(), reward.getId(), newReward).execute().getRewards().get(0));
    }

    public void update(Function<CustomReward,CustomReward> func) {
        update(func.apply(reward));
    }

    public void setStatus(String redemptionId, RedemptionStatus status) {
        if (redemptionId == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getTwitchClient().getHelix().updateRedemptionStatus(null, plugin.getBroadcasterId(), reward.getId(), Arrays.asList(redemptionId), status).execute());
    }

    public boolean acceptsInput() {
        return false;
    }

    public boolean autoFulfill() {
        return true;
    }

    protected static String titleCase(String input) {
        if (input.equals("SLOW")) input = "SLOWNESS";
        StringJoiner friendlyName = new StringJoiner(" ");
        for (String part : input.toString().split("_")) {
            friendlyName.add(part.charAt(0) + part.substring(1, part.length()).toLowerCase());
        }
        return friendlyName.toString();
    }

    public String getTitle() {
        return name;
    }

    public String getDisplayTitle() {
        return name + " (Minecraft only)";
    }

    public abstract void onAction(Player player, String input, String redemptionId);

    public boolean validateInput(String input) {
        return true;
    }

}
