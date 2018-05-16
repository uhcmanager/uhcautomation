package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSetWorld implements CommandExecutor {
    private Main m;
    public CommandSetWorld(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        String world = args[0];
        if (world.endsWith("_nether") || world.endsWith("_the_end")) {
            sender.sendMessage(ChatColor.RED + "You cannot set the UHC world to be the nether or the end!");
            return true;
        }
        m.gi.setGameWorld(args[0]);
        return true;
    }
}
