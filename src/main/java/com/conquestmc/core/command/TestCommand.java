package com.conquestmc.core.command;

import com.conquestmc.core.command.framework.CCommand;
import com.conquestmc.core.command.framework.CommandInfo;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "/test", description = "test command")
public class TestCommand implements CCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Got your command!");
        return true;
    }
}
