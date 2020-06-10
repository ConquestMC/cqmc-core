package com.conquestmc.core.punishments;

import com.conquestmc.core.model.ConquestPlayer;
import com.google.common.collect.Lists;
import com.mongodb.DBCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class PunishmentManager {

    private final MongoDatabase database;
    private final MongoCollection<Document> punishmentCollection;

    public PunishmentManager(MongoDatabase database) {
        this.database = database;
        this.punishmentCollection = database.getCollection("punishments");
    }

    public void punishPlayer(UUID uuid, PunishmentType type, int severity) {
        long punishedUnitl = getDuration(type, severity);
        boolean perm = false;
        if (punishedUnitl == -1) {
            perm = true;
        }
        Punishment p = new Punishment(uuid, UUID.randomUUID(), type);
        p.setPerm(perm);
        p.setActiveUntil(punishedUnitl);

        Player punish = Bukkit.getPlayer(uuid);
        if (type == PunishmentType.GAMEPLAY || type == PunishmentType.HACKING) {
            //BAN so kick them.
            punish.kickPlayer(ChatColor.RED + "You have been banned for a " + WordUtils.capitalizeFully(type.name()) + " offence!");
        }
        this.punishmentCollection.insertOne(p.getDBObject());
    }

    public List<Punishment> getPunishmentHistory(UUID uuid) {
        List<Punishment> history = Lists.newArrayList();
        FindIterable<Document> findIterable = punishmentCollection.find(eq("uuid", uuid.toString()));

        for (Document d : findIterable) {
            Punishment p = new Punishment(d);
            history.add(p);
        }

        return history;
    }

    public List<Punishment> getPunishmentHistory(ConquestPlayer player) {
        return getPunishmentHistory(player.getUuid());
    }

    public boolean isMuted(UUID uuid) {
        for (Punishment p : getPunishmentHistory(uuid)) {
            if (p.getType() == PunishmentType.CHAT) {

                if (p.getActiveUntil() > System.currentTimeMillis() || p.getActiveUntil() == -1 || p.isPerm()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameBanned(UUID uuid) {
        for (Punishment p : getPunishmentHistory(uuid)) {
            if (p.getType() == PunishmentType.GAMEPLAY || p.getType() == PunishmentType.HACKING) {
                if (p.getActiveUntil() > System.currentTimeMillis() || p.getActiveUntil() == -1 || p.isPerm()) {
                    return true;
                }
            }
        }
        return false;
    }

    public long getDuration(PunishmentType type, int severity) {
        long now = System.currentTimeMillis();
        long hour = now * 3600;
        switch (type) {
            case HACKING:
                if (severity == 1) {
                    return hour * 24 * 7;
                } else if (severity == 2) {
                    return hour * 24 * 30;

                } else if (severity == 3) {
                    return hour * 24 * 45;

                } else {
                    return -1;
                }
            case GAMEPLAY:
                if (severity == 1) {
                    return hour * 24;
                } else if (severity == 2) {
                    return hour * 24 * 7;

                } else {
                    return -1;
                }
            case CHAT:
                if (severity == 1) {
                    return hour * 6;
                }
                if (severity == 2) {
                    return hour * 24 * 2;
                }
                if (severity == 3) {
                    return hour * 24 * 30;
                } else {
                    return -1;
                }
            case OTHER:
                return -1;
            case REPORT:
                return -1;
            default:
                return 0;
        }
    }
}
