package com.conquestmc.core.punishments;

import com.conquestmc.core.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class PunishmentHistoryCommand implements CommandExecutor {

    private final PunishmentManager punishmentManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player sender = (Player) commandSender;
        if (commandSender.hasPermission("staff.ph")) {
            if (args.length == 1) {
                String playerName = args[0];
                Player player = Bukkit.getPlayer(playerName);

                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "That player is not online at this time!");
                    return true;
                }

                List<Punishment> history = punishmentManager.getPunishmentHistory(player.getUniqueId());
                Inventory inv = Bukkit.createInventory(null, 9*6, "Punish History: " + playerName);

                for (Punishment p : history) {
                    Material mat = null;
                    if (p.getType() == PunishmentType.GAMEPLAY) {
                        mat = Material.ANVIL;
                    }
                    else if (p.getType() == PunishmentType.HACKING) {
                        mat = Material.GOLD_SWORD;
                    }
                    else if (p.getType() == PunishmentType.CHAT) {
                        mat = Material.FLINT_AND_STEEL;
                    }
                    else {
                        mat = Material.PAPER;
                    }

                    ItemStack punishItem = new ItemStack(mat, 1);
                    ItemMeta meta = punishItem.getItemMeta();
                    meta.setDisplayName(ChatColor.AQUA + WordUtils.capitalizeFully(p.getType().name()));
                    meta.setLore(Arrays.asList(
                            ChatColor.GOLD + "Date Issued: " + ChatColor.YELLOW + p.getIssued(),
                            ChatColor.GOLD + "Severity: " + ChatColor.YELLOW + p.getSeverity(),
                            ChatColor.GOLD + "Reason: " + ChatColor.YELLOW + "Coming soon.",
                            ChatColor.GOLD + "Duration: " + ChatColor.YELLOW + TimeUtil.formatTimeToFormalDate(p.getActiveUntil()),
                            ChatColor.GOLD + "Time Left: " + ChatColor.YELLOW + p.getTimeLeftVisual()
                    ));
                    punishItem.setItemMeta(meta);
                    inv.addItem(punishItem);
                }
                sender.openInventory(inv);
            }
        }
        return false;
    }
}
