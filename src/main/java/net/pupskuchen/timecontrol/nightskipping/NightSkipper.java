package net.pupskuchen.timecontrol.nightskipping;

import java.util.List;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import net.pupskuchen.timecontrol.TimeControl;
import net.pupskuchen.timecontrol.config.ConfigManager;
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
    private final ConfigManager configManager;
    private final TCLogger logger;

    public NightSkipper(final TimeControl plugin, final World world) {
        this.world = world;
        this.configManager = plugin.getConfigManager();
        this.logger = plugin.getTCLogger();
    }

    private int getSkipPercentage() {
        if (!configManager.isPercentageEnabled(world)) {
            try {
                return world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (NoSuchFieldError e) {
                logger.warn("Could not fetch game-rule value 'playersSleepingPercentage!"
                        + " Please enable players-sleeping-percentage in the plugin configuration.");
                logger.warn("Using fallback percentage of %d %%.", SKIP_PERCENTAGE_FALLBACK);

                return SKIP_PERCENTAGE_FALLBACK;
            }
        }

        return configManager.getConfigPercentage(world);
    }

    private boolean skipThresholdMet() {
        final int skipPercentage = getSkipPercentage();

        if (skipPercentage <= 0) {
            return true;
        }

        final List<Player> players = world.getPlayers();
        final int sleeping = (int) players.stream()
                .filter((player) -> player.getSleepTicks() >= SKIPPABLE_SLEEP_TICKS).count();
        final float sleepingPercentage = ((float) sleeping / players.size()) * 100;

        return sleepingPercentage >= skipPercentage;
    }

    private boolean shouldSkipNight() {
        if (!TimeUtil.sleepAllowed(world)) {
            return false;
        }

        final boolean thresholdMet = skipThresholdMet();

        return thresholdMet;
    }

    public void skipNight() {
        if (!shouldSkipNight()) {
            return;
        }

        final int wakeTime = TimeUtil.getWakeTime(world);

        world.setTime(wakeTime);
        logger.info("Skipped the night on world \"%s\".", world.getName());
        logger.debug("Set time to %d on world \"%s\".", wakeTime, world.getName());
    }
}
