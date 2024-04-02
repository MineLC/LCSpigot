package net.minecraft.server.v1_8_R3;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.tinylog.Logger;

public class WorldLoaderServer extends WorldLoader {


    public WorldLoaderServer(File file) {
        super(file);
    }

    protected int c() {
        return 19133;
    }

    public void d() {
        RegionFileCache.a();
    }

    public IDataManager a(String s, boolean flag) {
        return new ServerNBTManager(this.a, s, flag);
    }

    public boolean isConvertable(String s) {
        WorldData worlddata = this.c(s);

        return worlddata != null && worlddata.l() != this.c();
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        iprogressupdate.a(0);
        ArrayList arraylist = Lists.newArrayList();
        ArrayList arraylist1 = Lists.newArrayList();
        ArrayList arraylist2 = Lists.newArrayList();
        File file = new File(this.a, s);
        File file1 = new File(file, "DIM-1");
        File file2 = new File(file, "DIM1");

        Logger.info("Scanning folders...");
        this.a(file, arraylist);
        if (file1.exists()) {
            this.a(file1, arraylist1);
        }

        if (file2.exists()) {
            this.a(file2, arraylist2);
        }

        int i = arraylist.size() + arraylist1.size() + arraylist2.size();

        Logger.info("Total conversion count is " + i);
        WorldData worlddata = this.c(s);
        Object object = null;

        object = new WorldChunkManager(BiomeBase.PLAINS, 0.5F);
 
        this.a(new File(file, "region"), (Iterable) arraylist, (WorldChunkManager) object, 0, i, iprogressupdate);
        this.a(new File(file1, "region"), (Iterable) arraylist1, new WorldChunkManager(BiomeBase.HELL, 0.0F), arraylist.size(), i, iprogressupdate);
        this.a(new File(file2, "region"), (Iterable) arraylist2, new WorldChunkManager(BiomeBase.SKY, 0.0F), arraylist.size() + arraylist1.size(), i, iprogressupdate);
        worlddata.e(19133);
        if (worlddata.getType() == WorldType.NORMAL_1_1) {
            worlddata.a(WorldType.NORMAL);
        }

        this.g(s);
        IDataManager idatamanager = this.a(s, false);

        idatamanager.saveWorldData(worlddata);
        return true;
    }

    private void g(String s) {
        File file = new File(this.a, s);

        if (!file.exists()) {
            Logger.warn("Unable to create level.dat_mcr backup");
        } else {
            File file1 = new File(file, "level.dat");

            if (!file1.exists()) {
                Logger.warn("Unable to create level.dat_mcr backup");
            } else {
                File file2 = new File(file, "level.dat_mcr");

                if (!file1.renameTo(file2)) {
                    Logger.warn("Unable to create level.dat_mcr backup");
                }

            }
        }
    }

    private void a(File file, Iterable<File> iterable, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            File file1 = (File) iterator.next();

            this.a(file, file1, worldchunkmanager, i, j, iprogressupdate);
            ++i;
            int k = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.a(k);
        }

    }

    private void a(File file, File file1, WorldChunkManager worldchunkmanager, int i, int j, IProgressUpdate iprogressupdate) {
        try {
            String s = file1.getName();
            RegionFile regionfile = new RegionFile(file1);
            RegionFile regionfile1 = new RegionFile(new File(file, s.substring(0, s.length() - ".mcr".length()) + ".mca"));

            for (int k = 0; k < 32; ++k) {
                int l;

                for (l = 0; l < 32; ++l) {
                    if (regionfile.c(k, l) && !regionfile1.c(k, l)) {
                        DataInputStream datainputstream = regionfile.a(k, l);

                        if (datainputstream == null) {
                            Logger.warn("Failed to fetch input stream");
                        } else {
                            NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);

                            datainputstream.close();
                            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");
                            OldChunkLoader.OldChunk oldchunkloader_oldchunk = OldChunkLoader.a(nbttagcompound1);
                            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

                            nbttagcompound2.set("Level", nbttagcompound3);
                            OldChunkLoader.a(oldchunkloader_oldchunk, nbttagcompound3, worldchunkmanager);
                            DataOutputStream dataoutputstream = regionfile1.b(k, l);

                            NBTCompressedStreamTools.a(nbttagcompound2, (DataOutput) dataoutputstream);
                            dataoutputstream.close();
                        }
                    }
                }

                l = (int) Math.round(100.0D * (double) (i * 1024) / (double) (j * 1024));
                int i1 = (int) Math.round(100.0D * (double) ((k + 1) * 32 + i * 1024) / (double) (j * 1024));

                if (i1 > l) {
                    iprogressupdate.a(i1);
                }
            }

            regionfile.c();
            regionfile1.c();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }

    private void a(File file, Collection<File> collection) {
        File file1 = new File(file, "region");
        File[] afile = file1.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                return s.endsWith(".mcr");
            }
        });

        if (afile != null) {
            Collections.addAll(collection, afile);
        }

    }
}
