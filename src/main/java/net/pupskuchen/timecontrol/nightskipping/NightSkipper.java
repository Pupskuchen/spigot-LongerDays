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
    private static final int SKIP_PERCENTAGE_FALLBACK = 100;

    private final World world;
    private final ConfigManager configManager;
    private final NightSkipGuard skipGuard;
    private final TCLogger logger;

    public NightSkipper(final TimeControl plugin, final World world) {
        this.world = world;
        this.configManager = plugin.getConfigManager();
        this.skipGuard = new NightSkipGuard(plugin);
        this.logger = plugin.getTCLogger();
    }

    public void restartGuard() {
        if (!this.skipThresholdMet()) {
            skipGuard.makeSkippable();
        }
    }

    private int getSkipPercentage() {
        if (!configManager.isPercentageEnabled()) {
            try {
                return world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (NoSuchFieldError e) {
                logger.warn("Could not fetch game-rule value 'playersSleepingPercentage!"
                        + " Please enable players-sleeping-percentage in the plugin configuration.");
                logger.warn("Using fallback percentage of %d %%", SKIP_PERCENTAGE_FALLBACK);

                return SKIP_PERCENTAGE_FALLBACK;
            }
        }

        return configManager.getConfigPercentage();
    }

    private boolean skipThresholdMet() {
        final int skipPercentage = getSkipPercentage();
        final List<Player> players = world.getPlayers();
        final int sleeping = (int) players.stream().filter((player) -> player.isSleeping()).count();
        final float sleepingPercentage = ((float) sleeping / players.size()) * 100;

        return sleepingPercentage >= skipPercentage;
    }

    private boolean shouldSkipNight() {
        if (!skipGuard.isSkippable() || !TimeUtil.sleepAllowed(world)) {
            return false;
        }

        final boolean thresholdMet = skipThresholdMet();

        if (!thresholdMet) {
            skipGuard.cancel();
        }

        return thresholdMet;
    }

    public void skipNight() {
        if (!shouldSkipNight()) {
            return;
        }

        final int wakeTime = TimeUtil.getWakeTime(world);

        world.setTime(wakeTime);
        logger.info("The night has been skipped by sleeping");
    }
}
