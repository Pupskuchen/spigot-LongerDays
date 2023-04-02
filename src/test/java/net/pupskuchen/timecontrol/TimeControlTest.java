package net.pupskuchen.timecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import org.bukkit.GameRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControlTest {
    private TimeControl plugin;
    private ServerMock server;

    // private TCLogger logger;
    private ConfigHandler config;
    // private Runnable runnable;

    private WorldMock[] worlds = new WorldMock[4];

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();

        String[] worlds = {"one", "two", "three", "four"};
        for (int i = 0; i < worlds.length; i++) {
            WorldMock world = server.addSimpleWorld("world_" + worlds[i]);
            this.worlds[i] = world;
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }

        try (MockedConstruction<ConfigHandler> configMock = mockConstruction(ConfigHandler.class);
                MockedConstruction<TCLogger> loggerMock = mockConstruction(TCLogger.class);
                MockedConstruction<Runnable> runnableMock = mockConstruction(Runnable.class)) {
            plugin = MockBukkit.load(TimeControl.class);
            // logger = plugin.getTCLogger();
            config = plugin.getConfigManager();
            assertEquals(1, runnableMock.constructed().size());
            // runnable = runnableMock.constructed().get(0);
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void onEnable() {
        verify(config).initializeDebugMode();
        verify(config).validate();
    }

    // TODO: properly test initialization
}
