package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.List;
import java.util.stream.Collectors;

public class CommandSetWorld implements CommandExecutor, TabCompleter {
    private Main m;
    public CommandSetWorld(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream().filter(world -> world.getEnvironment() == World.Environment.NORMAL).map(World::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }
}
