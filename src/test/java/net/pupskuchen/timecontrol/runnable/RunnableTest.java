package net.pupskuchen.timecontrol.runnable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.timer.WorldTimer;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TickUtil;

@ExtendWith(MockitoExtension.class)
public class RunnableTest {
    @Mock(name = "plugin")
    final TimeControl plugin = mock(TimeControl.class);
    @Mock
    final ConfigHandler configManager = mock(ConfigHandler.class);
    @Mock
    final TCLogger logger = mock(TCLogger.class);

    private WorldTimer worldTimer;
    private ServerMock server;
    private BukkitSchedulerMock scheduler;

    @BeforeEach
    public void setup() {
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigHandler()).thenReturn(configManager);
        server = MockBukkit.mock();
        scheduler = server.getScheduler();
        worldTimer = new WorldTimer(plugin);
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unmock();
    }

    @Test
    public void runCycles() {
        WorldMock world1 = server.addSimpleWorld("world_one");
        WorldMock world2 = server.addSimpleWorld("world_two");
        WorldMock disabledWorld = server.addSimpleWorld("world_disabled");

        world1.setTime(0);
        world2.setTime(13000);
        disabledWorld.setTime(0);

        when(configManager.isWorldEnabled(world1)).thenReturn(true);
        when(configManager.isWorldEnabled(world2)).thenReturn(true);
        when(configManager.isWorldEnabled(disabledWorld)).thenReturn(false);

        when(configManager.getDay(world1)).thenReturn(20);
        when(configManager.getNight(world1)).thenReturn(5);
        when(configManager.getDay(world2)).thenReturn(40);
        when(configManager.getNight(world2)).thenReturn(5);

        try (MockedStatic<TickUtil> mock = mockStatic(TickUtil.class)) {
            mock.when(() -> TickUtil.cycleMinsToTickRatio(5)).thenReturn(2d);
            mock.when(() -> TickUtil.cycleMinsToTickRatio(20)).thenReturn(0.5d);

            worldTimer.enableForWorlds(Arrays.asList(world1, world1, world2, disabledWorld));

            verify(logger, times(1)).info("Enabling custom time control for world \"%s\".",
                    "world_one");
            verify(logger, times(1)).info("Enabling custom time control for world \"%s\".",
                    "world_two");

            assertEquals(0, world1.getTime());
            assertEquals(13000, world2.getTime());

            scheduler.performTicks(2);

            assertEquals(1, world1.getTime());
            assertEquals(13004, world2.getTime());
        }
    }

    @Test
    public void runCyclesOriginalSpeed() {
        WorldMock world = server.addSimpleWorld("world_one");

        world.setTime(0);

        when(configManager.isWorldEnabled(world)).thenReturn(true);
        when(configManager.getDay(world)).thenReturn(10);
        when(configManager.getNight(world)).thenReturn(10);

        try (MockedStatic<TickUtil> mock = mockStatic(TickUtil.class)) {
            mock.when(() -> TickUtil.cycleMinsToTickRatio(10)).thenReturn(1d);

            worldTimer.enableForWorld(world);

            assertEquals(0, world.getTime());
            scheduler.performTicks(2);
            assertEquals(2, world.getTime());
        }
    }
}
