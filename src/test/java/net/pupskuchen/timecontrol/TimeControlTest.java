package net.pupskuchen.timecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.GameRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.runnable.Runnable;
import net.pupskuchen.timecontrol.util.TCLogger;

public class TimeControlTest {
    private TimeControl plugin;
    private ServerMock server;

    private TCLogger logger;
    private ConfigManager configManager;
    private Runnable runnable;

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

        try (MockedConstruction<ConfigManager> configMock = mockConstruction(ConfigManager.class);
                MockedConstruction<TCLogger> loggerMock = mockConstruction(TCLogger.class);
                MockedConstruction<Runnable> runnableMock = mockConstruction(Runnable.class)) {
            plugin = MockBukkit.load(TimeControl.class);
            logger = plugin.getTCLogger();
            configManager = plugin.getConfigManager();
            when(configManager.getWorlds()).thenReturn(
                    new HashSet<String>(Arrays.asList("world_one", "world_two", "world_three")));
            server.getScheduler().performOneTick();
            assertEquals(1, runnableMock.constructed().size());
            runnable = runnableMock.constructed().get(0);
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void onEnable() {
        verify(configManager).isDebug();
        verify(configManager).validate();
        verify(logger).setDebug(anyBoolean());
    }

    @Test
    public void registerRunnables() {
        verify(runnable, times(1)).runCycles(server.getWorld("world_one"));
        verify(runnable, times(1)).runCycles(server.getWorld("world_two"));
        verify(runnable, times(1)).runCycles(server.getWorld("world_three"));
        verifyNoMoreInteractions(runnable);
    }

    @Test
    public void setDaylightCycle() {
        for (int i = 0; i < worlds.length - 1; i++) {
            WorldMock world = worlds[i];
            assertFalse(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        }
    }
}
