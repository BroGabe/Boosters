package com.thedev.boosters.Utils;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.Boosters.EXPBooster;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
    }

    public ItemBuilder(Material material, String name, String... lore) {
        this(material, name, 1, 0, null, lore);
    }

    public ItemBuilder(Material material, String name, int amount, int data, String texture, String... lore) {
        item = new ItemStack(material, amount, (short) data);
        assignName(name);
        assignLore(Arrays.asList(lore));

        if(texture != null && !texture.isEmpty() && data == 3 && material == Material.SKULL_ITEM) {
            assignTexture(texture);
        }
    }


    public ItemStack setGlow() {
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);

        return item;
    }

    private void assignLore(List<String> loreList) {
        List<String> updatedLore = new ArrayList<>();

        loreList.forEach(string -> updatedLore.add(ColorUtil.color(string)));

        ItemMeta itemMeta= item.getItemMeta();
        itemMeta.setLore(updatedLore);
        item.setItemMeta(itemMeta);
    }

    private void assignName(String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ColorUtil.color(name));

        item.setItemMeta(itemMeta);
    }

    public ItemStack getItem() {
        return item;
    }

    public void updateLore(Function<String, String> replace) {
        ItemMeta itemMeta = getItem().getItemMeta();;

        List<String> updatedLore = new ArrayList<>();

        for(String string : itemMeta.getLore()) {
            updatedLore.add(replace.apply(string));
        }

        itemMeta.setLore(updatedLore);
        getItem().setItemMeta(itemMeta);

    }

    public void updateName(Function<String, String> replace) {
        ItemMeta itemMeta = getItem().getItemMeta();

        itemMeta.setDisplayName(replace.apply(itemMeta.getDisplayName()));

        getItem().setItemMeta(itemMeta);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void assignTexture(String texture) {
        NBT.modify(item, nbt -> {
            final ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");

            skullOwnerCompound.setUUID("Id", UUID.randomUUID());
            skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", texture);
        });
    }

    public static ItemStack getBoosterItem(Booster booster, double multiplier, long secondsExpiration) {
        Boosters plugin = Boosters.getInst();
        FileConfiguration config = plugin.getConfig();

        String section = "booster-item";
        String itemName = config.getString(section + ".name");
        String texture = config.getString(section + ".texture");

        Material material = Material.valueOf(config.getString(section + ".material"));

        int data = config.getInt(section + ".data");

        boolean shouldGlow = config.getBoolean(section + ".glow");

        List<String> lore = config.getStringList(section + ".lore");

        ItemBuilder itemBuilder = new ItemBuilder(material, itemName, 1, data, texture, lore.toArray(lore.toArray(new String[0])));

        if(shouldGlow) itemBuilder.setGlow();

        String boosterName = booster.getClass().getSimpleName();

        Function<String, String> updatePlaceholders = (string) -> string.replaceAll("%boostername%", boosterName)
                .replaceAll("%amount%", String.valueOf(multiplier));

        itemBuilder.updateLore(updatePlaceholders);

        itemBuilder.updateName(updatePlaceholders);

        setBoosterNBT(booster, multiplier, itemBuilder, secondsExpiration);

        return itemBuilder.getItem();
    }

    private static void setBoosterNBT(Booster booster, double multiplier, ItemBuilder itemBuilder, long secondsExpiration) {
        NBTItem nbtItem = new NBTItem(itemBuilder.getItem());

        NBTCompound compound = nbtItem.addCompound("SweetBoosters");
        compound.setString("booster-name", booster.getClass().getSimpleName());
        compound.setDouble("timed-multiplier", multiplier);
        compound.setDouble("permanent-multiplier", booster.getPermanentMulti());
        compound.setLong("booster-expiration", secondsExpiration);

        itemBuilder.setItem(nbtItem.getItem());
    }
}
