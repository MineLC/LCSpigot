package lc.lcspigot.commands.custom;

import org.bukkit.command.CommandSender;

import lc.lcspigot.commands.Command;
import lc.lcspigot.configuration.LCConfig;
import lc.lcspigot.configuration.sections.ConfigKnockback;

final class KnockbackCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (!checkPermission(sender, "knockback")) {
            return;
        }
        if (args.length != 2) {
            send(sender, format());
            return;
        }
        final double value = tryParse(args[1], sender);
        if (value == -99.9) {
            return;
        }
        final ConfigKnockback knockback = LCConfig.getConfig().knockback;
        switch (args[0].toLowerCase()) {
            case "friction":
                knockback.friction = value;
                break;
            case "horizontal":
                knockback.horizontal = value;
                break;
            case "vertical":
                knockback.vertical = value;
                break;
            case "verticallimit":
                knockback.verticalLimit = value;
                break;
            case "extrahorizontal":
                knockback.extraHorizontal = value;
                break;
            case "extravertical":
                knockback.extraVertical = value;
                break;
            default:
                break;
        }
        send(sender, "Value: §e" + args[0] + "§f changed to §e" + value);
    }

    @Override
    public String[] tab(CommandSender sender, String[] args) {
        return (args.length == 0) ? list("friction", "horizontal", "vertical", "verticalLimit", "extraHorizontal", "extraVertical") : none();
    }

    private double tryParse(final String arg, final CommandSender sender) {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            send(sender, format());
            return -99.9;
        }
    }

    private String format() {
        return
            "\n " +
            "\n §f§lPanda§6§lSpigot §7Knockback" +
            "\n §8To save this, change lcspigot.yml" +
            "\n §6/kb §7->" +
            "\n     §ffriction §e(decimal) §7- Set the weight of entity" +
            "\n     §fhorizontal §e(decimal) §7- Default horizontal knockback" +
            "\n     §fvertical §e(decimal) §7- Default vertical knockback" +
            "\n     §fverticalLimit §e(decimal) §7- Limit of vertical knockback" +
            "\n     §fextraHorizontal §e(decimal) §7- Add extra horizontal on sprint" +
            "\n     §fextraVertical §e(decimal) §7- Add extra vertical on sprint" +
            "\n ";
    }
}