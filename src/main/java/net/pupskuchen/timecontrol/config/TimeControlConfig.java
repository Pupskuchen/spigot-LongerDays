package net.pupskuchen.timecontrol.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Time control configuration that can be global or for a specific world.
 */
@SerializableAs("TimeControlConfig")
public class TimeControlConfig implements ConfigurationSerializable {

    public static class KEY {
        public static KEY DURATIONS = new KEY("durations");
        public static KEY NIGHT_SKIPPING = new KEY("night-skipping");
        public static KEY PLAYERS_SLEEPING = new KEY("players-sleeping-percentage");

        public static KEY DURATION_DAY = new KEY(DURATIONS, "day");
        public static KEY DURATION_NIGHT = new KEY(DURATIONS, "night");

        // TODO: remove
        @Deprecated(since = "1.2.0", forRemoval = true)
        public static KEY DURATION_DAY_LEGACY = new KEY("day");
        // TODO: remove
        @Deprecated(since = "1.2.0", forRemoval = true)
        public static KEY DURATION_NIGHT_LEGACY = new KEY("night");

        public static KEY NIGHT_SKIPPING_ENABLED = new KEY(NIGHT_SKIPPING, "enabled");
        public static KEY PLAYERS_SLEEPING_PERCENTAGE_ENABLED =
                new KEY(PLAYERS_SLEEPING, "enabled");
        public static KEY PLAYERS_SLEEPING_PERCENTAGE = new KEY(PLAYERS_SLEEPING, "percentage");

        private final String fullPath;
        private final String key;

        public KEY(final String key) {
            fullPath = this.key = key;
        }

        public KEY(final KEY parent, final String key) {
            this.fullPath = parent.getFull() + "." + key;
            this.key = key;
        }

