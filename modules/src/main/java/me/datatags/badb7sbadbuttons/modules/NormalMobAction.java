package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

public class NormalMobAction extends RandomMobAction {

    public NormalMobAction() {
        super("Random Mob", "Spawn a random mob on me! (No boss mobs)");
    }

    @Override
    public boolean isValid(EntityType type) {
        return Mob.class.isAssignableFrom(type.getEntityClass());
    }

}
