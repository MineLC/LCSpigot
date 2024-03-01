package lc.lcspigot.commands.vanilla;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;

final class WorldBorderCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            send(sender, "You need be player to execute this command");
            return;
        }
        if (checkPermission(sender, "lcspigot.worldborder")) {
            return;
        }
        if (args.length != 1) {
            send(sender, "Format: /worldborder (size)");
            return;
        }
        try {    
            final int size = Integer.parseInt(args[0]);
            if (size < 0) {
                send(sender, "Format: /worldborder (size). Size need be a positive number");
                return;
            }
            ((Player)sender).getWorld().getWorldBorder().setSize(size);
        } catch (NumberFormatException e) {
            send(sender, "Format: /worldborder (size). Size need be a positive number");
        }
    }
}