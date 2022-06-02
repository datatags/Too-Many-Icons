package me.datatags.badb7sbadbuttons.actions;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.twitch4j.helix.domain.CustomReward;

import java.util.Arrays;
import java.util.List;

import me.datatags.badb7sbadbuttons.BadB7sBadButtons;

public abstract class Action {
    protected final BadB7sBadButtons plugin;
    protected final String internalName;
    protected final String name;
    protected final ConfigurationSection section;
    protected CustomReward reward;
    public Action(String name) {
        this.plugin = BadB7sBadButtons.getInstance();
        this.internalName = name.toLowerCase().replace(" ", "");
        this.name = name;
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
            reward = new CustomReward().withTitle(name).withCost(1000).withIsEnabled(false);
            // Replace with the updated reward, which includes an ID, among other things
            reward = plugin.getTwitchClient().getHelix().createCustomReward(null, plugin.getBroadcasterId(), reward).execute().getRewards().get(0);
            section.set("id", reward.getId());
            plugin.saveConfig();
        }
    }

    public CustomReward getReward() {
        return reward;
    }

    public void update(CustomReward newReward) {
        reward = plugin.getTwitchClient().getHelix().updateCustomReward(null, plugin.getBroadcasterId(), reward.getId(), newReward).execute().getRewards().get(0);
    }

    public abstract void onAction(Player player);
}
