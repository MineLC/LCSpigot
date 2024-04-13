package org.bukkit.craftbukkit.v1_8_R3.command;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public abstract class ServerCommandSender implements CommandSender {

    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    public boolean isPlayer() {
        return false;
    }

    public Server getServer() {
        return Bukkit.getServer();
    }
}
