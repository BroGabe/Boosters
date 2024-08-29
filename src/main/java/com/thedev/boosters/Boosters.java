package com.thedev.boosters;

import co.aikar.commands.PaperCommandManager;
import com.thedev.boosters.BoostersManager.BoosterManager;
import com.thedev.boosters.Commands.BoosterCommand;
import com.thedev.boosters.Listeners.BoosterRedeem;
import com.thedev.boosters.Menu.BoostersMenu;
import com.thedev.boosters.PlayerManager.PlayerJoinQuit;
import com.thedev.boosters.PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Boosters extends JavaPlugin {

    private static Boosters inst;

    private BoosterManager boosterManager;
    private PlayerManager playerManager;

    private BoostersMenu boostersMenu;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        boosterManager = new BoosterManager();
        playerManager = new PlayerManager(this, boosterManager);

        boostersMenu = new BoostersMenu(this);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);

        paperCommandManager.registerCommand(new BoosterCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinQuit(this), this);
        Bukkit.getPluginManager().registerEvents(new BoosterRedeem(this), this);

        inst = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }

    public BoostersMenu getBoostersMenu() {
        return boostersMenu;
    }

    public BoosterManager getBoosterManager() {
        return boosterManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static Boosters getInst() {
        return inst;
    }
}
