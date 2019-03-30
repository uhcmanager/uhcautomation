package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.timers.UHC_InitCountdown;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Start extends UHCCommand implements TabCompleter {
    private static Set<Long> starters = new HashSet<>();

    public static void startComplete(GameInstance instance) {

    }

    @Override
    public String getUsage() {
        return "/uhc start [secs] OR /uhc start <secs> <name/ID>";
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        GameInstance instance = null;
        if (!(commandSender instanceof Player)) {
            if (args.length < 2) {
                return false;
            }
            if (args[1].matches("-?[0-9]+")) { // Find ID
                try {
                    instance = GameManager.getGame(Long.valueOf(args[0]));
                } catch (NumberFormatException ignored) { }
            }
            if (instance == null) { // Found no instance with ID check
                String[] nameArr = new String[args.length - 1];
                System.arraycopy(args, 1, nameArr, 0, args.length - 1);
                String gameName = String.join(" ", nameArr);
                List<GameInstance> instances = new LinkedList<>();
                for (GameInstance g : GameManager.getActiveGames().values()) {
                    if (g.getName().equalsIgnoreCase(gameName)) {
                        instances.add(g);
                    }
                }
                switch (instances.size()) {
                    case 0 -> {
                        commandSender.sendMessage(ChatColor.RED + "No game found with that name or ID.");
                        return true;
                    }
                    case 1 -> instance = instances.get(0);
                    default -> {
                        commandSender.sendMessage(ChatColor.YELLOW + "Multiple games found with that name! Select one via the associated ID:");
                        StringBuilder builder = new StringBuilder();
                        builder.append("\n");
                        for (GameInstance g : instances) {
                            builder.append(String.format("GID: %d | Main World: %s | Players: %d | Spectators: %d",
                                    g.getGameID(), (g.getMainWorld() == null
                                            ? "NULL"
                                            : g.getMainWorld().getName()),
                                    g.getAlivePlayers().size(), g.getSpectators().size()));
                            builder.append("\n");
                        }
                        commandSender.sendMessage(builder.toString());
                        return true;
                    }
                }
            }
        } else {
            instance = GameManager.getPlayerGame(((Player) commandSender).getUniqueId());
        }
        if (instance == null) {
            commandSender.sendMessage(ChatColor.RED + "Must be in a game or specify a game name/ID.");
            return true;
        }
        if (args.length > 0) {
            try {
                int delay = Integer.valueOf(args[0]);
                new UHC_InitCountdown(instance, delay);
                starters.add(instance.getGameID());
                return true;
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Unknown integer: " + args[0]);
                return true;
            }
        } else {
            instance.updateState(GameStateEvent.INIT);
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
