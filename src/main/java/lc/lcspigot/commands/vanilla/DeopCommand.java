package lc.lcspigot.commands.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftOfflinePlayer;

import lc.lcspigot.commands.Command;

final class DeopCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "lcspigot.deop")) {
            return;
        }

        if (args.length != 1) {
            send(sender, "Format: /deop (player)");
            return;
        }

        @SuppressWarnings("deprecation")
        final CraftOfflinePlayer offlinePlayer = (CraftOfflinePlayer) Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            send(sender, "The player " + args[0] + " don't exist. This is case sensitive");
            return;
        }
        offlinePlayer.setOp(false);
        send(sender, "Now, the player " + args[0] + " is deop");
    }
}
