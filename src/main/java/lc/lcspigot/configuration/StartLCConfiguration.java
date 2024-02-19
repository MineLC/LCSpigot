package lc.lcspigot.configuration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.tinylog.Logger;

public final class StartLCConfiguration {

    public void load() {       
        final File file = new File("lcspigot.yml");

        if (!file.exists()) {
            if (!createFile(file)) {
                return;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        final LCConfig lcConfig = new LCConfig(
            Color.parse(config.getString("unknown-command")),
            getInt(config, "tick-time", 20),
            getInt(config, "container-update-delay", 1),
            getInt(config, "", 10000)
        );

        LCConfig.setInstance(lcConfig);   
    }

    private boolean createFile(final File file) {
        final InputStream stream = StartLCConfiguration.class.getClassLoader().getResourceAsStream("configurations/lcspigot.yml");
        if (stream == null) {
            Logger.error("Can't found lcspigot.yml in jar");
            return false;
        }
        
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            BufferedInputStream in = new BufferedInputStream(stream)
        ) {
            IOUtils.copy(in,out);
            stream.close();
            return true;
        } catch (IOException e) {
            Logger.error(e);
            return false;
        }
    }

    private int getInt(final FileConfiguration config, final String section, final int defaultValue) {
        final int value = config.getInt(section);
        return (value == 0) ? defaultValue : value;
    }
}