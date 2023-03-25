package net.pupskuchen.timecontrol.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class NightSkipGuard {
    private static final int DEFAULT_SLEEP_TICKS = 101;

    private final long skippableTickThreshold = DEFAULT_SLEEP_TICKS;
    private final JavaPlugin plugin;

    private boolean canSkip = false;
    private BukkitTask waitForSkippable;

    public NightSkipGuard(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void makeSkippable() {
        this.cancel();

        waitForSkippable = new BukkitRunnable() {
            @Override
            public void run() {
                canSkip = true;
            }
        }.runTaskLater(plugin, skippableTickThreshold);
    }

    public void cancel() {
        if (waitForSkippable == null) {
            return;
        }

        waitForSkippable.cancel();
        canSkip = false;
    }

    public boolean isSkippable() {
        return canSkip;
    }
}
