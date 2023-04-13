package net.pupskuchen.timecontrol.util;

public class TimeRange {
    public final int start;
    public final int end;

    public TimeRange(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public boolean isInRange(final long time) {
        return time >= start && time <= end;
    }

    public int duration() {
        // Since we're thinking in ticks here, even if start equals end, we still count it as 1 tick
        // duration, so we add 1 here.
        return end - start + 1;
    }
}
