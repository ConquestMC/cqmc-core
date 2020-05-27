package com.conquestmc.core.model;

import com.conquestmc.core.friends.FriendRequest;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;

import com.mongodb.DBObject;
import lombok.Data;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ConquestPlayer {

    private UUID uuid;
    private String knownName;

    private Rank rank;
    private int coins;
    private long points;

    private List<FriendRequest> friendRequests = Lists.newArrayList();
    private List<UUID> friends = Lists.newArrayList();

    private Set<Statistic> statistics = new HashSet<>();

    public ConquestPlayer(UUID uuid, Document object) {
        this.uuid = uuid;
        this.rank = Rank.valueOf(object.get("rank").toString().toUpperCase());
        this.knownName = (String) object.get("knownName");
        this.coins = (int) object.get("coins");
        this.points = (long) object.get("points");

        for (Document stat : (List<Document>) object.get("stats")) {
            Statistic statistic = new Statistic((String)stat.get("name"), (int)stat.get("value"));
            this.statistics.add(statistic);
        }

        for (Document obj : (List<Document>) object.get("friends")) {
            UUID friend = UUID.fromString(String.valueOf(obj.get("_id")));
            this.friends.add(friend);
        }
    }

    public ConquestPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.knownName = name;
        this.coins = 0;
        this.points = 0;
        this.rank = Rank.NONE;
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
            DBObject object = new BasicDBObject("_id", s.getName()).append("value", s.getValue());
            stats.add(object);
        }

        for (FriendRequest request : this.friendRequests) {
            DBObject object = new BasicDBObject()
                    .append("from", request.getFrom())
                    .append("to", request.getTo());
            friendRequests.add(object);
        }

        Document object = new Document("_id", uuid.toString())
                .append("knownName", getKnownName())
                .append("stats", stats)
                .append("rank", rank.name())
                .append("coins", coins)
                .append("points", points)
                .append("friendRequests", friendRequests)
                .append("friends", friends.stream().map(f -> new BasicDBObject("_id", f.toString())).collect(Collectors.toList()));

        return object;
    }
}