        public String getFull() {
            return fullPath;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    private final Durations durations;
    private final Boolean nightSkippingEnabled;
    private final Boolean playersSleepingPercentageEnabled;
    private final Integer playersSleepingPercentage;

    public Integer getDurationDay() {
        return durations.day;
    }

    public Integer getDurationNight() {
        return durations.night;
    }

    public Boolean getNightSkippingEnabled() {
        return nightSkippingEnabled;
    }

    public Boolean getPlayersSleepingPercentageEnabled() {
        return playersSleepingPercentageEnabled;
    }

    public Integer getPlayersSleepingPercentage() {
        return playersSleepingPercentage;
    }

    private TimeControlConfig(final Durations durations, final Boolean nightSkippingEnabled,
            final Boolean playersSleepingPercentageEnabled,
            final Integer playersSleepingPercentage) {
        this.durations = durations;
        this.nightSkippingEnabled = nightSkippingEnabled;
        this.playersSleepingPercentageEnabled = playersSleepingPercentageEnabled;
        this.playersSleepingPercentage = playersSleepingPercentage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> serialized = new HashMap<>();

        for (KEY entry : List.of(KEY.DURATIONS, KEY.NIGHT_SKIPPING, KEY.PLAYERS_SLEEPING)) {
            serialized.put(entry.toString(), new HashMap<>());
        }

        ((HashMap<String, Object>) serialized.get(KEY.DURATIONS.toString()))
                .put(KEY.DURATION_DAY.toString(), getDurationDay());
        ((HashMap<String, Object>) serialized.get(KEY.DURATIONS.toString()))
                .put(KEY.DURATION_NIGHT.toString(), getDurationNight());


        ((HashMap<String, Object>) serialized.get(KEY.NIGHT_SKIPPING.toString()))
                .put(KEY.NIGHT_SKIPPING_ENABLED.toString(), getNightSkippingEnabled());

        ((HashMap<String, Object>) serialized.get(KEY.PLAYERS_SLEEPING.toString())).put(
                KEY.PLAYERS_SLEEPING_PERCENTAGE_ENABLED.toString(),
                getPlayersSleepingPercentageEnabled());
        ((HashMap<String, Object>) serialized.get(KEY.PLAYERS_SLEEPING.toString()))
                .put(KEY.PLAYERS_SLEEPING_PERCENTAGE.toString(), getPlayersSleepingPercentage());


        return serialized;
    }

    @SuppressWarnings("unchecked")
    public static TimeControlConfig deserialize(final Map<String, Object> serialized) {
        Map<String, Object> durations = null;
        Map<String, Object> nightSkipping = null;
        Map<String, Object> playersSleeping = null;
        final Object durationsEntry = serialized.get(KEY.DURATIONS.toString());
        final Object nightSkippingEntry = serialized.get(KEY.NIGHT_SKIPPING.toString());
        final Object playersSleepingEntry = serialized.get(KEY.PLAYERS_SLEEPING.toString());

        if (durationsEntry instanceof Map) {
            durations = (Map<String, Object>) durationsEntry;
        } else if (durationsEntry instanceof MemorySection) {
            durations = ((MemorySection) durationsEntry).getValues(false);
        }

        if (nightSkippingEntry instanceof Map) {
            nightSkipping = (Map<String, Object>) nightSkippingEntry;
        } else if (nightSkippingEntry instanceof MemorySection) {
            nightSkipping = ((MemorySection) nightSkippingEntry).getValues(false);
        }

        if (playersSleepingEntry instanceof Map) {
            playersSleeping = (Map<String, Object>) playersSleepingEntry;
        } else if (playersSleepingEntry instanceof MemorySection) {
            playersSleeping = ((MemorySection) playersSleepingEntry).getValues(false);
        }

        Integer dayDuration = null;
        Integer nightDuration = null;
        Boolean nightSkippingEnabled = null;
        Boolean playersSleepingPercentageEnabled = null;
        Integer playersSleepingPercentage = null;

        if (durations != null) {
            dayDuration = (Integer) durations.get(KEY.DURATION_DAY.toString());
            nightDuration = (Integer) durations.get(KEY.DURATION_NIGHT.toString());
        }

        // TODO: remove legacy check in a future version
        if (dayDuration == null) {
            dayDuration = (Integer) serialized.get(KEY.DURATION_DAY_LEGACY.toString());
        }

        // TODO: remove legacy check in a future version
        if (nightDuration == null) {
            nightDuration = (Integer) serialized.get(KEY.DURATION_NIGHT_LEGACY.toString());
        }

        if (nightSkipping != null) {
            nightSkippingEnabled =
                    (Boolean) nightSkipping.get(KEY.NIGHT_SKIPPING_ENABLED.toString());
        }

        if (playersSleeping != null) {
            playersSleepingPercentageEnabled = (Boolean) playersSleeping
                    .get(KEY.PLAYERS_SLEEPING_PERCENTAGE_ENABLED.toString());
            playersSleepingPercentage =
                    (Integer) playersSleeping.get(KEY.PLAYERS_SLEEPING_PERCENTAGE.toString());
        }

        return new TimeControlConfig(new Durations(dayDuration, nightDuration),
                nightSkippingEnabled, playersSleepingPercentageEnabled, playersSleepingPercentage);
    }

    @SuppressWarnings("unchecked")
    public static TimeControlConfig validate(TimeControlConfig unvalidated,
            TimeControlConfig fallback) {
        if (unvalidated == null) {
            return fallback;
        }

        Map<String, Object> serialized = unvalidated.serialize();

        Integer day = unvalidated.getDurationDay();
        if (day == null || day <= 0) {
            ((HashMap<String, Object>) serialized.get(KEY.DURATIONS.toString()))
                    .put(KEY.DURATION_DAY.toString(), fallback.getDurationDay());
        }

        Integer night = unvalidated.getDurationNight();
        if (night == null || night <= 0) {
            ((HashMap<String, Object>) serialized.get(KEY.DURATIONS.toString()))
                    .put(KEY.DURATION_NIGHT.toString(), fallback.getDurationNight());
        }

        Boolean nightSkippingEnabled = unvalidated.getNightSkippingEnabled();
        if (nightSkippingEnabled == null) {
            ((HashMap<String, Object>) serialized.get(KEY.NIGHT_SKIPPING.toString()))
                    .put(KEY.NIGHT_SKIPPING_ENABLED.toString(), fallback.getNightSkippingEnabled());
        }

        Boolean playersSleepingPercentageEnabled =
                unvalidated.getPlayersSleepingPercentageEnabled();
        if (playersSleepingPercentageEnabled == null) {
            ((HashMap<String, Object>) serialized.get(KEY.PLAYERS_SLEEPING.toString())).put(
                    KEY.PLAYERS_SLEEPING_PERCENTAGE_ENABLED.toString(),
                    fallback.getPlayersSleepingPercentageEnabled());
        }

        Integer playersSleepingPercentage = unvalidated.getPlayersSleepingPercentage();
        if (playersSleepingPercentage == null) {
            ((HashMap<String, Object>) serialized.get(KEY.PLAYERS_SLEEPING.toString())).put(
                    KEY.PLAYERS_SLEEPING_PERCENTAGE.toString(),
                    fallback.getPlayersSleepingPercentage());
        }

        return TimeControlConfig.deserialize(serialized);
    }

    public static TimeControlConfig validate(TimeControlConfig unvalidated,
            TimeControlConfig... fallbacks) {
        TimeControlConfig validated = unvalidated;

        for (TimeControlConfig fallback : fallbacks) {
            validated = validate(validated, fallback);
        }

        return validated;
    }

    private static class Durations {
        public final Integer day;
        public final Integer night;

        private Durations(final Integer day, final Integer night) {
            this.day = day;
            this.night = night;
        }
    }
}
