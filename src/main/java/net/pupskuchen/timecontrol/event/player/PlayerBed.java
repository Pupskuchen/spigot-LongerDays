package net.pupskuchen.timecontrol.event.player;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.nightskipping.NightSkipper;
import net.pupskuchen.timecontrol.util.TCLogger;

public class PlayerBed implements Listener {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final ConfigManager config;
    private final Map<String, NightSkipper> worldSkippers;

    public PlayerBed(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
        this.config = plugin.getConfigManager();
        this.worldSkippers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        if (!config.isNightSkippingEnabled(world)) {
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        final String worldName = world.getName();
        NightSkipper skipper = worldSkippers.get(worldName);

        logger.debug("%s (@ %s) entered a bed at %d", player.getName(), worldName, world.getTime());

        if (skipper == null) {
            skipper = new NightSkipper(plugin, world);
            this.worldSkippers.put(worldName, skipper);
        }
    }

    @EventHandler
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        if (!config.isNightSkippingEnabled(world)) {
            return;
        }

        final String worldName = world.getName();

        logger.debug("%s (@ %s) left a bed at %d", player.getName(), worldName, world.getTime());

        final NightSkipper skipper = worldSkippers.get(worldName);

        if (skipper == null) {
            return;
        }

        skipper.skipNight();
    }
}
