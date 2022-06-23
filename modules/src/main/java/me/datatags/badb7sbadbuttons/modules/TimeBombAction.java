package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.twitch4j.eventsub.domain.RedemptionStatus;

import me.datatags.badb7sbadbuttons.actions.Action;

public class TimeBombAction extends Action {

    public TimeBombAction() {
        super("Time Bomb", "After this number of jumps, TNT will spawn on me! (number between 10 and 100)");
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        int threshold = player.getStatistic(Statistic.JUMP) + Integer.parseInt(input);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    setStatus(redemptionId, RedemptionStatus.CANCELED);
                    plugin.getLogger().info("Canceled reward due to target going offline");
                    return;
                }
                if (player.getStatistic(Statistic.JUMP) < threshold) return;
                cancel();
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(60);
                player.playSound(tnt, Sound.ENTITY_TNT_PRIMED, 1, 0);
                setStatus(redemptionId, RedemptionStatus.FULFILLED);
            }
        }.runTaskTimer(plugin, 10, 10);
    }

    @Override
    public boolean autoFulfill() {
        return false;
    }

    @Override
    public boolean acceptsInput() {
        return true;
    }

    @Override
    public boolean validateInput(String input) {
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return amount >= 10 && amount <= 100;
    }
}
