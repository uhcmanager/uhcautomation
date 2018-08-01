package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CommandSetWorld implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            return false;
        }
        if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "Game is currently active! Stop the game first before changing the game world.");
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Must be a player to use command this way!");
                return true;
            }
            World world = ((Player) sender).getWorld();
            Main.getInstance().getGameInstance().setWorld(world);
        }
        String worldName = args[0];
        if (worldName.endsWith("_nether") || worldName.endsWith("_the_end")) {
            sender.sendMessage(ChatColor.RED + "You cannot set the UHC world to be the nether or the end!");
            return true;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Could not find world " + ChatColor.WHITE + worldName + ChatColor.RED + ".");
            return true;
        }
        Main.getInstance().getGameInstance().setWorld(world);
        sender.sendMessage(ChatColor.GREEN + "Successfully set game world to " + ChatColor.WHITE + worldName + ChatColor.GREEN + " for event " + Main.getInstance().getConfig().getString("event-name"));
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
