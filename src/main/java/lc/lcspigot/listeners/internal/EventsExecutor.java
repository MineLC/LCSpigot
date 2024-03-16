package lc.lcspigot.listeners.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.tinylog.Logger;

import lc.lcspigot.listeners.EventListener;
import lc.lcspigot.listeners.ListenerData;

public final class EventsExecutor {

    private static final Map<Class<? extends Event>, List<EventListener>[]> LISTENERS = new HashMap<>();

    public final static void execute(final Event event) {
        final List<EventListener>[] listeners = LISTENERS.get(event.getClass());

        if (listeners == null) {
            return;
        }
        try {
            if (execute(event, EventPriority.LOWEST, listeners)
                || execute(event, EventPriority.LOW, listeners)
                || execute(event, EventPriority.NORMAL, listeners)
                || execute(event, EventPriority.HIGH, listeners)
                || execute(event, EventPriority.HIGHEST, listeners)
                || execute(event, EventPriority.MONITOR, listeners)) {
                return;
            }   
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    private final static boolean execute(final Event event, final EventPriority priority, final List<EventListener>[] eventListeners) {
        final List<EventListener> listeners = eventListeners[priority.ordinal()];

        if (listeners == null) {
            return false;
        }

        if (event instanceof Cancellable) {
            final Cancellable cancellable = (Cancellable)event;

            for (final EventListener listener : listeners) {
                listener.handle(event);
                if (cancellable.isCancelled()) {
                    return true;
                }
            }

            return false;
        }

        for (final EventListener listener : listeners) {
            listener.handle(event);
        }
        return false;
    }

    public final void addListener(Class<? extends Event> eventClass, EventListener listener, ListenerData data) {
        List<EventListener>[] listeners = LISTENERS.get(eventClass);
        if (listeners == null) {
            listeners = new ArrayList[6];
            LISTENERS.put(eventClass, listeners);
        }

        List<EventListener> eventListeners = listeners[data.priority().ordinal()];
        if (eventListeners == null) {
            eventListeners = new ArrayList<>();
            listeners[data.priority().ordinal()] = eventListeners;
        }

        eventListeners.add(listener);
    }
}