package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.entity.EntityType;

public class BossMobAction extends RandomMobAction {

    public BossMobAction() {
        super("Boss Mob", "Spawn a boss mob (Wither, Ender Dragon, or Warden)");
    }

    @Override
    public boolean isValid(EntityType type) {
        return type == EntityType.WITHER || type == EntityType.ENDER_DRAGON || type == EntityType.WARDEN;
    }

}
