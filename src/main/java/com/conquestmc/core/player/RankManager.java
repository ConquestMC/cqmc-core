package com.conquestmc.core.player;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public class RankManager {

    private Map<String, Rank> ranks = Maps.newHashMap();

    public RankManager() {
        Rank none = new Rank("none");
        none.addPermissions("group.default");
        Rank knight = new DonationRank("knight");
        knight.setPrefix("&bKnight");
        Rank duke = new DonationRank("duke");
        duke.setPrefix("&aDuke");
        Rank lord = new DonationRank("lord");
        lord.setPrefix("&dLord");
        Rank king = new DonationRank("king");
        king.setPrefix("&6King");

        Rank emperor = new DonationRank("emperor");
        emperor.setPrefix("&6&lEmperor");

        Rank media = new DonationRank("media");
        media.setPrefix("&5&lMedia");

        Rank trial = new DonationRank("trial");
        trial.setPrefix("&3&lTrial");
        Rank mod = new StaffRank("mod", Arrays.asList(trial));
        mod.setPrefix("&2&lMod");

        Rank srmod = new StaffRank("srmod", Arrays.asList(trial));
        srmod.setPrefix("&2&lSrMod");

        Rank admin = new StaffRank("admin", Arrays.asList(mod));
        admin.setPrefix("&c&lAdmin");
        admin.addPermissions("core.gamemode", "core.gamemode.others", "staff.punish");

        Rank manager = new StaffRank("manager", Arrays.asList(admin));
        manager.setPrefix("&4&lManager");
        manager.addPermission("core.setrank");

        Rank dev = new StaffRank("dev", Arrays.asList(manager));
        dev.setPrefix("&5&lDev");
        Rank owner = new StaffRank("owner", Arrays.asList(dev, media, king, lord, knight, duke, none));
        owner.setPrefix("&4&lOwner");

        ranks.put("none", none);
        ranks.put("knight", knight);
        ranks.put("duke", duke);
        ranks.put("lord", lord);
        ranks.put("king", king);
        ranks.put("media", media);
        ranks.put("trial", trial);
        ranks.put("mod", mod);
        ranks.put("srmod", srmod);
        ranks.put("admin", admin);
        ranks.put("manager", manager);
        ranks.put("dev", dev);
        ranks.put("owner", owner);
    }

    public Rank getRank(String name) {
        return ranks.get(name);
    }
}
