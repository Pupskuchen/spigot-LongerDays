package net.pupskuchen.timecontrol.nightskipping;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import net.pupskuchen.timecontrol.TimeControl;

public class NightSkipSchedulerTest {
    private NightSkipper skipper = mock(NightSkipper.class);
    private TimeControl plugin = mock(TimeControl.class);

    private NightSkipScheduler skipScheduler;
    private BukkitSchedulerMock serverScheduler;

    @BeforeEach
    public void setup() {
        serverScheduler = MockBukkit.mock().getScheduler();
        skipScheduler = new NightSkipScheduler(plugin, skipper, 10);
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unmock();
    }

    @Test
    public void doNothingInitially() {
        serverScheduler.performTicks(1000);
        verifyNoInteractions(skipper);
    }

    @Test
    public void doNothingWhenCancelled() {
        skipScheduler.scheduleSkip();
        skipScheduler.cancel();
        serverScheduler.performTicks(1000);
        verifyNoInteractions(skipper);
    }

    @Test
    public void shouldCallSkipNight() {
        skipScheduler.scheduleSkip();
        serverScheduler.performTicks(10);
        verify(skipper).skipNight();
    }
}
