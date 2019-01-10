package usa.cactuspuppy.uhc_automation.game.types;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.game.types.PvN;

import java.util.logging.Level;
import java.util.logging.Logger;


public enum GameType {
    PvN (PvN.class),
    UHC (UHC.class);

    @Getter Class<GameInstance> gameInstanceClass;

    GameType(Class c) {
        if (c.getSuperclass().equals(GameInstance.class)) {
            gameInstanceClass = c;
        }
    }

    public static GameType parse(String s) {
        for (GameType g : GameType.class.getEnumConstants()) {
            if (g.name().equalsIgnoreCase(s)) return g;
        }
        return null;
    }
}
