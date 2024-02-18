package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

public class VersionCommand extends BukkitCommand {
    public VersionCommand(String name) {
        super(name);
        this.setAliases(Arrays.asList("ver", "about"));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        sender.sendMessage(format());
        return true;
    }

    private String format() {
        return
            "\n " +
            "\n §b§lLC§6§lSPIGOT §7(1.8)" +
            "\n " +
            "\n §fFeatures:" +
            "\n §7- Uses TinyLog" +
            "\n §7- No patches based" +
            "\n §7- PandaSpigot features" +
            "\n §7- Recode chat, and more";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return ImmutableList.of();
    }
}