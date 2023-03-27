package net.pupskuchen.timecontrol.runnable;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TickUtil;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class Runnable {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final ConfigManager cm;
    private final Map<String, Long> counts;

    public Runnable(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
        this.cm = this.plugin.getConfigManager();
        this.counts = new HashMap<>();
    }

    public void runCycles(final World world) {
        final double dayRatio = TickUtil.cycleMinsToTickRatio(cm.getDay());
        final double nightRatio = TickUtil.cycleMinsToTickRatio(cm.getNight());
        final String worldName = world.getName();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (TimeUtil.isDay(world)) {
                    setTime(world, worldName, dayRatio);
                } else {
                    setTime(world, worldName, nightRatio);
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);

        logger.info("Running day and night cycles for world \"%s\"", world.getName());
    }

    private void setTime(final World world, final String worldName, final double ratio) {
        this.counts.putIfAbsent(worldName, 0L);
        final long time = world.getTime();

        if (ratio > 1.0) {
            // speed up
            world.setTime(time + Math.round(ratio));
            this.counts.put(worldName, 0L);
        } else if (ratio < 1.0) {
            // slow down
            final long count = this.counts.get(worldName);

            if (count <= 0) {
                world.setTime(time + 1);
                this.counts.put(worldName, Math.round(1.0 / ratio) - 1);
            } else {
                this.counts.put(worldName, count - 1);
            }
        } else {
            // Normal
            world.setTime(time + 1);
        }
    }

}
