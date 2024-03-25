package lc.lcspigot.commands.custom;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tinylog.Logger;

import lc.lcspigot.commands.Command;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

final class BroadcastCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "broadcast")) {
            return;
        }
        if (args.length < 1) {
            send(sender, "Format: /bc (message)");
            return;
        }
        final StringBuilder builder = new StringBuilder();
        for (final String arg : args) {
            builder.append(arg);
            builder.append(' ');
        }
        final String message = builder.toString();
        final BaseComponent[] components = TextComponent.fromLegacyText(message);
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (final Player player : players) {
            player.spigot().sendMessage(components);
        }
        Logger.info(message);
    }
}
