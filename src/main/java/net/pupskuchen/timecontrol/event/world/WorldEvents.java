package net.pupskuchen.timecontrol.event.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import net.pupskuchen.timecontrol.timer.WorldTimer;

public class WorldEvents implements Listener {

    private final WorldTimer worldTimer;

    public WorldEvents(final WorldTimer worldTimer) {
        this.worldTimer = worldTimer;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.worldTimer.enableForWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        this.worldTimer.disableForWorld(event.getWorld());
    }
}
