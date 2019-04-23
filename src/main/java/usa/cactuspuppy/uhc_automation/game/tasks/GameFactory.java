package usa.cactuspuppy.uhc_automation.game.tasks;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.types.PVN;
import usa.cactuspuppy.uhc_automation.game.types.UHC;
import usa.cactuspuppy.uhc_automation.utils.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameFactory {
    private static Map<String, Class<? extends GameInstance>> gameTypeMap = new HashMap<>();
    static {
        addGameType("uhc", UHC.class);
        addGameType("pvn", PVN.class);
    }

    public static void addGameType(String name, Class<? extends GameInstance> type) {
        gameTypeMap.put(name, type);
    }

    public GameInstance getGame(CommandSender commandSender, String type, World world) {
        Class<? extends GameInstance> gameType = gameTypeMap.get(type);
        if (gameType == null) {
            commandSender.sendMessage(ChatColor.RED + "Unrecognized game type.");
            return null;
        }
        Constructor constructor;
        try {
            constructor = gameType.getConstructor(World.class);
        } catch (NoSuchMethodException e) {
            commandSender.sendMessage(ChatColor.RED + "Problem creating game instance");
            Logger.logSevere(this.getClass(), "Game type " + type + " does not possess default constructor!", e);
            return null;
        }
        GameInstance instance = null;
        try {
            instance = (GameInstance) constructor.newInstance(world);
        } catch (ClassCastException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            commandSender.sendMessage(ChatColor.RED + "Problem creating game instance");
            Logger.logSevere(this.getClass(), "Problem initiating game type " + type, e);
        }
        return instance;
    }

    public Set<Class<? extends GameInstance>> getGameTypes() {
        //TODO:
    }
}
