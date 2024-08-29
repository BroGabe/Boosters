package com.thedev.boosters.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.BoosterManager;
import com.thedev.boosters.Utils.ColorUtil;
import com.thedev.boosters.Utils.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@CommandAlias("boosters|booster")
public class BoosterCommand extends BaseCommand {

    private final Boosters plugin;

    public BoosterCommand(Boosters plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("give")
    @CommandPermission("boosters.admin")
    @Description("Give players ")
    public void onGiveCmd(CommandSender sender, OnlinePlayer onlinePlayer, String boosterType, double multiplier, long seconds) {
        FileConfiguration config = plugin.getConfig();
        Optional<Booster> boosterOptional = BoosterManager.getBoosterFromString(boosterType, 0.0, multiplier, seconds);

        if(!boosterOptional.isPresent()) {
            sender.sendMessage(ColorUtil.color(config.getString("messages.invalid-booster")));
            return;
        }

        ItemStack boosterItem = ItemBuilder.getBoosterItem(boosterOptional.get(), multiplier, seconds);

        onlinePlayer.getPlayer().getInventory().addItem(boosterItem);

        sender.sendMessage(ColorUtil.color(config.getString("messages.given-booster").replace("%player%", onlinePlayer.getPlayer().getName())));

        onlinePlayer.getPlayer().sendMessage(ColorUtil.color(config.getString("messages.received-booster")));
    }

    @Default
    public void onDefault(Player player) {
        plugin.getBoostersMenu().getBoosterGUI(player.getUniqueId()).open(player);
    }
}
