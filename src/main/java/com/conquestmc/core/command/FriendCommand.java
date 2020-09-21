package com.conquestmc.core.command;

import com.conquestmc.core.CorePlugin;
import com.conquestmc.core.friends.FriendRequest;
import com.conquestmc.core.model.ConquestPlayer;
import com.conquestmc.core.server.ServerManager;
import com.conquestmc.core.util.ChatUtil;
import com.conquestmc.core.util.SkullMaker;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
public class FriendCommand implements CommandExecutor {

    private final CorePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player pl = (Player) sender;

        if (args.length == 0) {
            pl.sendMessage(ServerManager.FRIENDS_PREFIX);
            pl.sendMessage(ChatUtil.color("&e - &7/friends"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends add [name]"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends accept [name]"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends decline [name]"));

            pl.sendMessage(ChatUtil.color("&e - &7/friends del|delete [name]"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends list"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends requests"));
            pl.sendMessage(ChatUtil.color("&e - &7/friends on/off"));
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&c/f add [player]"));
                return true;
            }

            plugin.getPlayer(pl).sendFriendRequest(args[1]);
        } else if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&c/f delete [player]"));
            }
            ConquestPlayer send = plugin.getPlayer(pl);
            Player target = Bukkit.getPlayer(args[1]);
            if (!send.getFriends().contains(target.getUniqueId())) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&cThis person could not be found on your friends list!"));
                return true;
            }
            send.getFriends().remove(target.getUniqueId());
            pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&1removed &7" + args[1] + " &1from your friends list!"));
        } else if (args[0].equalsIgnoreCase("list")) {
            ConquestPlayer player = plugin.getPlayer(pl);
            Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Your Friends");

            for (UUID uuid : player.getFriends()) {
                plugin.findPlayer(uuid).whenComplete((d, err) -> {
                    if (err != null) {
                        System.err.println(err.toString());
                    }
                    String name = d.getString("knownName");
                    String display = plugin.isPlayerOnNetwork(name) ? ChatColor.GREEN + name : ChatColor.RED + name;
                    ItemStack skull = new SkullMaker().withOwner(name).withName(display).build();
                    inv.addItem(skull);
                });
            }

            pl.openInventory(inv);
        } else if (args[0].equalsIgnoreCase("decline")) {
            if (args.length != 2) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&c/f decline [from]"));
                return true;
            }
            String from = args[1];
            FriendRequest toDecline = null;
            for (FriendRequest request : plugin.getPlayer(pl).getFriendRequests()) {
                if (request.getFrom().equalsIgnoreCase(from)) {
                    toDecline = request;
                }
            }
            if (toDecline == null) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&cYou do not have a friend request from " + from + "!"));
                return true;
            }

            toDecline.changeStatus("declined");
            plugin.getPlayer(pl).removeFriendRequest(pl.getName());
        } else if (args[0].equalsIgnoreCase("accept")) {
            System.out.println(args.toString()); //Todo Remove if not needed
            if (args.length != 2) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&c/f accept [from]"));
                return true;
            }
            String from = args[1];
            FriendRequest toAccept = null;
            for (FriendRequest request : plugin.getPlayer(pl).getFriendRequests()) {
                if (request.getFrom().equalsIgnoreCase(from)) {
                    toAccept = request;
                }
            }
            if (toAccept == null) {
                pl.sendMessage(ServerManager.FRIENDS_PREFIX + ChatUtil.color("&cYou do not have a friend request from " + from + "!"));
                return true;
            }

            toAccept.changeStatus("accepted");
            plugin.getPlayer(pl).removeFriendRequest(pl.getName());
            plugin.getPlayer(pl).getFriends().add(toAccept.getFromUUID());
        }
        return true;
    }
}
