package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.utils.MojangAPIHook;

import java.util.List;
import java.util.UUID;

public class Leave implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc leave <player>";
    }

    @Override
    public String getPurpose() {
        return "Removes a player from a game";
    }

    @Override
    public String getMoreInfo() {
        return "Removes a player from all games they are in.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String name = args[0];
        UUID u = MojangAPIHook.getUUID(name);
        GameInstance game = GameManager.getPlayerGame(u);
        if (game == null) {
            commandSender.sendMessage(ChatColor.YELLOW + "The player specified is not registered to a game.");
            return true;
        }
        game.removePlayer(u);
        game.addSpectator(u);
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
