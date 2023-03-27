package net.pupskuchen.timecontrol.nightskipping;

import java.util.List;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.pupskuchen.timecontrol.config.ConfigManager;
import net.pupskuchen.timecontrol.util.LogUtil;
import net.pupskuchen.timecontrol.util.TimeUtil;

public class NightSkipper {
    private static final int SKIP_PERCENTAGE_FALLBACK = 100;

    private final World world;
    private final ConfigManager configManager;
    private final NightSkipGuard skipGuard;

    public NightSkipper(final JavaPlugin plugin, final ConfigManager configManager, final World world) {
        this.world = world;
        this.configManager = configManager;
        this.skipGuard = new NightSkipGuard(plugin);
    }

    public void restartGuard() {
        if (!this.skipThresholdMet()) {
            skipGuard.makeSkippable();
        }
    }

    public void cancelGuard() {
        skipGuard.cancel();
    }

    public int getSkipPercentage() {
        if (!configManager.isPercentageEnabled()) {
            try {
                return world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (Exception e) {
                LogUtil.consoleWarning("Could not fetch game-rule value 'playersSleepingPercentage!" +
                        " Please enable players-sleeping-percentage in the plugin configuration.");
                LogUtil
                        .consoleWarning(String.format("Using fallback percentage of %d %%", SKIP_PERCENTAGE_FALLBACK));

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
        LogUtil.console("The night has been skipped by sleeping");
    }
}
