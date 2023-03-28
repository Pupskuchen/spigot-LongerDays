package net.pupskuchen.timecontrol.nightskipping;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.pupskuchen.timecontrol.TimeControl;

@ExtendWith(MockitoExtension.class)
public class NightSkipGuardTest {
    @Mock(name = "plugin")
    final TimeControl plugin = mock(TimeControl.class);

    @InjectMocks
    private NightSkipGuard guard;
    private ServerMock server;

    @BeforeEach
    public void setup() {
        server = MockBukkit.mock();
    }

    @AfterEach
    public void teardown() {
        MockBukkit.unmock();
    }

    @Test
    public void initialSkippable() {
        assertFalse(guard.isSkippable());
    }

    @Test
    public void makeSkippable() {
        guard.makeSkippable();
        assertFalse(guard.isSkippable());
        server.getScheduler().performTicks(100);
        assertFalse(guard.isSkippable());
        server.getScheduler().performOneTick();
        assertTrue(guard.isSkippable());
    }

    @Test
    public void cancel() {
        guard.makeSkippable();
        guard.cancel();
        server.getScheduler().performTicks(101);
        assertFalse(guard.isSkippable());
        server.getScheduler().performTicks(101);
        assertFalse(guard.isSkippable());
    }
}
