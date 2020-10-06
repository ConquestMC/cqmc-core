package com.conquestmc.core.achievement;

import com.conquestmc.core.CorePlugin;
import io.reactivex.Observable;
import org.bukkit.entity.Player;

public interface Achievement {

    Observable<Player> observe();
    String getName();
    void onAchieve(Player player);

    default CorePlugin getPlugin() {
        return CorePlugin.getInstance();
    }
}
