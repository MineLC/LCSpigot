package lc.lcspigot.commands.vanilla;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lc.lcspigot.commands.Command;

final class GamemodeCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "lcspigot.gamemode")) {
            return;
        }
        if (!(sender instanceof Player)) {
            send(sender, "Only players");
            return;
        }
        final Player player = (Player)sender;
        if (args.length != 1) {
            send(player, "Gamemodes: 0 (survival), 1 (creative), 2 (adventure), 3 (spectator)");
            return;
        }

        GameMode gameMode = null;
        switch (args[0].charAt(0)) {
            case '0': gameMode = GameMode.SURVIVAL; break;
            case '1': gameMode = GameMode.CREATIVE; break;
            case '2': gameMode = GameMode.ADVENTURE; break;
            case '3': gameMode = GameMode.SPECTATOR; break;
        }
        if (gameMode == null) {
            send(player, "Gamemodes: 0 (survival), 1 (creative), 2 (adventure), 3 (spectator)");
            return;
        }
        player.setGameMode(gameMode);
        send(player, "You gamemode is now " + gameMode.name());
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 1) ? new String[] {"0", "1", "2", "3"} : none();
    }
}
