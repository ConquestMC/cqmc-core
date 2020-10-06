package com.conquestmc.core.achievement;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class EventEmitter<EventType> implements ObservableOnSubscribe<EventType> {

    /**
     * The bukkit skeleton listener.
     */
    private final Listener listener = new Listener() {
    };

    /**
     * The clazz of the event.
     */
    private final Class<? extends Event> eventClazz;

    /**
     * if the listener should ignore cancelled events.
     */
    private final boolean ignoreCancelled;

    /**
     * The priority of the listener.
     */
    private final EventPriority eventPriority;

    /**
     * The bukkit plugin manager.
     */
    private final PluginManager pluginManager;

    /**
     * The bukkit plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new event emitter.
     *
     * @param eventClazz      The class of the event..
     * @param ignoreCancelled If cancelled events should be ignored.
     * @param eventPriority   The event priority.
     * @param pluginManager   The bukkit pluginManager manager.
     * @param plugin          The bukkit plugin instance.
     */
    public EventEmitter(Class<? extends Event> eventClazz, boolean ignoreCancelled, EventPriority eventPriority, PluginManager pluginManager, Plugin plugin) {
        this.eventClazz = eventClazz;
        this.ignoreCancelled = ignoreCancelled;
        this.eventPriority = eventPriority;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @Override
    public void subscribe(ObservableEmitter<EventType> observableEmitter) {
        pluginManager.registerEvent(eventClazz, listener, eventPriority, (listener1, event) -> {
            if (eventClazz.isAssignableFrom(event.getClass())) {
                observableEmitter.onNext((EventType) event);
            }
        }, plugin, ignoreCancelled);
    }

    /**
     * Get the skeleton listener.
     *
     * @return The listener.
     */
    public Listener getListener() {
        return listener;
    }
}