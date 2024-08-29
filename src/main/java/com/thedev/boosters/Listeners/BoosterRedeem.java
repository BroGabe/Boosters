package com.thedev.boosters.Listeners;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.BoosterManager;
import com.thedev.boosters.Utils.ColorUtil;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.Optional;

public class BoosterRedeem implements Listener {

    private final Boosters plugin;

    public BoosterRedeem(Boosters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();

        if(itemInHand == null || itemInHand.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(itemInHand);

        NBTCompound compound = nbtItem.getCompound("SweetBoosters");

        if(compound == null) return;

        event.setCancelled(true);

        String boosterName = compound.getString("booster-name");
        double timedMulti = compound.getDouble("timed-multiplier");
        double permanentMulti = compound.getDouble("permanent-multiplier");
        long expiration = compound.getLong("booster-expiration");

        Optional<Booster> boosterOptional = BoosterManager.getBoosterFromString(boosterName, permanentMulti, timedMulti, Instant.now().plusSeconds(expiration).toEpochMilli());

        if(!boosterOptional.isPresent()) {
            player.sendMessage(ColorUtil.color("&5&lBoosters &dInvalid Booster!"));
            return;
        }

        // If the player already has the booster
        Optional<Booster> ownedBooster = plugin.getBoosterManager().getPlayerBooster(player.getUniqueId(), boosterOptional.get().getClass());
        if(ownedBooster.isPresent()) {
            if(!ownedBooster.get().canMerge(boosterOptional.get())) {
                player.sendMessage(ColorUtil.color(plugin.getConfig().getString("messages.cannot-redeem")));
                return;
            }
        }

        if (itemInHand.getAmount() > 1) {
            itemInHand.setAmount(itemInHand.getAmount() - 1);
        } else {
            player.setItemInHand(null);
        }

        plugin.getBoosterManager().addPlayerBooster(player.getUniqueId(), boosterOptional.get());
    }
}
