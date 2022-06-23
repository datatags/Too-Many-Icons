package me.datatags.badb7sbadbuttons;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.datatags.badb7sbadbuttons.actions.Action;
import me.datatags.badb7sbadbuttons.actions.ActionManager;

public class EventListener implements Listener {
    private final ActionManager am;

    public EventListener(BadB7sBadButtons plugin) {
        this.am = plugin.getActionManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        setPaused(event, false);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        setPaused(event, true);
    }

    private void setPaused(PlayerEvent event, boolean paused) {
        if (!event.getPlayer().getUniqueId().equals(am.getTargetUUID())) return;
        for (Action action : am.getActions()) {
            action.update(action.getReward().withIsPaused(paused));
        }
    }
}
