package net.pupskuchen.timecontrol.nightskipping;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeUtil;

/**
 * Tests for 1.13 (manual skip scheduling)
 */
public class LegacyNightSkipperTest {
    private final TimeControl plugin = mock(TimeControl.class);
    private final ConfigHandler config = mock(ConfigHandler.class);
    private final TCLogger logger = mock(TCLogger.class);
    private final World world = mock(World.class);

    private ServerMock server;

    List<List<Player>> players = List.of(NightSkipperTest.getPlayers(0, 50, 0),
            NightSkipperTest.getPlayers(100, 50, 0), NightSkipperTest.getPlayers(100, 100, 50),
            NightSkipperTest.getPlayers(100, 100, 100));

    @BeforeEach
    public void setup() {
        server = spy(MockBukkit.mock());
        when(server.getBukkitVersion()).thenReturn("1.13-something");
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigHandler()).thenReturn(config);
        when(plugin.getServer()).thenReturn(server);
        when(world.isGameRule("playersSleepingPercentage")).thenReturn(false);
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unmock();
    }

    @Test
    public void scheduleNightSkip() {
        when(config.isPercentageEnabled(world)).thenReturn(true);
        when(config.getConfigPercentage(world)).thenReturn(50);
        when(world.getPlayers()).thenReturn(players.get(0)).thenReturn(players.get(2));
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);

            skipper.scheduleSkip();
            server.getScheduler().performTicks(100);
            verify(world, times(0)).setTime(anyLong());

            server.getScheduler().performOneTick();
            verify(world).setTime(anyLong());
        }
    }

    @Test
    public void instantSkipWhenAllAsleep() {
        when(config.isPercentageEnabled(world)).thenReturn(true);
        when(config.getConfigPercentage(world)).thenReturn(100);
        when(world.getPlayers()).thenReturn(players.get(2));
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);

            verify(world, times(0)).setTime(anyLong());
            skipper.skipNight();
            verify(world).setTime(anyLong());
        }
    }

    @Test
    public void dontScheduleWhenThresholdMet() {
        when(config.isPercentageEnabled(world)).thenReturn(true);
        when(config.getConfigPercentage(world)).thenReturn(100);
        when(world.getPlayers()).thenReturn(players.get(2));

        final NightSkipper skipper = new NightSkipper(plugin, world);
        skipper.scheduleSkip();
        server.getScheduler().performTicks(110);

        verifyNoInteractions(logger);
        verify(world, times(0)).setTime(anyLong());
    }
}
