package net.pupskuchen.timecontrol.timer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import net.pupskuchen.timecontrol.config.entity.Durations;
import net.pupskuchen.timecontrol.util.TickUtil;
import net.pupskuchen.timecontrol.util.TimeRange;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class WorldStateTest {
    @Test
    public void keepOriginalDoDaylightCycle() {
        WorldState state = new WorldState(new Durations<Double, Double>(1d, 1d, 1d, 1d), false);
        WorldState state2 = new WorldState(new Durations<Double, Double>(1d, 1d, 1d, 1d), true);

        assertFalse(state.originalDoDaylightCycle);
        assertTrue(state2.originalDoDaylightCycle);
    }

    @Test
    public void keepIntermediateTicks() {
        WorldState state = new WorldState(new Durations<Double, Double>(1d, 1d, 1d, 1d), false);

        assertEquals(0, state.getIntermediateTicks());
        state.setIntermediateTicks(1);
        assertEquals(1, state.getIntermediateTicks());
        state.setIntermediateTicks(123);
        assertEquals(123, state.getIntermediateTicks());
    }

    @Test
    public void getApplicableRatio() {
        try (MockedStatic<TickUtil> mock = mockStatic(TickUtil.class)) {
            mock.when(() -> TickUtil.cycleMinsToTickRatio(10d, TimeUtil.DAYTIME.duration()))
                    .thenReturn(1d);
            mock.when(() -> TickUtil.cycleMinsToTickRatio(20d, TimeUtil.NIGHTTIME.duration()))
                    .thenReturn(2d);
            mock.when(() -> TickUtil.cycleMinsToTickRatio(30d, TimeUtil.SUNSET.duration()))
                    .thenReturn(3d);
            mock.when(() -> TickUtil.cycleMinsToTickRatio(40d, TimeUtil.SUNRISE.duration()))
                    .thenReturn(4d);

            WorldState state =
                    new WorldState(new Durations<Double, Double>(10d, 20d, 30d, 40d), false);

            TimeRange[] ranges = new TimeRange[] {TimeUtil.DAYTIME, TimeUtil.NIGHTTIME,
                    TimeUtil.SUNSET, TimeUtil.SUNRISE};

            int i = 0;
            for (; i < ranges.length; i++) {
                TimeRange range = ranges[i];

                assertEquals(i + 1, state.getApplicableRatio(range.start));
                assertEquals(i + 1, state.getApplicableRatio(range.start + range.duration() / 2));
                assertEquals(i + 1, state.getApplicableRatio(range.end));
            }

            assertEquals(4, i);
        }
    }
}
