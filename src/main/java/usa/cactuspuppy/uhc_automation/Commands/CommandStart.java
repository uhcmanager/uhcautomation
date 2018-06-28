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
    private Main main;

    public CommandStart(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!main.gi.validate(sender)) {
            UHCUtils.broadcastMessage(main.gi, ChatColor.RED + "Could not start UHC, settings invalid.");
            return true;
        } else if (main.gi.isActive()) {
            sender.sendMessage(ChatColor.RED + "A game is already in progress! Use " + ChatColor.RESET + "/uhcreset " + ChatColor.RED + "to stop the game first!");
            return true;
        }
        if (PreGameCountdown.instanced) {
            if (args.length == 0) {
                PreGameCountdown.getInstance().instaStart();
            } else if (args.length == 1) {
                Bukkit.getScheduler().cancelTask(PreGameCountdown.getInstance().getID());
                try {
                    (new PreGameCountdown(main, Integer.valueOf(args[0]), sender)).schedule();
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }
        if (args.length == 0) {
            main.gi.start(sender);
            return true;
        } else if (args.length == 1) {
            try {
                int cdSecs = Integer.valueOf(args[0]);
                (new PreGameCountdown(main, cdSecs, sender)).schedule();
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
