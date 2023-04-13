package net.pupskuchen.timecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import be.seeseemelk.mockbukkit.MockBukkit;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.timer.WorldTimer;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControlTest {
    static {
        System.setProperty("bstats.relocatecheck", "false");
    }

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void onEnable() {
        try (MockedConstruction<ConfigHandler> configMock = mockConstruction(ConfigHandler.class);
                MockedConstruction<TCLogger> loggerMock = mockConstruction(TCLogger.class);
                MockedConstruction<WorldTimer> timerMock = mockConstruction(WorldTimer.class)) {
            final TimeControl plugin = MockBukkit.load(TimeControl.class);

            assertEquals(1, timerMock.constructed().size());
            assertEquals(1, configMock.constructed().size());
            assertEquals(1, loggerMock.constructed().size());

            final ConfigHandler config = plugin.getConfigHandler();
            final WorldTimer timer = timerMock.constructed().get(0);

            assertEquals(loggerMock.constructed().get(0), plugin.getTCLogger());
            assertEquals(configMock.constructed().get(0), config);

            verify(config).initializeDebugMode();
            verify(config).validate();
            verify(config).getWorlds();
            verify(timer).enableForWorlds(anyList());
        }

    }
}
