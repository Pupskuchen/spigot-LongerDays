package net.pupskuchen.timecontrol.timer;

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
import net.pupskuchen.timecontrol.util.TCLogger;

public class WorldTimer {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final ConfigHandler config;
    private final Map<World, WorldState> worldRatios = new ConcurrentHashMap<>();

    private BukkitTask runner;

    public WorldTimer(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
        this.config = plugin.getConfigHandler();
    }

    public void enableForWorld(final World world) {
        if (!config.isWorldEnabled(world) || worldRatios.containsKey(world)) {
            return;
        }

        final WorldState state = new WorldState(config.getDurations(world),
                world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        worldRatios.put(world, state);

        logger.info("Enabling custom time control for world \"%s\".", world.getName());
        logger.debug("time multipliers: day %.3f / night %.3f / sunset %.3f / sunrise %.3f",
                state.ratios.day, state.ratios.night, state.ratios.sunset, state.ratios.sunrise);

        startTimer();
    }

    public void enableForWorlds(final List<World> worlds) {
        for (World world : worlds) {
            enableForWorld(world);
        }
    }

    public void disableForWorld(final World world) {
        final WorldState state = this.worldRatios.remove(world);

        if (state == null) {
            return;
        }

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, state.originalDoDaylightCycle);
        logger.info("Disabling custom time control for world \"%s\".", world.getName());
        logger.debug("Restored game rule \"%s\" to \"%b\"", GameRule.DO_DAYLIGHT_CYCLE.getName(),
                state.originalDoDaylightCycle);

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
                for (Entry<World, WorldState> entry : worldRatios.entrySet()) {
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

    private void updateWorld(final World world, final WorldState state) {
        final long time = world.getTime();
        final double ratio = state.getApplicableRatio(time);

        if (ratio > 1.0) {
            // speed up
            world.setTime(time + Math.round(ratio));
            state.setIntermediateTicks(0);
        } else if (ratio < 1.0) {
            // slow down
            final long count = state.getIntermediateTicks();

            if (count <= 0) {
                world.setTime(time + 1);
                state.setIntermediateTicks(Math.round(1.0 / ratio) - 1);
            } else {
                state.setIntermediateTicks(count - 1);
            }
        } else {
            // Normal
            world.setTime(time + 1);
        }
    }
}
