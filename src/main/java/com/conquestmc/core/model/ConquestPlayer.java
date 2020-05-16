package com.conquestmc.core.model;

import com.conquestmc.core.CorePlugin;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.mongodb.morphia.annotations.Entity;

import java.beans.ConstructorProperties;
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

    public ConquestPlayer(UUID uuid, DBObject object) {
        this.uuid = uuid;
        this.rank = Rank.valueOf((String) object.get("rank"));
        this.knownName = (String) object.get("knownName");
        this.coins = (int) object.get("coins");
        this.points = (long) object.get("points");

        for (DBObject friendReq : (List<DBObject>) object.get("friendRequests")) {
            FriendRequest req = new FriendRequest(UUID.fromString((String) friendReq.get("from")), UUID.fromString((String) friendReq.get("to")));
            req.setAccepted((Boolean) friendReq.get("accepted"));
            this.friendRequests.add(req);
        }

        for (DBObject stat : (List<DBObject>) object.get("stats")) {
            Statistic statistic = new Statistic((String)stat.get("name"), (int)stat.get("value"));
            this.statistics.add(statistic);
        }

        for (DBObject obj : (List<DBObject>) object.get("friends")) {
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

    public void sendFriendRequest(Player target) {
        if (target == null) {
            return;
        }
        UUID tUUID = target.getUniqueId();
        FriendRequest request = new FriendRequest(uuid, tUUID);
        this.friendRequests.add(request);
        CorePlugin.getInstance().getPlayer(tUUID).friendRequests.add(request);

        String accept =  ChatColor.GREEN + ChatColor.BOLD.toString() + "ACCEPT";
        String beggining = ChatColor.YELLOW + getKnownName() + ChatColor.GRAY +
                " has requested to be friends! ";
        String decline = ChatColor.RED + ChatColor.BOLD.toString() + " DECLINE";

        TextComponent acceptComp = new TextComponent(accept);
        acceptComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f accept"));

        TextComponent declineComp = new TextComponent(decline);
        declineComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f decline"));

        TextComponent msg = new TextComponent(beggining);
        msg.addExtra(acceptComp);
        msg.addExtra(declineComp);
        target.spigot().sendMessage(msg);
        target.playSound(target.getLocation(), Sound.LEVEL_UP, 1, 1);
    }

    public List<FriendRequest> getIncomingFriendRequests() {
        return friendRequests.stream().filter(req -> req.getTo().toString().equals(uuid.toString())).collect(Collectors.toList());
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

    public DBObject getMongoObject() {
        List<DBObject> stats = Lists.newArrayList();
        List<DBObject> friendRequests = Lists.newArrayList();

        for (Statistic s : statistics) {
            DBObject object = new BasicDBObject("_id", s.getName()).append("value", s.getValue());
            stats.add(object);
        }

        for (FriendRequest request : this.friendRequests) {
            DBObject object = new BasicDBObject()
                    .append("from", request.getFrom().toString())
                    .append("to", request.getTo().toString())
                    .append("accepted", request.isAccepted());
            friendRequests.add(object);
        }

        DBObject object = new BasicDBObject("_id", uuid.toString())
                .append("knownName", getKnownName())
                .append("stats", stats)
                .append("coins", coins)
                .append("points", points)
                .append("friendRequests", friendRequests)
                .append("friends", friends.stream().map(f -> new BasicDBObject("_id", f.toString())).collect(Collectors.toList()));

        return object;
    }
}
