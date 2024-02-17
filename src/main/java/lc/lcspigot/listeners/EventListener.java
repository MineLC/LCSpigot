package lc.lcspigot.listeners;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public interface EventListener extends Listener {
    void handle(Event defaultEvent);
}