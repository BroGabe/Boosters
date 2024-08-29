package com.thedev.boosters.Listeners;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.Boosters.EXPBooster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;


public class XPBoosterListener implements Listener {

    private final Boosters plugin;

    public XPBoosterListener(Boosters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null) return;
        Player player = event.getEntity().getKiller();

        if(!plugin.getBoosterManager().getPlayerBooster(player.getUniqueId(), EXPBooster.class).isPresent()) return;

        Booster booster = plugin.getBoosterManager().getPlayerBooster(player.getUniqueId(), EXPBooster.class).get();

        int newExp = (int) (event.getDroppedExp() * booster.getMulti());

        event.setDroppedExp(newExp);
    }
}
