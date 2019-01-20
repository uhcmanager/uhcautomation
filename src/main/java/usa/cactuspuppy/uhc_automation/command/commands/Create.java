package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class Create extends UHCCommand {

    @Override
    public String getUsage() {
        return "/uhc create <type> [world]";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //TODO: Implement game creation
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
