package net.pupskuchen.timecontrol.event.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.nightskipping.NightSkipper;
import net.pupskuchen.timecontrol.util.TCLogger;

@ExtendWith(MockitoExtension.class)
public class PlayerBedTest {

    @Mock
    final TimeControl plugin = mock(TimeControl.class);
    @Mock
    final TCLogger logger = mock(TCLogger.class);
    @Mock
    final ConfigHandler config = mock(ConfigHandler.class);

    @Mock
    final PlayerBedEnterEvent enterEvent = mock(PlayerBedEnterEvent.class);
    @Mock
    final PlayerBedLeaveEvent leaveEvent = mock(PlayerBedLeaveEvent.class);
    @Mock
    final World world = mock(World.class);
    @Mock
    final Player player = mock(Player.class);

    private PlayerBed playerBed;

    @BeforeEach
    public void setup() {
        when(plugin.getTCLogger()).thenReturn(logger);
        when(plugin.getConfigManager()).thenReturn(config);
        playerBed = new PlayerBed(plugin);
    }

    private void stubPlayer() {
        when(player.getWorld()).thenReturn(world);
        when(player.getName()).thenReturn("somePlayerName");
    }

    private void stubEnterEvent() {
        when(enterEvent.getPlayer()).thenReturn(player);
    }

    private void stubLeaveEvent() {
        when(leaveEvent.getPlayer()).thenReturn(player);
    }

    private void stubWorld() {
        when(world.getName()).thenReturn("someWorld");
        when(world.getTime()).thenReturn(15000L);
    }

    @Test
    public void doNothingOnEnterIfEnterNotOk() {
        when(config.isWorldEnabled(world)).thenReturn(true);
        when(config.isNightSkippingEnabled(world)).thenReturn(true);
        when(enterEvent.getBedEnterResult()).thenReturn(PlayerBedEnterEvent.BedEnterResult.NOT_POSSIBLE_NOW);
        this.stubEnterEvent();
        when(player.getWorld()).thenReturn(world);

        playerBed.onPlayerBedEnter(enterEvent);

        verify(enterEvent, times(1)).getBedEnterResult();
        verifyNoInteractions(logger);
    }

    @Test
    public void createSkipperAndStartGuard() {
        when(config.isWorldEnabled(world)).thenReturn(true);
        when(config.isNightSkippingEnabled(world)).thenReturn(true);
        this.stubEnterEvent();
        this.stubPlayer();
        this.stubWorld();
        when(enterEvent.getBedEnterResult()).thenReturn(PlayerBedEnterEvent.BedEnterResult.OK);

        try (MockedConstruction<NightSkipper> mock = mockConstruction(NightSkipper.class)) {
            playerBed.onPlayerBedEnter(enterEvent);

            assertEquals(1, mock.constructed().size());
            NightSkipper skipper = mock.constructed().get(0);

            verify(logger, times(1)).debug("%s (@ %s) entered a bed at %d", "somePlayerName", "someWorld", 15000L);
            verifyNoInteractions(skipper);
        }
    }

    @Test
    public void restartExistingGuard() {
        when(config.isWorldEnabled(world)).thenReturn(true);
        when(config.isNightSkippingEnabled(world)).thenReturn(true);
        this.stubEnterEvent();
        this.stubPlayer();
        this.stubWorld();
        when(enterEvent.getBedEnterResult()).thenReturn(PlayerBedEnterEvent.BedEnterResult.OK);

        try (MockedConstruction<NightSkipper> mock = mockConstruction(NightSkipper.class)) {
            playerBed.onPlayerBedEnter(enterEvent);
            playerBed.onPlayerBedEnter(enterEvent);

            assertEquals(1, mock.constructed().size());
            NightSkipper skipper = mock.constructed().get(0);

            verify(logger, times(2)).debug("%s (@ %s) entered a bed at %d", "somePlayerName", "someWorld", 15000L);
            verifyNoInteractions(skipper);
        }
    }

    @Test
    public void doNothingOnLeaveIfNoSkipperExists() {
        when(config.isWorldEnabled(world)).thenReturn(true);
        when(config.isNightSkippingEnabled(world)).thenReturn(true);
        this.stubLeaveEvent();
        this.stubPlayer();
        this.stubWorld();

        try (MockedConstruction<NightSkipper> mock = mockConstruction(NightSkipper.class)) {
            playerBed.onPlayerBedLeave(leaveEvent);

            assertEquals(0, mock.constructed().size());

            verify(logger, times(1)).debug("%s (@ %s) left a bed at %d", "somePlayerName", "someWorld", 15000L);
        }
    }

    @Test
    public void skipNightOnLeave() {
        when(config.isWorldEnabled(world)).thenReturn(true);
        when(config.isNightSkippingEnabled(world)).thenReturn(true);
        this.stubEnterEvent();
        this.stubLeaveEvent();
        this.stubPlayer();
        this.stubWorld();
        when(enterEvent.getBedEnterResult()).thenReturn(PlayerBedEnterEvent.BedEnterResult.OK);

        try (MockedConstruction<NightSkipper> mock = mockConstruction(NightSkipper.class)) {
            playerBed.onPlayerBedEnter(enterEvent);
            playerBed.onPlayerBedLeave(leaveEvent);

            assertEquals(1, mock.constructed().size());
            NightSkipper skipper = mock.constructed().get(0);

            verify(logger, times(1)).debug("%s (@ %s) left a bed at %d", "somePlayerName", "someWorld", 15000L);
            verify(skipper, times(1)).skipNight();
        }
    }
}
