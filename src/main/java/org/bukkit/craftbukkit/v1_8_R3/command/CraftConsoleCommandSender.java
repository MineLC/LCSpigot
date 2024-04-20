package org.bukkit.craftbukkit.v1_8_R3.command;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.tinylog.Logger;

/**
 * Represents CLI input from a console
 */
public class CraftConsoleCommandSender extends ServerCommandSender implements ConsoleCommandSender {

    protected CraftConsoleCommandSender() {
        super();
    }

    public void sendMessage(String message) {
        sendRawMessage(message);
    }

    public void sendRawMessage(String message) {
        Logger.info(ChatColor.stripColor(message));
    }

    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public String getName() {
        return "CONSOLE";
    }

    public boolean isOp() {
        return true;
    }

    public void setOp(boolean value) {
        throw new UnsupportedOperationException("Cannot change operator status of server console");
    }

    @Override
    public void addPermission(String permission) {
    }

    @Override
    public void removePermission(String permission) {
    }
}
