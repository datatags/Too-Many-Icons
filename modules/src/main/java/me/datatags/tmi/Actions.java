package me.datatags.tmi;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.datatags.tmi.modules.BossMobAction;
import me.datatags.tmi.modules.BounceAction;
import me.datatags.tmi.modules.DiamondSetAction;
import me.datatags.tmi.modules.IronGolemAction;
import me.datatags.tmi.modules.NormalMobAction;
import me.datatags.tmi.modules.PotionAction;
import me.datatags.tmi.modules.RottenMeatAction;
import me.datatags.tmi.modules.TimeBombAction;
import me.datatags.toomanyicons.TooManyIcons;
import me.datatags.toomanyicons.actions.ActionManager;

public class Actions extends JavaPlugin {
    @Override
    public void onEnable() {
        ActionManager am = TooManyIcons.getInstance().getActionManager();
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
