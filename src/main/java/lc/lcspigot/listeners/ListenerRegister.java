package lc.lcspigot.listeners;

import java.lang.reflect.Method;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.tinylog.Logger;

import lc.lcspigot.listeners.internal.EventsExecutor;


public final class ListenerRegister {
    private static final EventExecutor EXECUTOR = (listener, event) -> ((EventListener)listener).handle(event);

    private final JavaPlugin plugin;

    public ListenerRegister(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(final EventListener listener, final boolean useOldRegister) {
        final ListenerData data = getData(listener);
        if (data == null) {
            Logger.warn("Can't register the listener of the class " + listener.getClass().getName() + " because don't contains ListenerData annontation");
            return;
        }
        if (useOldRegister) {
            plugin.getServer().getPluginManager().registerEvent(data.event(), listener, data.priority(), EXECUTOR, plugin);
        } else {
            new EventsExecutor().addListener(data.event(), listener, data);
        }
    }

    public void cancelEvent(final Class<? extends Event> event) {
        fastListener(event, new EventListener() {
            @Override
            public void handle(Event defaultEvent) {
                ((Cancellable)defaultEvent).setCancelled(true);
            }
        });
    }

    public void fastListener(final Class<? extends Event> event, final EventListener listener) {
        plugin.getServer().getPluginManager().registerEvent(
            event,
            listener,
            EventPriority.LOWEST,
            EXECUTOR,
            plugin
        );
    }

    private ListenerData getData(final EventListener listener) {
        final Method[] methods = listener.getClass().getMethods();
        for (final Method method : methods) {
            final ListenerData data = method.getAnnotation(ListenerData.class);
            if (data == null) {
                continue;
            }
            return data;
        }
        return null;
    }
}