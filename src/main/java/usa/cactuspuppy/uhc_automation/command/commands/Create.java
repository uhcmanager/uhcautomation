package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.GameFactory;

import java.util.ArrayList;
import java.util.List;

public class Create implements UHCCommand {

    @Override
    public String getUsage() {
        return "/uhc create <type> [world]";
    }

    @Override
    public String getPurpose() {
        return "Creates a new game instance";
    }

    @Override
    public String getMoreInfo() {
        return "Creates a new game instance, setting the lobby world to be the world specified or the world of the command executor. Will fail if world is already the lobby of another game.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        String type = args[0];
        String[] args1 = new String[args.length - 1];
        System.arraycopy(args, 1, args1, 0, args.length - 1);
        World world = null;
        if (args1.length >= 1) {
            String worldName = String.join(" ", args1);
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                commandSender.sendMessage(ChatColor.RED + "No world found with name " + ChatColor.RESET + worldName);
                return true;
            }
        }
        if (commandSender instanceof Player && args1.length < 1) {
            Player p = (Player) commandSender;
            world = p.getWorld();
        } else if (args1.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Must be a player to infer game world.");
            return true;
        }
        //Check world is overworld
        if (!world.getEnvironment().equals(World.Environment.NORMAL)) {
            commandSender.sendMessage(ChatColor.RED + "May only set overworld worlds as main world (for now)");
            return true;
        }
        //Create correct type and register
        GameInstance instance = new GameFactory().getGame(type.toLowerCase(), world);
        GameManager.registerGame(instance);
        instance.updateState(GameStateEvent.RESET);
        World nether = Bukkit.getWorld(world.getName() + "_nether");
        World theEnd = Bukkit.getWorld(world.getName() + "_the_end");
        if (nether != null) {
            instance.addOtherWorld(nether);
        }
        if (theEnd != null) {
            instance.addOtherWorld(theEnd);
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
