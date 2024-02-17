package lc.lcspigot.events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PreInteractEntityEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel = false;
    private final int entityID;
    private final double width, heigth, depth;
    private final World world;

    public PreInteractEntityEvent(int entityId, double width, double height, double depth, World world) {
        this.entityID = entityId;
        this.width = width;
        this.heigth = height;
        this.depth = depth;
        this.world = world;
    }

    public int getEntityID() {
        return entityID;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return heigth;
    }

    public double getDepth() {
        return depth;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}