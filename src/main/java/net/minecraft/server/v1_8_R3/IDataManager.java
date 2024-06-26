package net.minecraft.server.v1_8_R3;

import java.io.File;

public interface IDataManager {

    WorldData getWorldData();

    void checkSession() throws ExceptionWorldConflict;

    IChunkLoader createChunkLoader(WorldProvider worldprovider);

    void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound);

    void saveWorldData(WorldData worlddata);

    IPlayerFileData getPlayerFileData();

    void a();

    File getDirectory();

    File getDataFile(String s);

    String g();

    java.util.UUID getUUID(); // CraftBukkit
}
