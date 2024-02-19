package lc.lcspigot.commands.vanilla;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

final class StopCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "lcspigot.stop")) {
            return;
        }
        Bukkit.shutdown();
        send(sender, "Stopping the server...");

        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final BaseComponent[] components = TextComponent.fromLegacyText("The server is restarting...");
        for (final Player player : players) {
            player.spigot().sendMessage(components);
        }
    }
}