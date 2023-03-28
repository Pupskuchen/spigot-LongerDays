package net.pupskuchen.timecontrol.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import net.pupskuchen.timecontrol.TimeControl;

@ExtendWith(MockitoExtension.class)
public class TCLoggerTest {

    @Mock
    final TimeControl plugin = mock(TimeControl.class);
    @Mock
    final Logger logger = mock(Logger.class);
    @Mock
    final Logger debugLogger = mock(Logger.class);

    private TCLogger tcLogger;

    @BeforeEach
    public void setup() {
        when(plugin.getName()).thenReturn("PluginName");
        when(plugin.getLogger()).thenReturn(logger);

        try (MockedStatic<Logger> mocked = mockStatic(Logger.class)) {
            mocked.when(() -> Logger.getLogger("PluginName DEBUG")).thenReturn(debugLogger);

            tcLogger = new TCLogger(plugin);
        }
    }

    @Test
    public void warnDebugModeDisabled() {
        tcLogger.debug("asdf");
        verify(debugLogger).warning("debug mode hasn't been configured yet, ignoring message");
        verifyNoMoreInteractions(debugLogger);
        verifyNoInteractions(logger);
    }

    @Test
    public void ignoreDebugModeDisabled() {
        tcLogger.setDebug(false);
        tcLogger.debug("asdf");
        verifyNoInteractions(debugLogger);
        verifyNoInteractions(logger);
    }

    @Test
    public void logDebug() {
        tcLogger.setDebug(true);
        tcLogger.debug("asdf");
        verify(debugLogger).info("asdf");
        verifyNoMoreInteractions(debugLogger);
        verifyNoInteractions(logger);
    }

    @Test
    public void logInfo() {
        tcLogger.info("asdf");
        verify(logger).info("asdf");
        verifyNoMoreInteractions(logger);
        verifyNoInteractions(debugLogger);
    }

    @Test
    public void logWarn() {
        tcLogger.warn("asdf");
        verify(logger).warning("asdf");
        verifyNoMoreInteractions(logger);
        verifyNoInteractions(debugLogger);
    }

    @Test
    public void logError() {
        tcLogger.error("asdf");
        verify(logger).severe("asdf");
        verifyNoMoreInteractions(logger);
        verifyNoInteractions(debugLogger);
    }

    @Test
    public void format() {
        tcLogger.info("this %d %s will be formatted / %b", 1, "string", true);
        verify(logger).info("this 1 string will be formatted / true");
        verifyNoMoreInteractions(logger);
        verifyNoInteractions(debugLogger);
    }
}
