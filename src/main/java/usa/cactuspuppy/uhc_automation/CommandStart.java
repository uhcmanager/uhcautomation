package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {
    private Main main;

    public CommandStart(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!main.gi.validate()) {
            sender.sendMessage(ChatColor.RED + "Game settings are not valid! Use /uhcstatus to see current settings or check config.");
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
                    (new PreGameCountdown(main, Integer.valueOf(args[0]))).schedule();
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        }
        if (args.length == 0) {
            return main.gi.start();
        } else if (args.length == 1) {
            try {
                int cdSecs = Integer.valueOf(args[0]);
                (new PreGameCountdown(main, cdSecs)).schedule();
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }
}
