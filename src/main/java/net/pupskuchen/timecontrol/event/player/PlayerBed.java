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
import net.pupskuchen.timecontrol.nightskipping.NightSkipper;
import net.pupskuchen.timecontrol.util.TCLogger;

public class PlayerBed implements Listener {

    private final TimeControl plugin;
    private final TCLogger logger;
    private final Map<String, NightSkipper> worldSkippers;

    public PlayerBed(final TimeControl plugin) {
        this.plugin = plugin;
        this.logger = plugin.getTCLogger();
        this.worldSkippers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final String worldName = world.getName();
        NightSkipper skipper = worldSkippers.get(worldName);

        logger.debug("%s has entered a bed at %d", player.getName(), world.getTime());

        if (skipper == null) {
            skipper = new NightSkipper(plugin, world);
            this.worldSkippers.put(worldName, skipper);
        }

        skipper.restartGuard();
    }

    @EventHandler
    public void onPlayerBedLeave(final PlayerBedLeaveEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();

        logger.debug("%s has left a bed at %d", player.getName(), world.getTime());

        final NightSkipper skipper = worldSkippers.get(world.getName());

        if (skipper == null) {
            return;
        }

        skipper.skipNight();
    }
}
