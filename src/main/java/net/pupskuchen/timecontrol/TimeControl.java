package net.pupskuchen.timecontrol;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitRunnable;
import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.event.player.PlayerBed;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControl extends JavaPlugin {

    private ConfigManager cm;
    private TCLogger logger;

    // needed for the plugin to actually be able to be loaded in a server
    public TimeControl() {}

    // needed for unit tests with MockBukkit
    protected TimeControl(JavaPluginLoader loader, PluginDescriptionFile description,
            File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        logger = new TCLogger(this);
        registerConfig();
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
        cm.initializeDebugMode();
        cm.registerSerializables();
        cm.validate();
    }

    private void registerEvents() {
        if (!cm.nightSkippingDisabledGlobally()) {
            getServer().getPluginManager().registerEvents(new PlayerBed(this), this);
        }
    }

    private void registerRunnables() {
        final Runnable runnable = new Runnable(this);
        runnable.enableForWorlds(getEnabledWorlds());
    }

    private void setDaylightCycle(final boolean value) {
        getEnabledWorlds().forEach(world -> {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, value);
            logger.info("Set game rule \"%s\" to \"%b\" for world \"%s\".",
                    GameRule.DO_DAYLIGHT_CYCLE.getName(), value, world.getName());
        });
    }

    public List<World> getEnabledWorlds() {
        return getServer().getWorlds().stream()
                .filter(world -> cm.getWorlds().contains(world.getName()))
                .collect(Collectors.toList());
    }

    public ConfigManager getConfigManager() {
        return this.cm;
    }

    public TCLogger getTCLogger() {
        return this.logger;
    }
}
