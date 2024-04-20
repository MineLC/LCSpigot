package lc.lcspigot.roblox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lc.lcspigot.configuration.LCConfig;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public final class RobloxThread extends Thread {
    private final HttpClient client = HttpClient.newHttpClient();
    private static RobloxThread thread;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void execute() {
        if (LCConfig.getConfig().robloxUri == null) {
            return;
        }
        final String post = createFormat();
        if (post == null) {
            return;
        }
        RobloxData.getInstance().blocks = new HashSet<>();

        try {
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LCConfig.getConfig().robloxUri))
                .POST(HttpRequest.BodyPublishers.ofString(post))
                .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private String createFormat() {
        final Set<BlockData> blocks = RobloxData.getInstance().blocks;
        if (blocks.isEmpty() && MinecraftServer.getServer().getPlayerList().players.isEmpty()) {
            return null;
        }

        final StringBuilder builder = new StringBuilder(blocks.size() * 32 + MinecraftServer.getServer().getPlayerList().players.size() * 32);
        builder.append('{');
        appendPlayers(builder);
        builder.append(',');
        appendBlocks(builder);
        builder.append('}');

        return builder.toString();
    }

    private void appendPlayers(final StringBuilder builder) {
        final List<EntityPlayer> players = MinecraftServer.getServer().getPlayerList().players;
        int remainPlayers = players.size();
        builder.append("\"players\": [");

        for (final EntityPlayer player : players) {
            --remainPlayers;
            builder.append('\"');
            builder.append(roundDown2(player.locX));
            builder.append(',');
            builder.append(roundDown2(player.locY));
            builder.append(',');
            builder.append(roundDown2(player.locZ));
            builder.append(',');
            builder.append(player.getName());
            if (remainPlayers == 0) {
                builder.append("\"]");        
                continue;
            }
            builder.append("\",");
        }
    }
    private static float roundDown2(double d) {
        return (float) (((long)(d * 1e2)) / 1e2);
    }

    private void appendBlocks(final StringBuilder builder) {
        final Set<BlockData> blocks = RobloxData.getInstance().blocks;
        int remainBlocks = blocks.size();
        builder.append("\"changed-blocks\": [");
        if (remainBlocks == 0) {
            builder.append(']');
            return;
        }
        for (final BlockData data : blocks) {
            --remainBlocks;
            builder.append('\"');
            builder.append(data.x);
            builder.append(',');
            builder.append(data.y);
            builder.append(',');
            builder.append(data.z);
            builder.append(',');
            builder.append(data.material.name());
            if (remainBlocks == 0) {
                builder.append("\"]");        
                continue;
            }
            builder.append("\",");
        }
    }

    public static void iniciate() {
        RobloxData.start();
        thread = new RobloxThread();
        thread.start();
    }

    public static void close() {
        if (thread != null) {
            thread.client.close();
        }
    }
}