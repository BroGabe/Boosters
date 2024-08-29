package com.thedev.boosters.BoostersManager;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Boosters.EXPBooster;

import java.time.Instant;
import java.util.*;

public class BoosterManager {

    private final Map<UUID, Map<Class<? extends Booster>, Booster>> playerMap = new HashMap<>();

    public Optional<Booster> getPlayerBooster(UUID playerUUID, Class<? extends Booster> clazz) {
        return Optional.ofNullable(playerMap.getOrDefault(playerUUID, Collections.emptyMap()).get(clazz));
    }

    public List<Booster> getPlayerBoosters(UUID playerUUID) {
        return new ArrayList<>(playerMap.getOrDefault(playerUUID, Collections.emptyMap()).values());
    }

    public void addPlayerBooster(UUID playerUUID, Booster booster) {
        playerMap.computeIfAbsent(playerUUID, k -> new HashMap<>())
                .compute(booster.getClass(), (clazz, boosterValue) -> boosterValue == null ? booster : boosterValue.merge(booster));
    }

    public Map<UUID, Map<Class<? extends Booster>, Booster>> getPlayerMap() {
        return playerMap;
    }

    public static Optional<Booster> getBoosterFromString(String string, double permMulti) {
        return getBoosterFromString(string, permMulti, 0.0, Instant.now().toEpochMilli());
    }

    public static Optional<Booster> getBoosterFromString(String string, double permMulti, double timedMulti, long expiration) {
        switch (string.toUpperCase()) {
            case "EXPBOOSTER":
                return Optional.of(new EXPBooster(Boosters.getInst(), permMulti, 0.0, timedMulti, Instant.ofEpochMilli(expiration)));
        }

        return Optional.empty();
    }

    public static Optional<Booster> getBoosterFromString(String string, double permMulti, List<String> timedBoosters) {
        switch (string.toUpperCase()) {
            case "EXPBOOSTER":
                return Optional.of(new EXPBooster(Boosters.getInst(), permMulti, 0.0, timedBoosters));
        }

        return Optional.empty();
    }
}
