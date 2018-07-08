package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandHelp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (commandSender instanceof Player) {
            UHCUtils.sendPlayerGithubWiki((Player) commandSender);
        } else {
            commandSender.sendMessage("Go to https://github.com/CactusPuppy/uhcautomation/wiki for help.");
        }
        return true;
    }
}
