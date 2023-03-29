package net.pupskuchen.timecontrol.nightskipping;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
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
import org.mockito.MockedConstruction;
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
    private NightSkipGuard skipGuard;

    List<List<Player>> players =
            List.of(this.getPlayers(false, false, false), this.getPlayers(true, false, false),
                    this.getPlayers(true, true, false), this.getPlayers(true, true, true));

    @BeforeEach
    public void setup() {
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigManager()).thenReturn(configManager);
        try (MockedConstruction<NightSkipGuard> mock = mockConstruction(NightSkipGuard.class)) {
            skipper = new NightSkipper(plugin, world);
            skipGuard = mock.constructed().get(0);
        }
    }

    private Player getPlayer(boolean sleeping) {
        return when(mock(Player.class).isSleeping()).thenReturn(sleeping).getMock();
    }

    private List<Player> getPlayers(Boolean... sleeping) {
        return Arrays.stream(sleeping).map(asleep -> this.getPlayer(asleep))
                .collect(Collectors.toList());
    };

    @Test
    public void skippableByGameRule() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);

        for(int i = 0; i <= 3; i++) {
            when(world.getPlayers()).thenReturn(players.get(i));
            skipper.restartGuard();
        }

        verify(skipGuard, times(2)).makeSkippable();
    }

    @Test
    public void skippableByFallback() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenThrow(new NoSuchFieldError());

        for(int i = 0; i <= 3; i++) {
            when(world.getPlayers()).thenReturn(players.get(i));
            skipper.restartGuard();
        }

        verify(logger, times(4)).warn("Could not fetch game-rule value 'playersSleepingPercentage!" +
        " Please enable players-sleeping-percentage in the plugin configuration.");
        verify(logger, times(4)).warn("Using fallback percentage of %d %%", 100);
        verify(skipGuard, times(3)).makeSkippable();
    }

    @Test
    public void skippableByConfiguration() {
        when(configManager.isPercentageEnabled()).thenReturn(true);
        when(configManager.getConfigPercentage()).thenReturn( 50);

        for(int i = 0; i <= 3; i++) {
            when(world.getPlayers()).thenReturn(players.get(i));
            skipper.restartGuard();
        }

        verify(skipGuard, times(2)).makeSkippable();
    }

    @Test
    public void skipNightNotSkippable() {
        when(skipGuard.isSkippable()).thenReturn(false);
        skipper.skipNight();
        verifyNoInteractions(world);
        verifyNoInteractions(logger);
    }

    @Test
    public void skipNightNoSleepAllowed() {
        when(skipGuard.isSkippable()).thenReturn(true);

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
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
        when(skipGuard.isSkippable()).thenReturn(true);
        when(world.getPlayers()).thenReturn(players.get(0));

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            skipper.skipNight();
            verifyNoInteractions(logger);
            verify(skipGuard, times(1)).cancel();
        }
    }

    @Test
    public void skipNight() {
        when(configManager.isPercentageEnabled()).thenReturn(false);
        when(world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE)).thenReturn( 50);
        when(skipGuard.isSkippable()).thenReturn(true);
        when(world.getPlayers()).thenReturn(players.get(2));

        try(MockedStatic<TimeUtil> mock = mockStatic(TimeUtil.class)) {
            mock.when(() -> TimeUtil.sleepAllowed(world)).thenReturn(true);
            mock.when(() -> TimeUtil.getWakeTime(world)).thenReturn(123);
            skipper.skipNight();
            verify(skipGuard, times(0)).cancel();
            verify(world, times(1)).setTime(123);
            verify(logger, times(1)).info("The night has been skipped by sleeping");
        }
    }
}
