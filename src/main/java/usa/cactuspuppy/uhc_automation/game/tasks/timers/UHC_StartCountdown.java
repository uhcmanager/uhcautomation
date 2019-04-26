package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.util.Objects;

public class UHC_StartCountdown extends TimerTask {
    /**
     * Whether or not this timer has started counting down
     */
    private boolean countdown;
    /**
     * At last run, the time of the server tick
     */
    private long lastTick;
    /**
     * At last run, the seconds displayed on the timer
     */
    private long lastSecs;
    /**
     * Seconds to wait from landing and tick stabilization to
     */
    private int secsToCountdown;
    /**
     * Calculated time at which game should start
     */
    private long startTime = -1;

    @Getter
    @Setter
    private int ticksPerCycle = 1;
    @Getter
    @Setter
    private double maxTickDeviance = 0.1;

    /**
     * Full constructor for start-game countdown
     *
     * @param gameInstance    game to countdown for
     * @param delay           Seconds to countdown after all players land and ticks stabilize
     * @param ticksPerCycle   Number of ticks between each run of this task.
     *                        Higher values increase precision, but may introduce more load onto the server
     * @param maxTickDeviance Maximum allowable variation from 20 ticks per second as a proportion of tick time.
     *                        Higher values may be needed on servers that do not achieve 20 TPS, but this may result in
     *                        the countdown starting before teleport lag has cleared.
     */
    public UHC_StartCountdown(GameInstance gameInstance, int delay, int ticksPerCycle, double maxTickDeviance) {
        super(gameInstance, true, 0L, ticksPerCycle);
        countdown = false;
        getGameInstance().getUtils().log(Logger.Level.INFO, this.getClass(), "Initiating final game start countdown...");
        this.maxTickDeviance = maxTickDeviance;
        this.ticksPerCycle = ticksPerCycle;
        lastTick = System.currentTimeMillis();
        secsToCountdown = delay;
    }

    /**
     * Overloaded constructor
     */
    public UHC_StartCountdown(GameInstance gameInstance, int delay) {
        super(gameInstance, true, 0L, 1L);
        countdown = false;
        getGameInstance().getUtils().log(Logger.Level.INFO, this.getClass(), "Initiating final game start countdown...");
        lastTick = System.currentTimeMillis();
        secsToCountdown = delay;
    }

    @Override
    public void run() {
        long currTick = System.currentTimeMillis();
        if (!countdown) { //Wait for ticks to stabilize + players to land
            //Check current tick delay
            if (Math.abs(currTick - lastTick - ticksPerCycle * 50) > maxTickDeviance * ticksPerCycle * 50) {
                getGameInstance().getUtils().log(Logger.Level.FINE, this.getClass(),
                        String.format("Current run: %d | Last run: %d (%d ticks ago) | Avg Tick Time: %d (need %f)",
                                currTick, lastTick, ticksPerCycle, (currTick - lastTick) / ticksPerCycle, maxTickDeviance * 50));
                lastTick = currTick;
                getGameInstance().getUtils().broadcastTitle(ChatColor.GOLD + "Initiating Match", "Loading chunks...", 0, 20, 10);
            } else {
                //Check that all players are on the ground
                boolean allOnGround = getGameInstance().getAlivePlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).allMatch(Player::isOnGround);
                if (allOnGround) {
                    countdown = true;
                } else {
                    getGameInstance().getUtils().broadcastTitle(ChatColor.GOLD + "Initiating Match", "Waiting for all players to land...", 0, 20, 10);
                }
            }
        }
        if (!countdown) {
            return;
        }

        //Show countdown
        String actionBar;
        String title;
        String subtitle = ChatColor.GREEN + "Game starts in...";
        if (startTime == -1) {//Set time to start
            startTime = System.currentTimeMillis() + secsToCountdown * 1000;
        }
        long timeTo = startTime - currTick;
        long secs = timeTo / 1000 + (timeTo % 1000 == 0 ? 0 : 1);
        if (timeTo <= 0) {
            cancel();
            getGameInstance().updateState(GameStateEvent.START);
            return;
        }
        actionBar = String.format("Game starts in %.2f", timeTo / 1000D);
        title = String.format("Game starts in %d", secs);
        if (lastSecs != secs) {
            getGameInstance().getUtils().broadcastSoundTitle(Sound.BLOCK_NOTE_BLOCK_PLING, 0.594604F, title, subtitle, 0, 20, 10);
        }
        getGameInstance().getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.spigot().sendMessage(
                ChatMessageType.ACTION_BAR, new TextComponent(actionBar)
        ));
        lastSecs = secs;
    }
}
