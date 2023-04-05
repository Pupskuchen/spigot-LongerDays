package net.pupskuchen.timecontrol;

import java.io.File;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.event.player.PlayerBed;
import net.pupskuchen.timecontrol.event.world.WorldEvents;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControl extends JavaPlugin {

    private ConfigHandler config;
    private TCLogger logger;
    private Runnable runnable;

    // needed for the plugin to actually be able to be loaded in a server
    public TimeControl() {}

    // needed for unit tests with MockBukkit
    protected TimeControl(JavaPluginLoader loader, PluginDescriptionFile description,
            File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();

        logger = new TCLogger(this);
        registerConfig();
        runnable = new Runnable(this);

        registerPlayerBedEvents(pluginManager);
        registerWorldEvents(pluginManager);

        runnable.enableForWorlds(config.getWorlds());
    }

    @Override
    public void onDisable() {
        runnable.disableAll();
        runnable = null;
        logger = null;
        config = null;
    }

    private void registerConfig() {
        saveDefaultConfig();
        config = new ConfigHandler(this);
        config.initializeDebugMode();
        config.validate();
    }

    private void registerPlayerBedEvents(final PluginManager pluginManager) {
        if (!config.nightSkippingDisabledGlobally()) {
            pluginManager.registerEvents(new PlayerBed(this), this);
            logger.debug("Set up player bed event listener.");
        }
    }

    private void registerWorldEvents(final PluginManager pluginManager) {
        pluginManager.registerEvents(new WorldEvents(runnable), this);
        logger.debug("Set up world event listener.");
    }

    public ConfigHandler getConfigHandler() {
        return config;
    }

    public TCLogger getTCLogger() {
        return logger;
    }
}
