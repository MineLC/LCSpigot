package net.minecraft.server;

import java.io.OutputStream;
import java.io.PrintStream;
import org.tinylog.Logger;

public class RedirectStream extends PrintStream {

    private final String b;

    public RedirectStream(String s, OutputStream outputstream) {
        super(outputstream);
        this.b = s;
    }

    public void println(String s) {
        this.a(s);
    }

    public void println(Object object) {
        this.a(String.valueOf(object));
    }

    private void a(String s) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];

        Logger.info("[{}]@.({}:{}): {}", new Object[] { this.b, stacktraceelement.getFileName(), Integer.valueOf(stacktraceelement.getLineNumber()), s});
    }
}
