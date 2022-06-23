package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import me.datatags.badb7sbadbuttons.actions.Action;

public class DiamondSetAction extends Action {
    private static final Material[] DIAMOND_MATERIALS = new Material[] { Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD };
    private ItemStack[] DIAMONDS = new ItemStack[9];

    public DiamondSetAction() {
        super("Diamond Set", "Give me a full diamond armor and tool set!");
        for (int i = 0; i < DIAMOND_MATERIALS.length; i++) {
            DIAMONDS[i] = new ItemStack(DIAMOND_MATERIALS[i]);
        }
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        Map<Integer,ItemStack> remaining = player.getInventory().addItem(DIAMONDS);
        for (ItemStack stack : remaining.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), stack);
        }
    }

}
