package net.pupskuchen.timecontrol.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

public class TickUtilTest {
    @Test
    public void cycleMinsToTickRatio() {
        assertEquals(0.5, TickUtil.cycleMinsToTickRatio(20, 12000));
        assertEquals(1, TickUtil.cycleMinsToTickRatio(10, 12000));
        assertEquals(2, TickUtil.cycleMinsToTickRatio(5, 12000));
        assertEquals(round(1 / 3d), TickUtil.cycleMinsToTickRatio(30, 12000));

        assertEquals(1, TickUtil.cycleMinsToTickRatio(0.833333, 1000));
        assertEquals(round(50 / (60 * 25d)), TickUtil.cycleMinsToTickRatio(25, 1000));

        assertEquals(1, TickUtil.cycleMinsToTickRatio(8.333333, 10000));
        assertEquals(round(1 / 3d), TickUtil.cycleMinsToTickRatio(25, 10000));
    }

    private double round(final double value) {
        return new BigDecimal(value).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }
}
