package net.pupskuchen.timecontrol.nightskipping;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigHandler;
import net.pupskuchen.timecontrol.util.TCLogger;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class NightSkipper {
    /**
     * While the online documentation states that players sleep in a bed for 101 ticks, they seem to
     * actually get kicked out after 100 ticks already. Close enough …
     * https://minecraft.fandom.com/wiki/Bed#Sleeping
     */
    public static final int SKIPPABLE_SLEEP_TICKS = 100;
    private static final int SKIP_PERCENTAGE_FALLBACK = 100;

    private final World world;
    private boolean warnIfPercentageUnconfigured = true;
    private final ConfigHandler config;
    private final TCLogger logger;
    private final NightSkipScheduler skipScheduler;

    /**
     * In MC 1.13, when all players go to bed, the last player won't have to sleep as long for the
     * night to be skipped. Therefore, we'll allow skipping right when the last person goes to bed -
     * but only if *everyone* is in bed and only for 1.13.
     */
    private final boolean allowAllAsleepInstantSkip;

    public NightSkipper(final TimeControl plugin, final World world) {
        this.world = world;
        this.logger = plugin.getTCLogger();
        this.config = plugin.getConfigHandler();
        this.skipScheduler = createSkipScheduler(plugin, world);

        this.allowAllAsleepInstantSkip = plugin.getServer().getBukkitVersion().startsWith("1.13");
    }

    public void scheduleSkip() {
        if (allowAllAsleepInstantSkip && getSleepingRelevantPlayers().size() > 1
                && getSleepingPercentage(false, 1) >= 100) {
            skipNight(1);
            return;
        }

        if (skipScheduler == null || skipThresholdMet(false, 0)) {
            return;
        }

        skipScheduler.scheduleSkip();
        logger.debug("Scheduled night skip for world \"%s\".", world.getName());
    }

    private void cancelScheduledSkip() {
        if (skipScheduler != null) {
            skipScheduler.cancel();
        }
    }

    private NightSkipScheduler createSkipScheduler(final TimeControl plugin, final World world) {
        if (!world.isGameRule("playersSleepingPercentage")) {
            // In older versions, players aren't going to leave their beds by themselves,
            // so we have to manually schedule the skip.
            return new NightSkipScheduler(plugin, this, SKIPPABLE_SLEEP_TICKS + 1);
        }

        return null;
    }

    private int getSkipPercentage(final boolean warnIfUnconfigured) {
        if (!config.isPercentageEnabled(world)) {
            try {
                return world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (NoSuchFieldError e) {
                if (warnIfUnconfigured) {
                    logger.warn(
                            "Failed to read game rule \"playersSleepingPercentage\" for world \"%s\"!",
                            world.getName());
                    logger.warn("Please enable players-sleeping-percentage in the plugin config.");
                    logger.warn("Using fallback percentage of %d %%.", SKIP_PERCENTAGE_FALLBACK);
                }

                return SKIP_PERCENTAGE_FALLBACK;
            }
        }

        return config.getConfigPercentage(world);
    }

    private List<Player> getSleepingRelevantPlayers() {
        return world.getPlayers().stream().filter(player -> !player.isSleepingIgnored())
                .collect(Collectors.toList());
    }

    private float getSleepingPercentage(final boolean onlyFullyRested,
            final int sleepingCountOffset) {
        final int sleepTickThreshold = onlyFullyRested ? SKIPPABLE_SLEEP_TICKS : 1;
        final List<Player> players = getSleepingRelevantPlayers();
        final int sleeping = (int) players.stream()
                .filter((player) -> player.getSleepTicks() >= sleepTickThreshold).count()
                + sleepingCountOffset;

        return ((float) sleeping / players.size()) * 100;
    }

    private boolean skipThresholdMet(final boolean onlyFullyRested, final int sleepingCountOffset) {
        final int skipPercentage = getSkipPercentage(warnIfPercentageUnconfigured);
        warnIfPercentageUnconfigured = false;

        if (skipPercentage <= 0) {
            return true;
        } else if (skipPercentage > 100) {
            return false;
        }

        return getSleepingPercentage(onlyFullyRested, sleepingCountOffset) >= skipPercentage;
    }

    public void skipNight() {
        skipNight(0);
    }

    private void skipNight(final int sleepingCountOffset) {
        if (!TimeUtil.sleepAllowed(world)) {
            return;
        }

        if (!skipThresholdMet(!allowAllAsleepInstantSkip, sleepingCountOffset)) {
            if (!skipThresholdMet(false, sleepingCountOffset)) {
                cancelScheduledSkip();
            }

            return;
        }

        final int wakeTime = TimeUtil.getWakeTime(world);

        world.setTime(wakeTime);
        logger.info("Skipped the night on world \"%s\".", world.getName());
        logger.debug("Set time to %d on world \"%s\".", wakeTime, world.getName());
    }
}
