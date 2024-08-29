package com.thedev.boosters.BoostersManager;

import com.thedev.boosters.Boosters;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.*;

public class Booster {

    private final Boosters plugin;

    private double permanentMulti;

    private double tempMulti;

    private final Map<Instant, Double> timedBoostersMap = new HashMap<>();

    private BukkitTask timerTask;

    public Booster(Boosters plugin, double permanentMulti) {
        this(plugin, permanentMulti, 0.0);
    }

    public Booster(Boosters plugin, double permanentMulti, double tempMulti) {
        this(plugin, permanentMulti, tempMulti, 0.0, Instant.now());
    }

    public Booster(Boosters plugin, double permanentMulti, double tempMulti, double timedMulti, Instant timer) {
        this.plugin = plugin;
        this.permanentMulti = permanentMulti;
        this.tempMulti = tempMulti;

        if(timedMulti > 0.0 && timer.toEpochMilli() > Instant.now().toEpochMilli()) {
            timedBoostersMap.put(timer, timedMulti);
            startTimerTask();
        }
    }

    public Booster(Boosters plugin, double permanentMulti, double tempMulti, List<String> timedMultiList) {
        this(plugin, permanentMulti, tempMulti);
        assignTimedMulti(timedMultiList);
    }

    public final double getMulti() {
        return getPermanentMulti() + getTempMulti() + getTimedMulti();
    }

    public final double getTimedMulti() {
        double finalMulti = 0.0;

        for(Double timedMultiplier : timedBoostersMap.values()) {
            finalMulti = (finalMulti + timedMultiplier);
        }

        return finalMulti;
    }

    public final double getPermanentMulti() {
        return permanentMulti;
    }

    public final double getTempMulti() {
        return tempMulti;
    }

    public final void assignTimedMulti(List<String> timersList) {
        for(String timerString : timersList) {
            String[] splitTimer = timerString.split(":");
            Instant timer = Instant.ofEpochMilli(Long.parseLong(splitTimer[0]));
            double multiplier = Double.parseDouble(splitTimer[1]);
            timedBoostersMap.put(timer, multiplier);
        }
    }

    public final List<String> timedMapToString() {
        List<String> timedMapList = new ArrayList<>();

        for(Map.Entry<Instant, Double> entry : timedBoostersMap.entrySet()) {
            timedMapList.add(entry.getKey().toEpochMilli() + ":" + entry.getValue());
        }

        return timedMapList;
    }

    public final boolean canMerge(Booster booster) {
        FileConfiguration config = plugin.getConfig();
        double newMulti = (tempMulti + booster.getTempMulti()) +
                (permanentMulti + booster.getPermanentMulti()) + (getTimedMulti() + booster.getTimedMulti());

        if(config.getDouble("booster-limits." + getClass().getSimpleName()) <= 0.0) {
            return (newMulti > plugin.getConfig().getDouble("default-booster-limit"));
        }


        return (newMulti > config.getDouble("booster-limits." + getClass().getSimpleName()));
    }

    public void startTimerTask() {
        if(timerTask != null && Bukkit.getScheduler().isCurrentlyRunning(timerTask.getTaskId())) {
            timerTask.cancel();
        }

        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(timedBoostersMap.isEmpty()) {
                timerTask.cancel();
            }
            timedBoostersMap.keySet().removeIf(expiration -> Instant.now().isAfter(expiration));
        }, 20, 20);
    }

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    // If a player already has a booster, it will merge the multipliers
    // For timed boosters look
    public final Booster merge(Booster booster) {
        this.tempMulti = getTempMulti() + booster.getTempMulti();
        this.permanentMulti = getPermanentMulti() + booster.permanentMulti;

        if(booster.getTimerTask() != null) {
            booster.getTimerTask().cancel();
        }

        timedBoostersMap.putAll(booster.timedBoostersMap);

        if(!timedBoostersMap.isEmpty()) {
            startTimerTask();
        }

        return this;
    }

}
