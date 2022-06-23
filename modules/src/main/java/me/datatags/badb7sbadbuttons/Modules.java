package me.datatags.badb7sbadbuttons;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.datatags.badb7sbadbuttons.actions.ActionManager;
import me.datatags.badb7sbadbuttons.modules.BossMobAction;
import me.datatags.badb7sbadbuttons.modules.BounceAction;
import me.datatags.badb7sbadbuttons.modules.DiamondSetAction;
import me.datatags.badb7sbadbuttons.modules.IronGolemAction;
import me.datatags.badb7sbadbuttons.modules.NormalMobAction;
import me.datatags.badb7sbadbuttons.modules.PotionAction;
import me.datatags.badb7sbadbuttons.modules.RottenMeatAction;
import me.datatags.badb7sbadbuttons.modules.TimeBombAction;

public class Modules extends JavaPlugin {
    @Override
    public void onEnable() {
        ActionManager am = BadB7sBadButtons.getInstance().getActionManager();
        am.registerAction(new PotionAction(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0)));
        am.registerAction(new PotionAction(new PotionEffect(PotionEffectType.SLOW, 300, 3)));
        am.registerAction(new BounceAction());
        am.registerAction(new NormalMobAction());
        am.registerAction(new BossMobAction());
        am.registerAction(new IronGolemAction());
        am.registerAction(new TimeBombAction());
        am.registerAction(new DiamondSetAction());
        am.registerAction(new RottenMeatAction());
    }
}
