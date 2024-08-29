package com.thedev.boosters.Menu;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.BoosterManager;
import com.thedev.boosters.Utils.ColorUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class BoostersMenu {

    private final Boosters plugin;

    public BoostersMenu(Boosters plugin) {
        this.plugin = plugin;
    }

    public PaginatedGui getBoosterGUI(UUID playerUUID) {
        FileConfiguration config = plugin.getConfig();
        PaginatedGui gui = Gui.paginated()
                .title(Component.text(ColorUtil.color(config.getString("booster-menus.menu-name"))))
                .rows(3)
                .pageSize(27)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));

        gui.setItem(3, 4, ItemBuilder.from(Material.PAPER).setName("Previous").asGuiItem(event -> gui.previous()));

        gui.setItem(3, 6, ItemBuilder.from(Material.PAPER).setName("Next").asGuiItem(event -> gui.next()));

        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);

        gui.getFiller().fillBorder(ItemBuilder.from(blackGlass).asGuiItem());

        BoosterManager boosterManager = plugin.getBoosterManager();

        List<Booster> playerBoosters = boosterManager.getPlayerBoosters(playerUUID);

        for(Booster booster : playerBoosters) {
            String boosterName = booster.getClass().getSimpleName();
            String section = (config.getConfigurationSection("booster-menus." + boosterName.toLowerCase()) == null)
                    ? "default-booster-menu" : "booster-menus." + boosterName.toLowerCase();

            String name = config.getString(section + ".name");
            String texture = config.getString(section + ".texture");
            Material material = Material.valueOf(config.getString(section + ".material"));
            int data = config.getInt(section + ".data");

            List<String> lore = config.getStringList(section + ".lore");
            com.thedev.boosters.Utils.ItemBuilder itemBuilder = new com.thedev.boosters.Utils.ItemBuilder(material, name, 1, data, texture, lore.toArray(new String[0]));

            Function<String, String> updatePlaceholders = (string) -> string.replaceAll("%boostername%", boosterName).replaceAll("%amount%", String.valueOf(booster.getMulti()));

            itemBuilder.updateName(updatePlaceholders);
            itemBuilder.updateLore(updatePlaceholders);

            gui.addItem(new GuiItem(itemBuilder.getItem()));
        }


        return gui;
    }
}
