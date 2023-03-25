package net.pupskuchen.timecontrol.event.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.util.NightSkipper;

public class PlayerBed implements Listener {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Map<String, NightSkipper> worldSkippers;

    public PlayerBed(final JavaPlugin plugin, final ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.worldSkippers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        if (!configManager.isNightSkippingEnabled()) {
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        final World world = event.getPlayer().getWorld();
        final String worldName = world.getName();
        NightSkipper skipper = worldSkippers.get(worldName);

        if (skipper == null) {
            skipper = new NightSkipper(plugin, configManager, world);
            this.worldSkippers.put(worldName, skipper);
        }

        skipper.restartGuard();
    }

    @EventHandler
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        if (!configManager.isNightSkippingEnabled()) {
            return;
        }

        final String worldName = event.getPlayer().getWorld().getName();
        final NightSkipper skipper = worldSkippers.get(worldName);

        if (skipper == null) {
            return;
        }

        skipper.skipNight();
    }
}
