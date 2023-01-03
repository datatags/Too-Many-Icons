package me.datatags.toomanyicons.modules;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.datatags.toomanyicons.actions.Action;

public abstract class RandomMobAction extends Action {

    public RandomMobAction(String name, String description) {
        super(name, description);
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        List<EntityType> types = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (type == EntityType.UNKNOWN) continue;
            if (isValid(type)) {
                types.add(type);
            }
        }
        EntityType random = types.get(ThreadLocalRandom.current().nextInt(types.size()));
        Entity entity = player.getWorld().spawnEntity(player.getLocation(), random);
        Bukkit.broadcastMessage("Spawning a " + titleCase(random.toString()));
        if (random == EntityType.ENDER_DRAGON) {
            // reset dragon AI
            ((EnderDragon) entity).setPhase(Phase.CIRCLING);
        }
    }

    public abstract boolean isValid(EntityType type);
}
