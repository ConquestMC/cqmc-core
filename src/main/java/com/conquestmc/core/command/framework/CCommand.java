package com.conquestmc.core.command.framework;

import org.bukkit.command.CommandSender;

public interface CCommand {

    boolean execute(CommandSender sender, String[] args);
}
