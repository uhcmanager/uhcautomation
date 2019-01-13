package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

@org.bukkit.plugin.java.annotation.command.Command(name = "surface")
public class Surface extends UHCCommand implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return true;
    }
}
