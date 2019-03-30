package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.utils.MojangAPIHook;

import java.util.UUID;

public class Leave extends UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc leave <player>";
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
}
