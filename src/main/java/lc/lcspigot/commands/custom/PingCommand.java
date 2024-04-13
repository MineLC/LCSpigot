package lc.lcspigot.commands.custom;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;
import net.md_5.bungee.api.ChatColor;

public final class PingCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        Player target; 
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sendWithColor(sender, "&c " + args[0] + " isn't online");
                return;
            }
        } else {
            if (sender instanceof Player) {
                target = (Player)sender;
            } else {
                sender.sendMessage("Format: /ping (player)");
                return;
            }
        }
        final String message = format(((CraftPlayer)target).getHandle().ping);
        if (!target.equals(sender)) {
            sender.sendMessage(target.getName() + " = " + message);
            return;
        }
        sender.sendMessage(message);
    }

    private String format(int ping) {
        if (ping <= 90) {
            return ChatColor.AQUA.toString() + ping + "ms Perfect";
        }
        if (ping <= 170) {
            return ChatColor.GREEN.toString() + ping + "ms Nice";
        }
        if (ping <= 250) {
            return ChatColor.YELLOW.toString() + ping + "ms Normal";
        }
        if (ping <= 350) {
            return ChatColor.RED.toString() + ping + "ms Bad";
        }
        return ChatColor.DARK_RED.toString() + ping + "ms RIP";
    }
}