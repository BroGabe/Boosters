package com.thedev.boosters.PlayerManager;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;
import com.thedev.boosters.BoostersManager.BoosterManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final Boosters plugin;

    private final BoosterManager boosterManager;

    public PlayerManager(Boosters plugin, BoosterManager boosterManager) {
        this.plugin = plugin;
        this.boosterManager = boosterManager;
    }

    public FileConfiguration getPlayerConfig(UUID uuid) {
        return YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
    }

    public File getPlayerFile(UUID uuid) {
        File folder = new File(plugin.getDataFolder(), "PlayerData");
        if(!folder.exists()) folder.mkdirs();

        return new File(folder, uuid.toString() +".yml");
    }

    public void saveConfig(UUID uuid, FileConfiguration config) {
        try {
            config.save(getPlayerFile(uuid));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveBoostersToConfig(UUID playerUUID) {
        FileConfiguration config = getPlayerConfig(playerUUID);
        Set<Map.Entry<Class<? extends Booster>, Booster>> entrySet = boosterManager.getPlayerMap().get(playerUUID).entrySet();

        for(Map.Entry<Class<? extends Booster>, Booster> entry : entrySet) {
            String boosterName = entry.getKey().getSimpleName().toUpperCase();
            Booster booster = entry.getValue();

            config.set(boosterName + ".permanent-multiplier", booster.getPermanentMulti());
            config.set(boosterName + ".timed-boosters", booster.timedMapToString());
        }

        saveConfig(playerUUID, config);
    }

     protected void loadBoostersFromConfig(UUID playerUUID) {
        FileConfiguration config = getPlayerConfig(playerUUID);

        for(String section : config.getKeys(false)) {
            Optional<Booster> boosterOptional = BoosterManager.getBoosterFromString(section,
                    config.getDouble(section + ".permanent-multiplier"), config.getStringList(section + ".timed-boosters"));

            if(!boosterOptional.isPresent()) continue;

            boosterManager.addPlayerBooster(playerUUID, boosterOptional.get());
        }
    }
}
