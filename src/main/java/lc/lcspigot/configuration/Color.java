package lc.lcspigot.configuration;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

public final class Color {

    public static String color(final Object object) {
        return (object == null) ? "" : ChatColor.translateAlternateColorCodes('&', object.toString());
    }

    public static String parse(final Object text) {
        if (text == null) {
            return "";
        }
        if (!(text instanceof List)) {
            return color(text);
        }
        final StringBuilder builder = new StringBuilder();
        final List<?> list = (List<?>)text;
        int index = 0;

        for (final Object object : list) {
            builder.append(color(object));
            if (++index != list.size()) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }
}