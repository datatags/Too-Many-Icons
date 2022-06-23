package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.datatags.badb7sbadbuttons.actions.Action;

public class BounceAction extends Action {

    public BounceAction() {
        super("BOUUNCE", "Throw me up in the air!");
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        player.setVelocity(new Vector(0, 2.5, 0));
    }
}
