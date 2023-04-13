package net.pupskuchen.timecontrol.nightskipping;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.GameRule;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class NightSkipperTest {
    final TimeControl plugin = mock(TimeControl.class);
    final ConfigHandler configManager = mock(ConfigHandler.class);
    final TCLogger logger = mock(TCLogger.class);
    final World world = mock(World.class);
    final Server server = mock(Server.class);

    List<List<Player>> players = List.of(getPlayers(0, 50, 0), getPlayers(100, 50, 0),
            getPlayers(100, 100, 50), getPlayers(100, 100, 100));

    @BeforeEach
    public void setup() {
        when(server.getBukkitVersion()).thenReturn("1.19-something");
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigHandler()).thenReturn(configManager);
        when(plugin.getServer()).thenReturn(server);
        when(world.isGameRule("playersSleepingPercentage")).thenReturn(true);
    }

    private static Player getPlayer(int sleepTicks) {
        return when(mock(Player.class).getSleepTicks()).thenReturn(sleepTicks).getMock();
    }

    public static List<Player> getPlayers(int... sleepTicks) {
        return Arrays.stream(sleepTicks).mapToObj(asleep -> getPlayer(asleep))
                .collect(Collectors.toList());
    };

    @Test
    public void skipNightNoSleepAllowed() {
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try (MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(false);
            skipper.skipNight();
            verify(world, times(0)).setTime(anyLong());
            verify(world, times(0)).setTime(anyInt());
            verifyNoInteractions(logger);
        }
    }

    @Test
    public void skipNightThresholdNotMet() {
        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(0));

        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            skipper.skipNight();
            verifyNoInteractions(logger);
        }
    }

    @Test
    public void skipNightByGameRule() {
        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(2));
        when(world.getName()).thenReturn("fancy-world");

        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("Skipped the night on world \"%s\".", "fancy-world");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void skipNightByByFallback() {
        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenThrow(new NoSuchFieldError());
        when(world.getPlayers()).thenReturn(players.get(2), players.get(3));
        when(world.getName()).thenReturn("fancy-world");

        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(0)).setTime(anyLong());
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).warn("Failed to read game rule \"playersSleepingPercentage\" for world \"%s\"!",
                "fancy-world");
            verify(logger, times(1)).warn("Please enable players-sleeping-percentage in the plugin config.");
            verify(logger, times(1)).warn("Using fallback percentage of %d %%.", 100);

            verify(logger, times(1)).info("Skipped the night on world \"%s\".", "fancy-world");        }

    }

    @Test
    public void skipNightByByConfiguration() {
        when(configManager.isPercentageEnabled(world)).thenReturn(true);
        when(configManager.getConfigPercentage(world)).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(2));
        when(world.getName()).thenReturn("fancy-world");

        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("Skipped the night on world \"%s\".", "fancy-world");
        }
    }

    @Test
    public void doNotScheduleSkip() {
        final NightSkipper skipper = new NightSkipper(plugin, world);
        reset(world);

        skipper.scheduleSkip();

        verifyNoInteractions(logger);
        verifyNoInteractions(world);
    }

    @Test
    public void alwaysAllowSkipAt0Percentage() {
        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 0);
        when(world.getPlayers()).thenReturn(players.get(1));
        when(world.getName()).thenReturn("fancy-world");
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("Skipped the night on world \"%s\".", "fancy-world");
        }
    }

    @Test
    public void neverAllowSkipAbove100Percentage() {
        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 101);
        when(world.getPlayers()).thenReturn(players.get(3));
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            skipper.skipNight();
            verify(world, times(0)).setTime(anyLong());
            verifyNoInteractions(logger);
        }
    }

    @Test
    public void shouldIgnoreSleepingIgnoredPlayers() {
        List<Player> notIgnored = getPlayers(100, 100);
        List<Player> ignored = getPlayers(0, 10, 0).stream()
                .map(player -> (Player)when(player.isSleepingIgnored()).thenReturn(true).getMock())
                .collect(Collectors.toList());
        List<Player> allPlayers = Stream.concat(notIgnored.stream(), ignored.stream()).collect(Collectors.toList());

        when(configManager.isPercentageEnabled(world)).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn(100);
        when(world.getName()).thenReturn("fancy-world");
        when(world.getPlayers()).thenReturn(allPlayers);
        final NightSkipper skipper = new NightSkipper(plugin, world);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world).getPlayers();
            verify(world, times(1)).setTime(123);
        }
    }
}
