package me.datatags.badb7sbadbuttons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.twitch4j.helix.TwitchHelix;
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
            sender.sendMessage(ChatColor.GREEN + "Available commands: enable, disable, pause, unpause, setprice, trigger, settarget");
            return false;
        }
        if (args[0].equalsIgnoreCase("settarget")) {
            if (args[1].equalsIgnoreCase("default")) {
                plugin.getActionManager().setTargetUUID(null);
                sender.sendMessage(ChatColor.GREEN + "Reset target to default");
            } else {
                Player newTarget = Bukkit.getPlayer(args[1]);
                if (newTarget == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid player");
                    return true;
                }
                plugin.getActionManager().setTargetUUID(newTarget.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Target set. Use '/" + label + " settarget default' to reset");
            }
            if (Bukkit.getPlayer(plugin.getActionManager().getTargetUUID()) == null) {
                sender.sendMessage(ChatColor.RED + "Pausing redeems because target is not online");
                plugin.getActionManager().updateAll(r -> r.withIsPaused(true));
            } else {
                sender.sendMessage(ChatColor.GREEN + "Unpausing redeems because target is online");
                plugin.getActionManager().updateAll(r -> r.withIsPaused(false));
            }
            return true;
        }
        List<Action> actions = new ArrayList<>();
        if (args[1].equalsIgnoreCase("all")) {
            actions.addAll(plugin.getActionManager().getActions());
        } else {
            Action action = plugin.getActionManager().getByName(args[1]);
            if (action == null) {
                sender.sendMessage(ChatColor.RED + "Unknown reward: " + args[1]);
                return true;
            }
            actions.add(action);
        }
        if (args[0].equalsIgnoreCase("enable")) {
            actions.forEach(a -> a.update(r -> r.withIsEnabled(true)));
            sender.sendMessage(ChatColor.GREEN + "Reward enabled");
            return true;
        } else if (args[0].equalsIgnoreCase("disable")) {
            actions.forEach(a -> a.update(r -> r.withIsEnabled(false)));
            sender.sendMessage(ChatColor.GREEN + "Reward disabled");
            return true;
        } else if (args[0].equalsIgnoreCase("pause")) {
            actions.forEach(a -> a.update(r -> r.withIsPaused(true)));
            sender.sendMessage(ChatColor.GREEN + "Reward paused");
            return true;
        } else if (args[0].equalsIgnoreCase("unpause")) {
            actions.forEach(a -> a.update(r -> r.withIsPaused(false)));
            sender.sendMessage(ChatColor.GREEN + "Reward unpaused");
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
            actions.forEach(a -> a.update(a.getReward().withCost(price)));
            sender.sendMessage(ChatColor.GREEN + "Price updated");
            return true;
        } else if (args[0].equalsIgnoreCase("trigger")) {
            String input = args.length > 2 ? args[2] : "";
            List<Action> inputFiltered = new ArrayList<>(actions);
            inputFiltered.removeIf(a -> a.validateInput(input));
            if (inputFiltered.size() != 0) {
                sender.sendMessage(ChatColor.RED + "Invalid input");
                return true;
            }
            actions.forEach(a -> plugin.getActionManager().triggerAction(a, sender.getName() + " (in-game)", input, null));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("enable");
            options.add("disable");
            options.add("pause");
            options.add("unpause");
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
