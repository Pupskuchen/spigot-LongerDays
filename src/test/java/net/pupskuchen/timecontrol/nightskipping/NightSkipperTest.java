package net.pupskuchen.timecontrol.nightskipping;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeUtil;

@ExtendWith(MockitoExtension.class)
public class NightSkipperTest {
    @Mock
    final TimeControl plugin = mock(TimeControl.class);
    @Mock
    final ConfigManager configManager = mock(ConfigManager.class);
    @Mock
    final TCLogger logger = mock(TCLogger.class);
    @Mock
    final World world = mock(World.class);

    private NightSkipper skipper;

    List<List<Player>> players = List.of(this.getPlayers(0, 50, 0), this.getPlayers(100, 50, 0),
            this.getPlayers(100, 100, 50), this.getPlayers(100, 100, 100));

    @BeforeEach
    public void setup() {
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigManager()).thenReturn(configManager);
            skipper = new NightSkipper(plugin, world);
    }

    private Player getPlayer(int sleepTicks) {
        return when(mock(Player.class).getSleepTicks()).thenReturn(sleepTicks).getMock();
    }

    private List<Player> getPlayers(int... sleepTicks) {
        return Arrays.stream(sleepTicks).mapToObj(asleep -> this.getPlayer(asleep))
                .collect(Collectors.toList());
    };

    @Test
    public void skipNightNoSleepAllowed() {
        try (MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(false);
            skipper.skipNight();
            verifyNoInteractions(world);
            verifyNoInteractions(logger);
        }
    }

    @Test
    public void skipNightThresholdNotMet() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(0));

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            skipper.skipNight();
            verifyNoInteractions(logger);
        }
    }

    @Test
    public void skipNightByGameRule() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(2));

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("The night has been skipped by sleeping");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void skipNightByByFallback() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenThrow(new NoSuchFieldError());
        when(world.getPlayers()).thenReturn(players.get(2), players.get(3));


        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(0)).setTime(anyLong());
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(2)).warn("Could not fetch game-rule value 'playersSleepingPercentage!" +
            " Please enable players-sleeping-percentage in the plugin configuration.");
            verify(logger, times(2)).warn("Using fallback percentage of %d %%", 100);
            verify(logger, times(1)).info("The night has been skipped by sleeping");
        }

    }

    @Test
    public void skipNightByByConfiguration() {
        when(configManager.isPercentageEnabled()).thenReturn(true);
        when(configManager.getConfigPercentage()).thenReturn( 50);
        when(world.getPlayers()).thenReturn(players.get(2));

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("The night has been skipped by sleeping");
        }
    }
}
