package net.pupskuchen.timecontrol.event.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import net.pupskuchen.timecontrol.runnable.Runnable;

public class WorldEvents implements Listener {

    private final Runnable runnable;

    public WorldEvents(final Runnable runnable) {
        this.runnable = runnable;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.runnable.enableForWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldLoad(WorldUnloadEvent event) {
        this.runnable.disableForWorld(event.getWorld());
    }
}
