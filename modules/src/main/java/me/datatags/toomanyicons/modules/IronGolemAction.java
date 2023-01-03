package me.datatags.toomanyicons.modules;

import org.bukkit.entity.EntityType;

public class IronGolemAction extends RandomMobAction {

    public IronGolemAction() {
        super("Iron Golem", "Spawn an iron golem");
    }

    @Override
    public boolean isValid(EntityType type) {
        return type == EntityType.IRON_GOLEM;
    }

}
