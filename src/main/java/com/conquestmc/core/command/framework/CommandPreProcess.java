package com.conquestmc.core.command.framework;

import com.conquestmc.core.CorePlugin;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandPreProcess implements Listener {

    @EventHandler
    public void onPreProcess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        String cmd = args[0];

        if (!CorePlugin.getInstance().getCommandMap().keySet().contains(cmd)) {
            return;
        }
        CommandSender sender = event.getPlayer();
        if (handle(sender, args)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onConsoleProcess(ServerCommandEvent event) {
        String[] args = event.getCommand().split(" ");
        String cmd = args[0];

        if (!CorePlugin.getInstance().getCommandMap().keySet().contains(cmd)) {
            return;
        }
        handle(event.getSender(), event.getCommand().split(" "));
    }
    private List<CCommand> getMatchingCommands(String arg) {
        List<CCommand> result = new ArrayList<>();
        for (Map.Entry<String, CCommand> entry : CorePlugin.getInstance().getCommandMap().entrySet()) {
            if (arg.toLowerCase().matches(entry.getKey())) {
                result.add(entry.getValue());
            }
        }

        return result;
    }
    private String[] trimFirstArg(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    public boolean handle(CommandSender sender, String[] args) {

        List<CCommand> matches = getMatchingCommands(args[0]);

        // Because we use regex patterns for the command arguments, we need to
        // make sure that there are no conflicting matches.
        if (matches.size() > 1) {
            sender.sendMessage("There are multiple commands that match this!");
            return true;
        }

        if (matches.size() == 0) {
            sender.sendMessage(ChatColor.RED + "Cannot find this command!");
            return false;
        }

        CCommand command = matches.get(0);
        CommandPermission perm = command.getClass().getAnnotation(
                CommandPermission.class);

        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

        if (info.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This is a player only command!");
            return true;
        }

        if (perm != null) {
            if (!sender.hasPermission(perm.permission())) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
        }

        String[] params = trimFirstArg(args);


        if (!command.execute(sender, params)) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage!");
            return true;
        }
        return true;
    }

    boolean checkCommand(String cmd) {
        boolean isCommand = false;

        if (Bukkit.getServer().getPluginCommand(cmd) != null || CorePlugin.getInstance().getCommand(cmd) != null) {
            return true;
        }
        System.out.println(cmd);

        for (String[] s : Bukkit.getServer().getCommandAliases().values()) {
            for (String sub : s) {
                System.out.println(sub);
                if (sub.equalsIgnoreCase(cmd.replace("/", ""))) {
                    isCommand = true;
                }
            }
        }
        return isCommand;
    }
}
