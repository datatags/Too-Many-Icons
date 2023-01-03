package me.datatags.tmi.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.datatags.toomanyicons.actions.Action;

public class RottenMeatAction extends Action {

    public RottenMeatAction() {
        super("Rotten meat", "Turn everything in my inventory into rotten meat!");
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack stack : inventory) {
            if (stack == null) continue;
            stack.setType(Material.ROTTEN_FLESH);
        }
        // setContents is apparently unnecessary
    }

}
