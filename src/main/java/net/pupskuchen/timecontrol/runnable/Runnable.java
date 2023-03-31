package net.pupskuchen.timecontrol.runnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeRatio;

public class Runnable {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final Map<World, TimeRatio> worldRatios = new HashMap<>();

    public Runnable(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
    }

    public void enableForWorlds(final List<World> worlds) {
        for (World world : worlds) {
            final TimeRatio ratios = new TimeRatio(plugin, world);
            worldRatios.put(world, ratios);
            logger.info("Enabling custom time control for world \"%s\".", world.getName());
            logger.debug("ratios: day %.2f | night %.2f", ratios.day, ratios.night);
        }

        startTimer();
    }

    private void startTimer() {
        if (worldRatios.size() == 0) {
            logger.warn("No worlds configured. Without any worlds, this plugin does nothing.");

            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<World, TimeRatio> entry : worldRatios.entrySet()) {
                    updateWorld(entry.getKey(), entry.getValue());
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);

        logger.debug("Custom time control started.");
    }

    private void updateWorld(final World world, final TimeRatio ratios) {
        final long time = world.getTime();
        final double ratio = ratios.getApplicableRatio(time);

        if (ratio > 1.0) {
            // speed up
            world.setTime(time + Math.round(ratio));
            ratios.setIntermediateTicks(0);
        } else if (ratio < 1.0) {
            // slow down
            final long count = ratios.getIntermediateTicks();

            if (count <= 0) {
                world.setTime(time + 1);
                ratios.setIntermediateTicks(Math.round(1.0 / ratio) - 1);
            } else {
                ratios.setIntermediateTicks(count - 1);
            }
        } else {
            // Normal
            world.setTime(time + 1);
        }
    }
}
