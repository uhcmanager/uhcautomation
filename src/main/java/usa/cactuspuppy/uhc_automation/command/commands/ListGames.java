package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;

import java.util.ArrayList;

public class ListGames implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc listgames";
    }

    @Override
    public String getPurpose() {
        return "Lists all registered game instances";
    }

    @Override
    public String getMoreInfo() {
        return "Displays information on all the currently active games.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        StringBuilder finalMsg = new StringBuilder();
        finalMsg.append(ChatColor.GOLD).append("Currently active games:").append("\n").append(ChatColor.RESET);

        boolean altColor = false;
        if (GameManager.getActiveGames().isEmpty()) {
            finalMsg.append(ChatColor.RED).append("NONE");
        }
        for (GameInstance g : GameManager.getActiveGames().values()) {
            if (altColor) {
                finalMsg.append(ChatColor.AQUA);
            } else {
                finalMsg.append(ChatColor.GREEN);
            }
            altColor = !altColor;
            finalMsg.append(
                    String.format("GID: %s | Name: %s | Main World: %s | Status: %s | Players: %d | Spectators: %d",
                            g.getGameID().toString(), g.getName(),
                            (g.getMainWorld() == null ? "NULL" : g.getMainWorld().getName()),
                            g.getGameState().toString(),
                            g.getAlivePlayers().size(), g.getSpectators().size())
            ).append("\n").append(ChatColor.RESET);
        }

        commandSender.sendMessage(finalMsg.toString());
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public java.util.@Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
