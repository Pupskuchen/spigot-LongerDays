package net.pupskuchen.timecontrol.event.world;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.bukkit.World;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.pupskuchen.timecontrol.timer.WorldTimer;

public class WorldEventsTest {
    private WorldTimer timer = mock(WorldTimer.class);
    private WorldEvents listener;


    @BeforeEach
    public void setup() {
        listener = new WorldEvents(timer);
    }

    @Test
    public void onWorldLoad() {
        WorldLoadEvent event = mock(WorldLoadEvent.class);
        World world = mock(World.class);

        when(event.getWorld()).thenReturn(world);

        listener.onWorldLoad(event);

        verify(timer).enableForWorld(world);
    }

    @Test
    public void onWorldUnload() {
        WorldUnloadEvent event = mock(WorldUnloadEvent.class);
        World world = mock(World.class);

        when(event.getWorld()).thenReturn(world);

        listener.onWorldUnload(event);

        verify(timer).disableForWorld(world);
    }
}
