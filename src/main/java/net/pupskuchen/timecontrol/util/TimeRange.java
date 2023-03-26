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
}
