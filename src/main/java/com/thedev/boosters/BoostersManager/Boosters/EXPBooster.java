package com.thedev.boosters.BoostersManager.Boosters;

import com.thedev.boosters.Boosters;
import com.thedev.boosters.BoostersManager.Booster;

import java.time.Instant;
import java.util.List;

public class EXPBooster extends Booster {

    public EXPBooster(Boosters plugin, double permanentMulti) {
        super(plugin, permanentMulti);
    }

    public EXPBooster(Boosters plugin, double permanentMulti, double tempMulti) {
        super(plugin, permanentMulti, tempMulti);
    }

    public EXPBooster(Boosters plugin, double permanentMulti, double tempMulti, double timedMulti, Instant timer) {
        super(plugin, permanentMulti, tempMulti, timedMulti, timer);
    }

    public EXPBooster(Boosters plugin, double permanentMulti, double tempMulti, List<String> timedMultiList) {
        super(plugin, permanentMulti, tempMulti, timedMultiList);
    }
}
