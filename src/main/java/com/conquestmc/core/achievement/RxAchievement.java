package com.conquestmc.core.achievement;

import com.conquestmc.core.CorePlugin;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import javafx.event.EventType;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class RxAchievement {

    private Plugin plugin;
    private PluginManager pluginManager;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public RxAchievement(Plugin plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz) {
        return observeEvent(eventClazz, EventPriority.NORMAL);
    }

    /**
     * Observe on a specific event with a given event priority.
     *
     * @param eventClazz    The class of the event.
     * @param eventPriority The event priority.
     * @param <EventType>   The type of the event.
     * @return The observable.
     */
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority) {
        return observeEvent(eventClazz, eventPriority, false);
    }

    /**
     * Observe on a specific event and ignore cancelled events or not.
     *
     * @param eventClazz      The class of the event.
     * @param ignoreCancelled If we should ignore cancelled events.
     * @param <EventType>     The type of the event.
     * @return The observable.
     */
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, boolean ignoreCancelled) {
        return observeEvent(eventClazz, EventPriority.NORMAL, ignoreCancelled);
    }

    /**
     * Observe on a specific event with a given event priority and ignore cancelled events or not.
     *
     * @param eventClazz      The class of the event.
     * @param eventPriority   The event priority.
     * @param ignoreCancelled If we should ignore cancelled events.
     * @param <EventType>     The type of the event.
     * @return The observable.
     */
    public <EventType extends Event> Observable<EventType> observeEvent(Class<EventType> eventClazz, EventPriority eventPriority, boolean ignoreCancelled) {
        return observeEvent(new EventEmitter<EventType>(eventClazz, ignoreCancelled, eventPriority, pluginManager, plugin));
    }

    /**
     * Create an observable based on the given emitter.
     *
     * @param eventEmitter The emitter.
     * @param <EventType>  The type of the event.
     * @return The observable.
     */
    private <EventType extends Event> Observable<EventType> observeEvent(EventEmitter<EventType> eventEmitter) {
        return Observable.create(eventEmitter)
                .doOnSubscribe(compositeDisposable::add)
                .doOnDispose(() -> HandlerList.unregisterAll(eventEmitter.getListener()));
    }

    /**
     * Clean da shiat up.
     */
    public void reset() {
        compositeDisposable.dispose();
    }
}
