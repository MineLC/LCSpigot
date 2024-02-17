package net.minecraft.server;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.tinylog.Logger;


public class SystemUtils {

    private static String a;

    public static String a() {
        return SystemUtils.a == null ? "<unknown>" : SystemUtils.a;
    }

    public static <V> V a(FutureTask<V> futuretask) {
        try {
            futuretask.run();
            return futuretask.get();
        } catch (ExecutionException executionexception) {
            Logger.error("Error executing task", executionexception);
        } catch (InterruptedException interruptedexception) {
            Logger.error("Error executing task", interruptedexception);
        }

        return null;
    }

    static {
        try {
            SystemUtils.a = String.format("%dx %s", new Object[] { 4, 1}).replaceAll("\\s+", " ");
        } catch (Exception exception) {
            ;
        }

    }
}
