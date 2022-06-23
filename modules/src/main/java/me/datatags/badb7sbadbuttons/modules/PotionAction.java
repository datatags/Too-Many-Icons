package me.datatags.badb7sbadbuttons.modules;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.datatags.badb7sbadbuttons.actions.Action;

public class PotionAction extends Action {
    private final PotionEffect effect;
    public PotionAction(PotionEffect effect) {
        super(titleCase(effect.getType().getName()), "Give me " + titleCase(effect.getType().getName()) + " for " + effect.getDuration() / 20 + " seconds!");
        this.effect = effect;
    }

    @Override
    public void onAction(Player player, String input, String redemptionId) {
        PotionEffect active = player.getPotionEffect(effect.getType());
        if (active != null) {
            player.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration() + active.getDuration(), effect.getAmplifier()));
            return;
        }
        effect.apply(player);
    }
}
