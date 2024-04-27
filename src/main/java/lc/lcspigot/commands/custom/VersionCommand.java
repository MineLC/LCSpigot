package lc.lcspigot.commands.custom;

import org.bukkit.command.CommandSender;

import lc.lcspigot.commands.Command;

public final class VersionCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        sender.sendMessage(format());
    }

    private String format() {
        return
            "\n " +
            "\n §b§lLC§6§lSPIGOT §7(1.8) - 1.0.0" +
            "\n " +
            "\n §fFeatures:" +
            "\n §7- Uses TinyLog" +
            "\n §7- No patches based" +
            "\n §7- PandaSpigot features" +
            "\n §7- Recode chat, commands and more." +
            "";
    }
}