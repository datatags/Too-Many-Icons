package me.datatags.badb7sbadbuttons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.CustomReward;
import com.netflix.hystrix.HystrixCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import me.datatags.badb7sbadbuttons.actions.Action;

public class RewardCommand implements TabExecutor {
    private final BadB7sBadButtons plugin;
    public RewardCommand(BadB7sBadButtons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.GREEN + "Available commands: enable, disable, setprice, trigger, settarget");
            return false;
        }
        if (args[0].equalsIgnoreCase("settarget")) {
            if (args[1].equalsIgnoreCase("default")) {
                plugin.getActionManager().setTargetUUID(null);
                sender.sendMessage(ChatColor.GREEN + "Reset target to default");
                return true;
            }
            Player newTarget = Bukkit.getPlayer(args[1]);
            if (newTarget == null) {
                sender.sendMessage(ChatColor.RED + "Invalid player");
                return true;
            }
            plugin.getActionManager().setTargetUUID(newTarget.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Target set. Use '/" + label + " settarget default' to reset");
            return true;
        }
        Action action = plugin.getActionManager().getByName(args[1]);
        if (action == null) {
            sender.sendMessage(ChatColor.RED + "Unknown reward: " + args[1]);
            return true;
        }
        CustomReward reward = action.getReward();
        if (args[0].equalsIgnoreCase("enable")) {
            action.update(reward.withIsEnabled(true));
            sender.sendMessage(ChatColor.GREEN + "Reward enabled");
            return true;
        } else if (args[0].equalsIgnoreCase("disable")) {
            action.update(reward.withIsEnabled(false));
            sender.sendMessage(ChatColor.GREEN + "Reward disabled");
            return true;
        } else if (args[0].equalsIgnoreCase("setprice")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "You must supply a new price");
                return true;
            }
            int price;
            try {
                price = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid price");
                return true;
            }
            action.update(reward.withCost(price));
            sender.sendMessage(ChatColor.GREEN + "Price updated");
            return true;
        } else if (args[0].equalsIgnoreCase("trigger")) {
            plugin.getActionManager().triggerAction(action, sender.getName() + " (in-game)");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("enable");
            options.add("disable");
            options.add("setprice");
            options.add("trigger");
            options.add("settarget");
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("settarget")) {
            options.addAll(plugin.getActionManager().getActionNames());
        } else {
            return options;
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options, new ArrayList<>());
    }

    @SuppressWarnings("unused")
    private void apiCall(Function<TwitchHelix,HystrixCommand<?>> func) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> func.apply(plugin.getTwitchClient().getHelix()).execute());
    }
}
