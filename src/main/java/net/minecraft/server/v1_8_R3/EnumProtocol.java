package net.minecraft.server.v1_8_R3;

import java.lang.reflect.InvocationTargetException;

import org.tinylog.Logger;

public enum EnumProtocol {

    HANDSHAKING(0, 1, -1) {;
        {
            this.a(EnumProtocolDirection.SERVERBOUND, PacketHandshakingInSetProtocol.class);
        }
    }, PLAY(74, 26, 0) {;
    {
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKeepAlive.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutLogin.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutChat.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateTime.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEquipment.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnPosition.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateHealth.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRespawn.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPosition.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutHeldItemSlot.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBed.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAnimation.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedEntitySpawn.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCollect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntity.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityLiving.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityPainting.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityExperienceOrb.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityVelocity.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityDestroy.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMove.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutEntityLook.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityTeleport.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityHeadRotation.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityStatus.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAttachEntity.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityMetadata.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutEntityEffect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutRemoveEntityEffect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExperience.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateAttributes.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunk.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMultiBlockChange.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockChange.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockAction.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutBlockBreakAnimation.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMapChunkBulk.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutExplosion.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldEvent.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutNamedSoundEffect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldParticles.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutGameStateChange.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSpawnEntityWeather.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenWindow.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCloseWindow.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetSlot.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowItems.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWindowData.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTransaction.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateSign.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutMap.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTileEntityData.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutOpenSignEditor.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutStatistic.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerInfo.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutAbilities.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTabComplete.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardObjective.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardScore.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardDisplayObjective.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutScoreboardTeam.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCustomPayload.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutKickDisconnect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutServerDifficulty.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCombatEvent.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutCamera.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutWorldBorder.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutTitle.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutSetCompression.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutPlayerListHeaderFooter.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutResourcePackSend.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketPlayOutUpdateEntityNBT.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInKeepAlive.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInChat.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInUseEntity.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPosition.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInLook.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInFlying.PacketPlayInPositionLook.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockDig.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInBlockPlace.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInHeldItemSlot.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInArmAnimation.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInEntityAction.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInSteerVehicle.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInCloseWindow.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInWindowClick.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInTransaction.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInSetCreativeSlot.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInEnchantItem.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInUpdateSign.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInAbilities.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInTabComplete.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInSettings.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInClientCommand.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInCustomPayload.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInSpectate.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketPlayInResourcePackStatus.class);
    }
}, STATUS(2, 2, 1) {;
    {
        this.a(EnumProtocolDirection.SERVERBOUND, PacketStatusInStart.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutServerInfo.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketStatusInPing.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketStatusOutPong.class);
    }
}, LOGIN(4, 2, 2) {;
    {
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutDisconnect.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutEncryptionBegin.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSuccess.class);
        this.a(EnumProtocolDirection.CLIENTBOUND, PacketLoginOutSetCompression.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketLoginInStart.class);
        this.a(EnumProtocolDirection.SERVERBOUND, PacketLoginInEncryptionBegin.class);
    }
};

    private static int e = -1;
    private static int f = 2;
    private static final EnumProtocol[] g = new EnumProtocol[EnumProtocol.f - EnumProtocol.e + 1];
    private final int i;
    private final DirectionStorage serverPackets, clientPackets;

    private int serverIndex = 0, clientIndex = 0;

    private EnumProtocol(int clientPackets, int serverPackets, int i) {
        this.i = i;
        this.serverPackets = new DirectionStorage(serverPackets);
        this.clientPackets = new DirectionStorage(clientPackets);
    }

    protected EnumProtocol a(EnumProtocolDirection enumprotocoldirection, Class<? extends Packet<?>> oclass) {
        try {
            final Packet<?> constructor = oclass.getConstructor().newInstance();
            if (enumprotocoldirection == EnumProtocolDirection.CLIENTBOUND) {
                clientPackets.packetsConstructors[clientIndex++] = constructor;
            } else {
                serverPackets.packetsConstructors[serverIndex++] = constructor;
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            Logger.error(e);
        }
        return this;
    }

    public Packet<?> createEmptyPacket(EnumProtocolDirection enumprotocoldirection, int i) {
        final DirectionStorage storage = (enumprotocoldirection == EnumProtocolDirection.CLIENTBOUND
            ? clientPackets
            : serverPackets
        );
        if (storage.packetsConstructors.length < i || i > storage.packetsConstructors.length) {
            return null;
        }
        return storage.packetsConstructors[i].emptyCopy();
    }

    public int a() {
        return this.i;
    }

    public static EnumProtocol a(int i) {
        return i >= EnumProtocol.e && i <= EnumProtocol.f ? EnumProtocol.g[i - EnumProtocol.e] : null;
    }

    private static final class DirectionStorage {
        private final Packet<?>[] packetsConstructors;
        private DirectionStorage(int amountPackets) {
            this.packetsConstructors = new Packet[amountPackets];
        }
    }

    static {
        EnumProtocol[] aenumprotocol = values();
        int i = aenumprotocol.length;

        for (int j = 0; j < i; ++j) {
            EnumProtocol enumprotocol = aenumprotocol[j];
            int k = enumprotocol.a();

            if (k < EnumProtocol.e || k > EnumProtocol.f) {
                throw new Error("Invalid protocol ID " + Integer.toString(k));
            }

            EnumProtocol.g[k - EnumProtocol.e] = enumprotocol;
        }
    }
}