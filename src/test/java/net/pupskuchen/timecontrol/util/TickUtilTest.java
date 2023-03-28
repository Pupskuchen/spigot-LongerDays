package net.pupskuchen.timecontrol.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TickUtilTest {
    @Test
    public void cycleMinsToTickRatio() {
        assertEquals(0.5, TickUtil.cycleMinsToTickRatio(20));
        assertEquals(1, TickUtil.cycleMinsToTickRatio(10));
        assertEquals(2, TickUtil.cycleMinsToTickRatio(5));
        assertEquals(1 / 3d, TickUtil.cycleMinsToTickRatio(30));
    }
}
