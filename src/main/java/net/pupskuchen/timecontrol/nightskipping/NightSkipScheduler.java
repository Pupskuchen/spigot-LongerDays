package net.pupskuchen.timecontrol.nightskipping;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.pupskuchen.timecontrol.TimeControl;

class SkipAttempt extends BukkitRunnable {
    private final NightSkipper skipper;

    public SkipAttempt(final NightSkipper skipper) {
        this.skipper = skipper;
    }

    @Override
    public void run() {
        this.skipper.skipNight();
    }
}


class NightSkipScheduler {
    private final TimeControl plugin;
    private final NightSkipper skipper;
    private final int sleepTicksToWake;

    private BukkitTask skipTask;

    public NightSkipScheduler(final TimeControl plugin, final NightSkipper skipper,
            final int sleepTicksToWake) {
        this.plugin = plugin;
        this.skipper = skipper;
        this.sleepTicksToWake = sleepTicksToWake;
    }

    public void scheduleSkip() {
        this.cancel();
        skipTask = new SkipAttempt(skipper).runTaskLater(plugin, sleepTicksToWake);
    }

    public void cancel() {
        if (skipTask == null) {
            return;
        }

        skipTask.cancel();
        skipTask = null;
    }
}
