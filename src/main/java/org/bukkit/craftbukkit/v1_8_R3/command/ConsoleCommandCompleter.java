package org.bukkit.craftbukkit.v1_8_R3.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

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
        final String[] split = StringUtils.split(buffer, ' ');
        final Command command = CommandStorage.getCommand(split[0]);
        if (command == null) {
            return cursor;
        }

        final String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);

        final String[] tab = command.tab(Bukkit.getConsoleSender(), split);

        if (tab != null) {
            for (final String tabOption : tab) {
                candidates.add(tabOption);
            }
        }
        return cursor;
    }
}
