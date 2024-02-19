package lc.lcspigot.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import lc.lcspigot.configuration.LCConfig;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public final class CommandStorage {
    private static final Map<String, Command> COMMANDS = new HashMap<>();

    public static void execute(final CommandSender sender, final String command) {
        final String[] split = StringUtils.split(command, ' ');
        final Command executor = COMMANDS.get(split[0]);
    
        if (executor == null) {
            sender.sendMessage(LCConfig.getConfig().getUnknownCommand().replace("%command%", split[0]));
            return;
        }
        final String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
        MinecraftServer.getServer().processQueue.add( () -> executor.handle(sender, args));
    }

    public static String[] tab(final CommandSender sender, final String command) {
        final String[] split = StringUtils.split(command, ' ');
        final Command executor = getCommand(split[0]);
        if (executor == null) {
            return null;
        }
        final String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
        return executor.tab(sender, args);
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