package lc.lcspigot.commands.vanilla;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;

final class ClearCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "lcspigot.clear")) {
            return;
        }
        if (!(sender instanceof Player)) {
            return;
        }
        ((Player)sender).getInventory().clear();
        sender.sendMessage("Inventory cleared!");
    }
}