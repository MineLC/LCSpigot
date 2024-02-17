package org.bukkit.craftbukkit.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import org.bukkit.Bukkit;
import org.tinylog.Logger;

public final class Versioning {
    public static String getBukkitVersion() {
        String result = "Unknown-Version";

        InputStream stream = Bukkit.class.getClassLoader().getResourceAsStream("META-INF/maven/org.spigotmc/spigot-api/pom.properties");
        Properties properties = new Properties();

        if (stream != null) {
            try {
                properties.load(stream);

                result = properties.getProperty("version");
            } catch (IOException ex) {
                Logger.error("Could not get Bukkit version!", ex);
            }
        }

        return result;
    }
}
