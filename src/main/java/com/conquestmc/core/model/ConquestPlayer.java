package com.conquestmc.core.model;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.friends.FriendRequest;
import com.conquestmc.core.player.Rank;
import com.conquestmc.core.player.StaffRank;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;

import com.mongodb.DBObject;
import lombok.Data;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ConquestPlayer {

    private UUID uuid;
    private String knownName;

    private List<Rank> ranks = Lists.newArrayList();
    private int coins;
    private long points;

    private SimpleScoreboard currentScoreboard;

    private List<FriendRequest> friendRequests = Lists.newArrayList();
    private List<UUID> friends = Lists.newArrayList();

    private Set<Statistic> statistics = new HashSet<>();

    private Set<Trophy> trophies = Sets.newHashSet();

    private Rank prefixedRank;

    public ConquestPlayer(UUID uuid, Document object) {
        this.uuid = uuid;
        JsonObject jsonObject = new JsonParser().parse(object.toJson()).getAsJsonObject();
        JsonArray array = jsonObject.getAsJsonArray("ranks");

        for (JsonElement element : array) {
            String name = element.getAsString();
            Rank r = CorePlugin.getInstance().getRankManager().getRank(name);

            if (r instanceof StaffRank) {
                prefixedRank = r;
            }

            this.ranks.add(CorePlugin.getInstance().getRankManager().getRank(name));
        }

        if (prefixedRank == null) {
            this.prefixedRank = ranks.get(0);
        }

        this.knownName = (String) object.get("knownName");
        this.coins = (int) object.get("coins");
        this.points = (long) object.get("points");

        for (Document stat : (List<Document>) object.get("stats")) {
            Statistic statistic = new Statistic((String) stat.get("name"), (int) stat.get("value"));
            this.statistics.add(statistic);
        }

        for (Document obj : (List<Document>) object.get("friends")) {
            UUID friend = UUID.fromString(String.valueOf(obj.get("_id")));
            this.friends.add(friend);
        }
    }

    public ConquestPlayer(UUID uuid) {
        this.uuid = uuid;
        this.coins = 0;
        this.points = 0;
        this.ranks.add(CorePlugin.getInstance().getRankManager().getRank("none"));
        this.prefixedRank = ranks.get(0);
    }

    public void sendFriendRequest(String targetName) {
        FriendRequest request = new FriendRequest(knownName, targetName);
        this.friendRequests.add(request);

        request.send();
    }

    public void removeFriendRequest(String to) {
        FriendRequest rem = null;
        for (FriendRequest request : getFriendRequests()) {
            if (request.getTo().equalsIgnoreCase(to)) {
                rem = request;
            }
        }
        if (rem != null) {
            this.getFriendRequests().remove(rem);
        }
    }

    public List<FriendRequest> getIncomingFriendRequests() {
        return friendRequests.stream().filter(req -> req.getTo().equals(uuid.toString())).collect(Collectors.toList());
    }

    public String getCompetitiveRankName() {
        if (points >= 0 && points <= 2499) {
            return "Noobie";
        } else if (points >= 2500 && points <= 9999) {
            return "Still Learning";
        } else if (points >= 10000 && points <= 24999) {
            return "Needs Practice";
        } else if (points >= 25000 && points <= 99999) {
            return "Elite";
        } else if (points >= 100000 && points <= 199999) {
            return "Elite II";
        } else if (points >= 200000 && points <= 299999) {
            return "Elite III";
        } else if (points >= 300000 && points <= 399999) {
            return "Champion I";
        } else if (points >= 400000 && points <= 499999) {
            return "Champion II";
        } else if (points >= 500000 && points <= 599999) {
            return "Champion III";
        } else if (points >= 600000 && points <= 699999) {
            return "MASTER I";
        } else if (points >= 700000 && points <= 799999) {
            return "MASTER II";
        } else if (points >= 800000 && points <= 949000) {
            return "MASTER III";
        } else {
            return "GRAND MASTER";
        }
    }

    public void awardPoints(int points) {
        setPoints(getPoints() + points);
    }

    public void sendPointsAwardedMessage(int awarded) {
        getBukkitPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You've earned " + ChatColor.RED + "" + ChatColor.BOLD + awarded + ChatColor.GOLD + "" + ChatColor.BOLD + " Conquest Points!");
    }

    public Statistic getStatistic(String name) {
        for (Statistic statistic : statistics) {
            if (statistic.getName().equalsIgnoreCase(name)) {
                return statistic;
            }
        }
        return null;
    }


    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Document getMongoObject() {
        List<DBObject> stats = Lists.newArrayList();
        List<DBObject> friendRequests = Lists.newArrayList();

        for (Statistic s : statistics) {
            DBObject object = new BasicDBObject("name", s.getName()).append("value", s.getValue());
            stats.add(object);
        }

        for (FriendRequest request : this.friendRequests) {
            DBObject object = new BasicDBObject()
                    .append("from", request.getFrom())
                    .append("to", request.getTo());
            friendRequests.add(object);
        }

        List<String> rankNames = Lists.newArrayList();
        for (Rank r : ranks) {
            rankNames.add(r.getName());
        }

        Document object = new Document("uuid", getUuid().toString())
                .append("knownName", getKnownName())
                .append("stats", stats)
                .append("ranks", rankNames)
                .append("coins", coins)
                .append("points", points)
                .append("friendRequests", friendRequests)
                .append("friends", friends.stream().map(f -> new BasicDBObject("_id", f.toString())).collect(Collectors.toList()));

        return object;
    }

    public SimpleScoreboard getCurrentScoreboard() {
        return currentScoreboard;
    }

    public void setCurrentScoreboard(SimpleScoreboard scoreboard) {
        this.currentScoreboard = scoreboard;
    }

    public boolean hasRank(Rank rank) {
        for (Rank r : ranks) {
            if (r.getName().equalsIgnoreCase(rank.getName())) {
                return true;
            }
        }
        return false;
    }

    public void updatePrefixedRank() {
        this.prefixedRank = null;
        for (Rank rank : ranks) {
            if (rank instanceof StaffRank) {
                this.prefixedRank = rank;
            }
        }
        if (prefixedRank == null) {
            this.prefixedRank = ranks.get(0);
        }
    }
}
