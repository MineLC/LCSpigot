package lc.lcspigot.commands.custom;

import org.bukkit.command.CommandSender;

import lc.lcspigot.commands.Command;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.MinecraftServer;

final class TpsCommand implements Command {

    @Override
    public void handle(CommandSender sender, String[] args) {
        final StringBuilder sb = new StringBuilder(ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " );
        for (double tps : MinecraftServer.getServer().recentTps) {
            sb.append( format( tps ) );
            sb.append( ", " );
        }
        send(sender, sb.substring( 0, sb.length() - 2 ) );
    }

    private String format(double tps) {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }
}