package pro.fazeclan.river.jarona.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pro.fazeclan.river.jarona.Jarona;

import java.io.Closeable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SchedulingUtil {

    public static Closeable runLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskLater(plugin, task, delay);
        return handler::cancel;
    }

    public static BukkitTask runTaskLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        return plugin
                .getServer()
                .getScheduler()
                .runTaskLater(plugin, task, delay);
    }

    public static Closeable interval(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskTimer(plugin, task, delay, period);
        return handler::cancel;
    }

    public static BukkitTask intervalTask(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        return plugin
                .getServer()
                .getScheduler()
                .runTaskTimer(plugin, task, delay, period);
    }

    public static void interval(long delay, long period, Supplier<Boolean> task) {
        var plugin = Jarona.getInstance();
        new ConditionalRunnable(cr -> task.get()).runTaskTimer(plugin, delay, period);
    }

    public static Closeable asyncRun(Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, task);
        return handler::cancel;
    }

    public static BukkitTask asyncRunTask(Runnable task) {
        var plugin = Jarona.getInstance();
        return plugin
                .getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, task);
    }

    public static Closeable asyncRunLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskLaterAsynchronously(plugin, task, delay);
        return handler::cancel;
    }

    public static BukkitTask asyncRunTaskLater(long delay, Runnable task) {
        var plugin = Jarona.getInstance();
        return plugin
                .getServer()
                .getScheduler()
                .runTaskLaterAsynchronously(plugin, task, delay);
    }

    public static Closeable asyncInterval(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        var handler = plugin
                .getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(plugin, task, delay, period);
        return handler::cancel;
    }

    public static BukkitTask asyncIntervalTask(long delay, long period, Runnable task) {
        var plugin = Jarona.getInstance();
        return plugin
                .getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public static class ConditionalRunnable extends BukkitRunnable {

        private Predicate<ConditionalRunnable> task;

        private ConditionalRunnable(Predicate<ConditionalRunnable> task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (!task.test(this)) {
                cancel();
            }
        }
    }

}
