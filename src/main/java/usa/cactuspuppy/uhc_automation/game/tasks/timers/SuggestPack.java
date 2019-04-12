package usa.cactuspuppy.uhc_automation.game.tasks.timers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.UUID;

public class SuggestPack extends TimerTask {
    private UUID player;

    public SuggestPack(GameInstance gameInstance, UUID player) {
        super(gameInstance, false, 100L, 0L);
    }

    @Override
    public void run() {
        String hash = Main.getMainConfig().get("pack.hash", "");
        String pack = Main.getMainConfig().get("pack.url", "");
        if (pack.equals("")) {
            return;
        }
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            return;
        }
        try {
            if (hash.equals("")) {
                p.setResourcePack(pack);
            } else {
                p.setResourcePack(pack, hash.getBytes());
            }
        } catch (IllegalArgumentException ignored) {
        }
    }
}
