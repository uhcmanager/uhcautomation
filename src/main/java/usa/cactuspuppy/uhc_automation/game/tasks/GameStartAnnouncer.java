package usa.cactuspuppy.uhc_automation.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GameStartAnnouncer extends Task {
    private List<String> startMsgs = new ArrayList<>();

    public GameStartAnnouncer(GameInstance gameInstance) {
        super(gameInstance);
        readStartMsgs();
    }

    private void readStartMsgs() {
        InputStream stream = Main.getInstance().getResource("");
        if (stream == null) {
            getGameInstance().getUtils().log(Logger.Level.WARNING, this.getClass(), "No start messages found, defaulting to no start msg");
            startMsgs.add("");
            return;
        }
        Scanner scan = new Scanner(stream);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.startsWith("#") || line.isBlank()) {
                continue;
            }
            startMsgs.add(line);
        }
    }

    /**
     * Execute everything that must be done when initiating this task
     *
     * @return Whether the task was successfully initiated.
     */
    @Override
    public boolean init() {
        Random rng = new Random();
        getGameInstance().getUtils().broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1F);
        getGameInstance().getUtils().broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.17F);
        Set<Player> players = getGameInstance().getAlivePlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : players) {
            String randomLine = startMsgs.get(rng.nextInt(startMsgs.size()));
            p.sendTitle(ChatColor.GREEN + "GO!", randomLine, 0, 80, 40);
        }
        Set<Player> spectators = getGameInstance().getSpectators().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet());
        for (Player p : spectators) {
            p.sendTitle(ChatColor.GREEN + "START!", ChatColor.ITALIC + "You are now spectating", 0, 60, 10);
        }
        return true;
    }

    /**
     * Execute everything that must be done when halting this task
     */
    @Override
    public void cancel() {

    }
}
