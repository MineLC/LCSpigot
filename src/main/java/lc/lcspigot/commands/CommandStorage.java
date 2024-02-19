package lc.lcspigot.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import lc.lcspigot.configuration.LCConfig;

public final class CommandStorage {
    private static final Map<String, Command> COMMANDS = new HashMap<>();
    
    public static void execute(final CommandSender sender, final String command) {
        final String[] split = command.split(" ");
        final Command executor = COMMANDS.get(split[0]);

        if (executor != null) {
            executor.handle(sender, split);
            return;
        }
        sender.sendMessage(LCConfig.getConfig().getUnknownCommand().replace("%command%", split[0]));
    }

    public static String[] tab(final CommandSender sender, final String command) {
        final String[] split = command.split(" ");
        final Command executor = getCommand(command);
        if (executor == null) {
            return null;
        }
        return executor.tab(sender, split);
    }

    public static void register(final Command executor, final String... aliases) {
        for (final String aliase : aliases) {
            COMMANDS.put(aliase, executor);
        }
    }

    public static Command getCommand(String command) {
        return COMMANDS.get(command);
    }

    public static Set<String> getCommands() {
        return COMMANDS.keySet();
    }
}