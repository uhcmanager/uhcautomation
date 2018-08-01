package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.Tasks.PreGameCountdown;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!Main.getInstance().getGameInstance().validate(sender)) {
            UHCUtils.broadcastMessage(Main.getInstance().getGameInstance(), ChatColor.RED + "Could not start UHC, settings invalid.");
            return true;
        } else if (Main.getInstance().getGameInstance().isActive()) {
            sender.sendMessage(ChatColor.RED + "A game is already in progress! Use " + ChatColor.RESET + "/uhcreset " + ChatColor.RED + "to stop the game first!");
            return true;
        }
        if (PreGameCountdown.instanced) {
            if (args.length == 0) {
                PreGameCountdown.getInstance().instaStart();
            } else if (args.length == 1) {
                Bukkit.getScheduler().cancelTask(PreGameCountdown.getInstance().getID());
                try {
                    (new PreGameCountdown(Integer.valueOf(args[0]), sender)).schedule();
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }
        if (args.length == 0) {
            Main.getInstance().getGameInstance().start(sender);
            return true;
        } else if (args.length == 1) {
            try {
                int cdSecs = Integer.valueOf(args[0]);
                (new PreGameCountdown(cdSecs, sender)).schedule();
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
