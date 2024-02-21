package lc.lcspigot.commands.vanilla;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lc.lcspigot.commands.Command;

final class GiveCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            send(sender, "Only players");
            return;
        }

        if (!checkPermission(sender, "lcspigot.give")) {
            return;
        }

        if (args.length < 1) {
            sendWithColor(sender, "&cFormat: /give (material) (amount)");
            return;
        }

        final Material material = Material.getMaterial(args[0].toUpperCase());

        if (material == null) {
            sendWithColor(sender, "&cThe material " + args[0] + " no exist");
            return;
        }

        final Player player = (Player)sender;
        if (args.length != 2) {
            player.getInventory().addItem(new ItemStack(material, 1));
            return;
        }
        try {
            final int amount = Integer.parseInt(args[1]);
            player.getInventory().addItem(new ItemStack(material, amount));
        } catch (NumberFormatException e) {
            sendWithColor(sender, "&cThe amount need be positive");
        }
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 0) ? (String[])Material.getMaterialNames().toArray() : none();
    }
}