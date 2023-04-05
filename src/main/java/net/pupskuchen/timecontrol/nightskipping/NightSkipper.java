package net.pupskuchen.timecontrol.nightskipping;

import java.util.List;
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
    private static final int SKIPPABLE_SLEEP_TICKS = 100;
    private static final int SKIP_PERCENTAGE_FALLBACK = 100;

    private final World world;
    private final int skipPercentage;
    private final TCLogger logger;
    private final NightSkipScheduler skipScheduler;

    public NightSkipper(final TimeControl plugin, final World world) {
        this.world = world;
        this.logger = plugin.getTCLogger();
        this.skipPercentage = getSkipPercentage(plugin.getConfigHandler());
        this.skipScheduler = createSkipScheduler(plugin, world);
    }

    public void scheduleSkip() {
        if (skipScheduler == null || skipThresholdMet(false)) {
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

    private int getSkipPercentage(final ConfigHandler config) {
        if (!config.isPercentageEnabled(world)) {
            try {
                return world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (NoSuchFieldError e) {
                logger.warn("Failed to read game rule 'playersSleepingPercentage!"
                        + " Please enable players-sleeping-percentage in the plugin configuration.");
                logger.warn("Using fallback percentage of %d %%.", SKIP_PERCENTAGE_FALLBACK);

                return SKIP_PERCENTAGE_FALLBACK;
            }
        }

        return config.getConfigPercentage(world);
    }

    private boolean skipThresholdMet(final boolean onlyFullyRested) {
        if (skipPercentage <= 0) {
            return true;
        } else if (skipPercentage > 100) {
            return false;
        }

        final int sleepTickThreshold = onlyFullyRested ? SKIPPABLE_SLEEP_TICKS : 1;
        final List<Player> players = world.getPlayers();
        final int sleeping = (int) players.stream()
                .filter((player) -> player.getSleepTicks() >= sleepTickThreshold).count();
        final float sleepingPercentage = ((float) sleeping / players.size()) * 100;

        return sleepingPercentage >= skipPercentage;
    }

    public void skipNight() {
        if (!TimeUtil.sleepAllowed(world)) {
            return;
        }

        if (!skipThresholdMet(true)) {
            if (!skipThresholdMet(false)) {
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
