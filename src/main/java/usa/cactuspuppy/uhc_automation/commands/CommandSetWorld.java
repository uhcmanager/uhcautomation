package usa.cactuspuppy.uhc_automation.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.List;
import java.util.stream.Collectors;

public class CommandSetWorld extends UHCCommand {
    public CommandSetWorld() {
        name = "setworld";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /uhc setworld [world name]");
        }
        if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "Game is currently active! Stop the game first before changing the game world.");
            return;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Must be a player to have plugin infer world!");
                return;
            }
            World world = ((Player) sender).getWorld();
            Main.getInstance().getGameInstance().setWorld(world);
            return;
        }
        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "Could not find world " + ChatColor.WHITE + worldName + ChatColor.RED + ".");
            return;
        }
        if ((world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END)) && !Main.getInstance().getConfig().getBoolean("nether-end-exclusion", true)) {
            sender.sendMessage(ChatColor.RED + "Cannot set the game world to be the nether or the end!");
            return;
        }
        Main.getInstance().getGameInstance().setWorld(world);
        sender.sendMessage(ChatColor.GREEN + "Successfully set game world to " + ChatColor.WHITE + worldName + ChatColor.GREEN + " for event " + Main.getInstance().getConfig().getString("event-name"));
    }

    public static List<String> onTabComplete(String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream().filter(CommandSetWorld::filterWorld).map(World::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }

    private static boolean filterWorld(World world) {
        return world.getEnvironment().equals(World.Environment.NORMAL) || !Main.getInstance().getConfig().getBoolean("nether-end-exclusion", true);
    }
}
