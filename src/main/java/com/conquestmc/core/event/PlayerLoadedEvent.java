package com.conquestmc.core.event;

import com.conquestmc.core.model.ConquestPlayer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLoadedEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter
    private ConquestPlayer player;

    public PlayerLoadedEvent(ConquestPlayer player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
