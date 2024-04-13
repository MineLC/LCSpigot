package lc.lcspigot.commands.vanilla;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;

final class OpCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "lcspigot.op")) {
            return;
        }
        if (args.length != 1) {
            send(sender, "Format: /op (player)");
            return;
        }

        final Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            send(sender, "The player " + args[0] + " don't exist. This is case sensitive");
            return;
        }
        player.setOp(true);
        send(sender, "Now, the player " + args[0] + " is op");
    }
}
