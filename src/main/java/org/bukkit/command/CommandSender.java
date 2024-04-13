package org.bukkit.command;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

public interface CommandSender {

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    public void sendMessage(String message);

    /**
     * Sends this sender multiple messages
     *
     * @param messages An array of messages to be displayed
     */
    public void sendMessage(String[] messages);

    /**
     * Returns the server instance that this command is running on
     *
     * @return Server instance
     */
    public Server getServer();

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    public String getName();

    public boolean hasPermission(final String permission);
    public void addPermission(final String permission);
    public void removePermission(final String permission);

    public boolean isOp();
    public void setOp(boolean value);
}
