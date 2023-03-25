package net.pupskuchen.timecontrol;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.event.player.PlayerBed;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TimeControlUtil;

public class TimeControl extends JavaPlugin {

    private ConfigManager cm;

    @Override
    public void onEnable() {
        this.registerConfig();
        this.registerEvents();
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
        this.setDaylightCycle(true);
    }

    private void registerConfig() {
        this.saveDefaultConfig();
        this.cm = new ConfigManager(this.getConfig());
        this.cm.validate();
    }

    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerBed(getConfigManager()), this);
    }

    private void registerRunnables() {
        final Runnable runnable = new Runnable(this);
        Bukkit.getWorlds()
                .stream()
                .filter(world -> this.cm.getWorlds().contains(world.getName()))
                .forEach(runnable::runCycles);
    }

    private void setDaylightCycle(final boolean value) {
        Bukkit.getWorlds()
                .stream()
                .filter(world -> this.cm.getWorlds().contains(world.getName()))
                .forEach(world -> {
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, value);
                    TimeControlUtil.console(
                            "Setting GameRule.DO_DAYLIGHT_CYCLE to " + value + " for world '" + world.getName() + "'");
                });
    }

    public ConfigManager getConfigManager() {
        return this.cm;
    }

}
