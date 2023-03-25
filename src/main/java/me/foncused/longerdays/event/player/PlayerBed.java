package me.foncused.longerdays.event.player;

import me.foncused.longerdays.config.ConfigManager;
import me.foncused.longerdays.util.LongerDaysUtil;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBed implements Listener {

    private final ConfigManager configManager;

    public PlayerBed(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
        if (!configManager.isNightSkippingEnabled()) {
            return;
        }

        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        final World world = event.getPlayer().getWorld();
        final int sleeping = (int) world.getPlayers().stream().filter((player) -> player.isSleeping()).count() + 1;

        int percentage;

        if (!configManager.isPercentageEnabled()) {
            try {
                percentage = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            } catch (Exception e) {
                LongerDaysUtil.consoleWarning("Could not fetch game-rule value 'playersSleepingPercentage!" +
                        " Please go to the config.yml and enable players-sleeping-percentage");
                return;
            }
        }

        percentage = configManager.getPercentage();

        if ((sleeping / world.getPlayers().size()) * 100 >= percentage) {
            world.setTime(1000);
            event.setCancelled(true);
            LongerDaysUtil.console("The night has been skipped by sleeping");
        }
    }

}
