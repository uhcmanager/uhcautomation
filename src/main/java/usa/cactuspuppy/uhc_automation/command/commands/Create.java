package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.game.tasks.GameFactory;

public class Create extends UHCCommand {

    @Override
    public String getUsage() {
        return "/uhc create <type> [world]";
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
        //Create correct type and register
        GameInstance instance = new GameFactory().getGame(type.toLowerCase(), world);
        GameManager.registerGame(instance);
        instance.updateState(GameStateEvent.RESET);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }
}
