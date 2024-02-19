package org.bukkit.craftbukkit.v1_8_R3.command;

import java.util.List;
import org.bukkit.Bukkit;
import org.tinylog.Logger;

import jline.console.completer.Completer;
import lc.lcspigot.commands.Command;
import lc.lcspigot.commands.CommandStorage;

public class ConsoleCommandCompleter implements Completer {

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        if (buffer.isEmpty()) {
            candidates.addAll(CommandStorage.getCommands());
            return cursor;
        }

        final Command command = CommandStorage.getCommand(buffer);
        if (command == null) {
            return cursor;
        }

        final String[] tab = command.tab(Bukkit.getConsoleSender(), buffer.split(" "));

        if (tab != null) {
            for (final String tabOption : tab) {
                candidates.add(tabOption);
            }
        }
        return cursor;
    }
}
