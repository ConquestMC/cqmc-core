package com.conquestmc.core.bossbar;

import com.conquestmc.core.CorePlugin;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BossBar1_8 extends BossBar {

    private HashMap<UUID, EntityWither> withers = new HashMap<>();

    public BossBar1_8(String text) {
        super(text);
        new BukkitRunnable() {
            @Override
            public void run() {
                exec();
            }
        }.runTaskTimer(CorePlugin.getInstance(), 0L, 10L);
    }

    public void addPlayer(String title, Player p) {
        EntityWither wither = new EntityWither(((CraftWorld) p.getWorld()).getHandle());
        Location l = getWitherLocation(p.getLocation());
        wither.setCustomName(title);
        wither.setInvisible(true);
        wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        withers.put(p.getUniqueId(), wither);
    }

    public void removePlayer(Player p) {
        EntityWither wither = withers.remove(p);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] {wither.getId()});
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public void setTitle(String title) {
        //this.title = title;
        for (Map.Entry<UUID, EntityWither> entry : withers.entrySet()) {
            EntityWither wither = entry.getValue();
            wither.setCustomName(title);
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
            ((CraftPlayer) Bukkit.getPlayer(entry.getKey())).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void setProgress(double progress) {
        for (Map.Entry<UUID, EntityWither> entry : withers.entrySet()) {
            EntityWither wither = entry.getValue();
            wither.setHealth((float) (progress * wither.getMaxHealth()));
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true);
            ((CraftPlayer) Bukkit.getPlayer(entry.getKey())).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public Location getWitherLocation(Location l) {
        return l.add(l.getDirection().multiply(60));
    }

    public void exec() {
        for (Map.Entry<UUID, EntityWither> en : withers.entrySet()) {
            EntityWither wither = en.getValue();
            Location l = getWitherLocation(Bukkit.getPlayer(en.getKey()).getLocation());
            wither.setLocation(l.getX(), l.getY(), l.getZ(), 0, 0);
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(wither);
            ((CraftPlayer)Bukkit.getPlayer(en.getKey())).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void send(Player player) {
        addPlayer(this.getText(), player);
    }

    @Override
    public void remove(Player player) {
        removePlayer(player);
    }
}
