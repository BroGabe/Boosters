package com.thedev.boosters.PlayerManager;

import com.thedev.boosters.Boosters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuit implements Listener {

    private final Boosters plugin;

    public PlayerJoinQuit(Boosters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().loadBoostersFromConfig(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().saveBoostersToConfig(event.getPlayer().getUniqueId());
        plugin.getBoosterManager().getPlayerMap().remove(event.getPlayer().getUniqueId());
    }
}
