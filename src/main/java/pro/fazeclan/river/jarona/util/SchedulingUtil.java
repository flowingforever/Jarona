package pro.fazeclan.river.jarona.util;

import pro.fazeclan.river.jarona.Jarona;

import java.io.Closeable;

public class SchedulingUtil {

    public static Closeable runLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskLater(plugin, task, delay);
        return handler::cancel;
    }

    public static Closeable interval(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskTimer(plugin, task, delay, period);
        return handler::cancel;
    }

    public static Closeable asyncRun(Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, task);
        return handler::cancel;
    }

    public static Closeable asyncRunLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskLaterAsynchronously(plugin, task, delay);
        return handler::cancel;
    }

    public static Closeable asyncInterval(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(plugin, task, delay, period);
        return handler::cancel;
    }

}
