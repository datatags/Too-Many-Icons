package me.datatags.toomanyicons;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.datatags.toomanyicons.actions.ActionManager;
import me.datatags.toomanyicons.modules.BossMobAction;
import me.datatags.toomanyicons.modules.BounceAction;
import me.datatags.toomanyicons.modules.DiamondSetAction;
import me.datatags.toomanyicons.modules.IronGolemAction;
import me.datatags.toomanyicons.modules.NormalMobAction;
import me.datatags.toomanyicons.modules.PotionAction;
import me.datatags.toomanyicons.modules.RottenMeatAction;
import me.datatags.toomanyicons.modules.TimeBombAction;

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
