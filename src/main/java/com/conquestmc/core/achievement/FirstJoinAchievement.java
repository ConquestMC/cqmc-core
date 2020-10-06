package com.conquestmc.core.achievement;

import io.reactivex.Observable;
import javafx.event.EventType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinAchievement implements Achievement {

    @Override
    public Observable<Player> observe() {
        Observable<Player> observable = getPlugin().getRxRegister().observeEvent(PlayerJoinEvent.class).map(PlayerEvent::getPlayer);

        observable.subscribe(this::onAchieve);
        return observable;
    }

    @Override
    public String getName() {
        return "First Join";
    }

    @Override
    public void onAchieve(Player player) {
        player.sendMessage("First join!");
    }
}
