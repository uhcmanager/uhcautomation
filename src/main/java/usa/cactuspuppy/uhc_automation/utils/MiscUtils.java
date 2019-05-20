package usa.cactuspuppy.uhc_automation.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;

import java.util.*;

public final class MiscUtils {
    /**
     * Teleport specified player(s) to the specified location while maintaining the player's orientation
     *
     * @param players     Player(s) to teleport relatively
     * @param destination Location to teleport to
     */
    public static void relativeTeleport(Location destination, Player... players) {
        for (Player p : players) {
            destination.setYaw(p.getLocation().getYaw());
            destination.setPitch(p.getLocation().getPitch());
            p.teleport(destination);
        }
    }

    public static Map<String, Integer> secsToHMS(long secs) {
        Map<String, Integer> hms = new HashMap<>();

        hms.put("hrs", (int) (secs / 3600));
        hms.put("mins", (int) ((secs / 60) % 60));
        hms.put("secs", (int) (secs % 60));

        return hms;
    }


    public static String secsToFormatString(long secs) {
        Map<String, Integer> hms = secsToHMS(secs);
        return hmsToFormatString(hms.get("hrs"), hms.get("mins"), hms.get("secs"));
    }

    public static String hmsToFormatString(int hrs, int mins, int secs) {
        assert (hrs >= 0) && (mins >= 0) && (secs >= 0);

        if (hrs == 0 && mins == 0 && secs == 0) {
            return "0 seconds";
        }

        StringJoiner fmtStng = new StringJoiner(" ");
        if (hrs == 1) {
            fmtStng.add(hrs + " hour");
        } else if (hrs != 0) {
            fmtStng.add(hrs + " hours");
        }

        if (mins == 1) {
            fmtStng.add(mins + " minute");
        } else if (mins != 0) {
            fmtStng.add(mins + " minutes");
        }

        if (secs == 1) {
            fmtStng.add(secs + " second");
        } else if (secs != 0) {
            fmtStng.add(secs + " seconds");
        }

        return fmtStng.toString();
    }

    public static String secsToFormatString2(int secs) {
        Map<String, Integer> hms = secsToHMS(secs);
        return hmsToFormatString2(hms.get("hrs"), hms.get("mins"), hms.get("secs"));
    }

    public static String hmsToFormatString2(int hrs, int mins, int secs) {
        assert hrs >= 0 && mins >= 0 && secs >= 0;

        return hrs + ":" + mins + "'" + secs + "\"";
    }

    @Getter
    public static class GetInstanceResult {
        private boolean usageCorrect = false;
        private GameInstance instance = null;

        private GetInstanceResult setUsageCorrect(boolean b) {
            usageCorrect = b;
            return this;
        }

        private GetInstanceResult setInstance(GameInstance instance) {
            this.instance = instance;
            return this;
        }
    }

    @NotNull
    public static GetInstanceResult getGameInstance(CommandSender commandSender, String[] args) {
        GameInstance instance = null;

        Logger.logFineMsg(MiscUtils.class, "Trying to find game with args" + Arrays.toString(args), 0);

        if (args.length < 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "Must be a player to infer game.");
                return new GetInstanceResult().setUsageCorrect(false);
            }
            instance = GameManager.getPlayerGame(((Player) commandSender).getUniqueId());
        } else {
            if (args.length == 1 && args[0].matches("-?[0-9]+")) { // Find ID
                try {
                    instance = GameManager.getGame(UUID.fromString(args[0]));
                } catch (NumberFormatException ignored) { }
            }
            if (instance == null) {// Found no instance with ID check
                //TODO: Fix finding game code
                String gameName = String.join(" ", args);
                Logger.logFineMsg(MiscUtils.class, "Finding game with name " + gameName, 0);
                List<GameInstance> instances = new LinkedList<>();
                for (GameInstance g : GameManager.getActiveGames().values()) {
                    if (g.getName().equalsIgnoreCase(gameName)) {
                        instances.add(g);
                    }
                }
                switch (instances.size()) {
                    case 0 -> {
                        commandSender.sendMessage(ChatColor.RED + "No game found with that name or ID.");
                        return new GetInstanceResult().setUsageCorrect(true);
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
                        return new GetInstanceResult().setUsageCorrect(true);
                    }
                }
            }
        }

        //Check that we actually found a game instance
        if (instance == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find that game instance!");
            return new GetInstanceResult().setUsageCorrect(true);
        }
        return new GetInstanceResult().setInstance(instance).setUsageCorrect(true);
    }
}
