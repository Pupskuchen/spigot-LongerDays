package net.pupskuchen.timecontrol.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.bukkit.GameRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.util.TCLogger;

@ExtendWith(MockitoExtension.class)
public class WorldTimerTest {
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

        when(configManager.getDurations(world1))
                .thenReturn(new Durations<Double, Double>(20d, 5d, 20d, 5d));
        when(configManager.getDurations(world2))
                .thenReturn(new Durations<Double, Double>(40d, 5d, 40d, 5d));

        worldTimer.enableForWorlds(Arrays.asList(world1, world1, world2, disabledWorld));

        verify(logger).info("Enabling custom time control for world \"%s\".", "world_one");
        verify(logger).info("Enabling custom time control for world \"%s\".", "world_two");
        verify(logger).debug("Custom time control started.");

        assertEquals(0, world1.getTime());
        assertEquals(13000, world2.getTime());

        scheduler.performTicks(2);

        assertEquals(1, world1.getTime());
        assertEquals(13004, world2.getTime());
    }

    @Test
    public void runCyclesOriginalSpeed() {
        WorldMock world = server.addSimpleWorld("world_one");

        world.setTime(0);

        when(configManager.isWorldEnabled(world)).thenReturn(true);
        when(configManager.getDurations(world))
                .thenReturn(new Durations<Double, Double>(10d, 10d, 10d, 10d));

        worldTimer.enableForWorld(world);

        assertEquals(0, world.getTime());
        scheduler.performTicks(2);
        assertEquals(2, world.getTime());
    }

    @Test
    public void disableWorld() {
        WorldMock world1 = server.addSimpleWorld("world_one");
        WorldMock world2 = server.addSimpleWorld("world_two");
        WorldMock disabledWorld = server.addSimpleWorld("world_disabled");

        world1.setTime(0);
        world2.setTime(13000);
        disabledWorld.setTime(0);

        world1.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world2.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        disabledWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

        when(configManager.isWorldEnabled(world1)).thenReturn(true);
        when(configManager.isWorldEnabled(world2)).thenReturn(true);

        when(configManager.getDurations(world1))
                .thenReturn(new Durations<Double, Double>(20d, 5d, 20d, 5d));
        when(configManager.getDurations(world2))
                .thenReturn(new Durations<Double, Double>(40d, 5d, 40d, 5d));

        worldTimer.enableForWorlds(Arrays.asList(world1, world2));

        assertFalse(world1.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        assertFalse(world2.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        assertTrue(disabledWorld.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));

        scheduler.performTicks(2);
        assertEquals(1, world1.getTime());
        assertEquals(13004, world2.getTime());

        reset(logger);

        worldTimer.disableForWorld(disabledWorld);
        verifyNoInteractions(logger);

        worldTimer.disableForWorld(world1);
        assertTrue(world1.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        verify(logger).info("Disabling custom time control for world \"%s\".", "world_one");
        verify(logger, times(0)).debug("Custom time control stopped.");

        scheduler.performTicks(2);
        assertEquals(1, world1.getTime());
        assertEquals(13008, world2.getTime());

        worldTimer.disableAll();
        verify(logger).info("Disabling custom time control for world \"%s\".", "world_two");
        assertFalse(world2.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        verify(logger).debug("Custom time control stopped.");

        scheduler.performTicks(2);
        assertEquals(1, world1.getTime());
        assertEquals(13008, world2.getTime());
    }
}
