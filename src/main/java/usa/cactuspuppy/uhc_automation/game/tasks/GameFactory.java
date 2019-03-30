package usa.cactuspuppy.uhc_automation.game.tasks;

import org.bukkit.World;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.PVN;
import usa.cactuspuppy.uhc_automation.game.types.UHC;

public class GameFactory {
    public GameInstance getGame(String type, World world) {
        return switch (type) {
            case "uhc" -> new UHC(world);
            case "pvn" -> new PVN(world);
            default -> null;
        };
    }
}
