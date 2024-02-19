package lc.lcspigot.commands;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public interface Command {

    public static final String[] EMPTY = new String[0];

    void handle(CommandSender sender, String[] args);
    default String[] tab(CommandSender sender, String[] args) {
        return none();
    }

    default String[] none() {
        return EMPTY;
    }

    default void send(final CommandSender sender, final String message) {
        sender.sendMessage(message);
    }

    default boolean checkPermission(final CommandSender sender, final String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(ChatColor.RED + "You need the permission " + permission);
        return false;
    }

    default String[] list(String... values) {
        return values;
    }
}