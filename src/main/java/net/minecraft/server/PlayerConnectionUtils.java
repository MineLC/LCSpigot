package net.minecraft.server;

public class PlayerConnectionUtils {

    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T t0, IAsyncTaskHandler iasynctaskhandler) throws CancelledPacketHandleException {
        if (!iasynctaskhandler.isMainThread()) {
            iasynctaskhandler.postToMainThread( () -> packet.a(t0));
            throw CancelledPacketHandleException.INSTANCE;
        }
    }
}
