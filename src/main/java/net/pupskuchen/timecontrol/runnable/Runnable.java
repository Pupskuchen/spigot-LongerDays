package net.pupskuchen.timecontrol.runnable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeRatio;

// TODO: rename
public class Runnable {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final ConfigHandler config;
    private final Map<World, TimeRatio> worldRatios = new ConcurrentHashMap<>();

    private BukkitTask runner;

    public Runnable(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
        this.config = plugin.getConfigHandler();
    }

    public void enableForWorld(final World world) {
        if (!config.isWorldEnabled(world) || worldRatios.containsKey(world)) {
            return;
        }

        final Durations durations = new Durations(config.getDay(world), config.getNight(world));
        final TimeRatio ratios =
                new TimeRatio(durations, world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        worldRatios.put(world, ratios);

        logger.info("Enabling custom time control for world \"%s\".", world.getName());
        logger.debug("tick multiplier: day %.2f | night %.2f", ratios.day, ratios.night);

        startTimer();
    }

    public void enableForWorlds(final List<World> worlds) {
        for (World world : worlds) {
            enableForWorld(world);
        }
    }

    public void disableForWorld(final World world) {
        final TimeRatio ratios = this.worldRatios.remove(world);

        if (ratios == null) {
            return;
        }

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, ratios.originalDoDaylightCycle);
        logger.info("Disabling custom time control for world \"%s\".", world.getName());
        logger.debug("Restored game rule \"%s\" to \"%b\"", GameRule.DO_DAYLIGHT_CYCLE.getName(),
                ratios.originalDoDaylightCycle);

        if (worldRatios.size() == 0) {
            stopTimer();
        }
    }

    public void disableAll() {
        for (World world : worldRatios.keySet()) {
            disableForWorld(world);
        }
    }

    private void startTimer() {
        if (runner != null) {
            return;
        } else if (worldRatios.size() == 0) {
            logger.warn("No worlds configured. Without any worlds, this plugin does nothing.");

            return;
        }

        runner = new BukkitRunnable() {
            @Override
            public void run() {
                for (Entry<World, TimeRatio> entry : worldRatios.entrySet()) {
                    updateWorld(entry.getKey(), entry.getValue());
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);

        logger.debug("Custom time control started.");
    }

    private void stopTimer() {
        if (runner == null) {
            return;
        }

        runner.cancel();
        runner = null;

        logger.debug("Custom time control stopped.");
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
