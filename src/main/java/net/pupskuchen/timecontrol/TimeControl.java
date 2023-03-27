package net.pupskuchen.timecontrol;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.event.player.PlayerBed;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControl extends JavaPlugin {

    private ConfigManager cm;
    private TCLogger logger;

    @Override
    public void onEnable() {
        logger = new TCLogger(this);
        registerConfig();
        logger.setDebug(cm.isDebug());
        registerEvents();

        new BukkitRunnable() {
            @Override
            public void run() {
                setDaylightCycle(false);
                registerRunnables();
            }
        }.runTask(this);
    }

    @Override
    public void onDisable() {
        setDaylightCycle(true);
    }

    private void registerConfig() {
        saveDefaultConfig();
        cm = new ConfigManager(this);
        cm.validate();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerBed(this, getConfigManager()), this);
    }

    private void registerRunnables() {
        final Runnable runnable = new Runnable(this);
        Bukkit.getWorlds()
                .stream()
                .filter(world -> cm.getWorlds().contains(world.getName()))
                .forEach(runnable::runCycles);
    }

    private void setDaylightCycle(final boolean value) {
        Bukkit.getWorlds()
                .stream()
                .filter(world -> cm.getWorlds().contains(world.getName()))
                .forEach(world -> {
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, value);
                    logger.info("Set game rule \"%s\" to \"%b\" for world \"%s\"",
                            GameRule.DO_DAYLIGHT_CYCLE.getName(), value, world.getName());
                });
    }

    public ConfigManager getConfigManager() {
        return this.cm;
    }

    public TCLogger getTCLogger() {
        return this.logger;
    }
}
