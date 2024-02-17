package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.tinylog.Logger;

public class WorldLoader implements Convertable {

    protected final File a;

    public WorldLoader(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }

        this.a = file;
    }

    public void d() {}

    public WorldData c(String s) {
        File file = new File(this.a, s);

        if (!file.exists()) {
            return null;
        } else {
            File file1 = new File(file, "level.dat");
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;

            if (file1.exists()) {
                try {
                    nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                    nbttagcompound1 = nbttagcompound.getCompound("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception) {
                   Logger.error("Exception reading " + file1, exception);
                }
            }

            file1 = new File(file, "level.dat_old");
            if (file1.exists()) {
                try {
                    nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
                    nbttagcompound1 = nbttagcompound.getCompound("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception1) {
                   Logger.error("Exception reading " + file1, exception1);
                }
            }

            return null;
        }
    }

    public boolean e(String s) {
        File file = new File(this.a, s);

        if (!file.exists()) {
            return true;
        } else {
           Logger.info("Deleting level " + s);

            for (int i = 1; i <= 5; ++i) {
               Logger.info("Attempt " + i + "...");
                if (a(file.listFiles())) {
                    break;
                }

               Logger.warn("Unsuccessful in deleting contents.");
                if (i < 5) {
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }
                }
            }

            return file.delete();
        }
    }

    protected static boolean a(File[] afile) {
        for (int i = 0; i < afile.length; ++i) {
            File file = afile[i];

           Logger.debug("Deleting " + file);
            if (file.isDirectory() && !a(file.listFiles())) {
               Logger.warn("Couldn\'t delete directory " + file);
                return false;
            }

            if (!file.delete()) {
               Logger.warn("Couldn\'t delete file " + file);
                return false;
            }
        }

        return true;
    }

    public IDataManager a(String s, boolean flag) {
        return new WorldNBTStorage(this.a, s, flag);
    }

    public boolean isConvertable(String s) {
        return false;
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        return false;
    }
}
