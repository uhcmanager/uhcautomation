package usa.cactuspuppy.uhc_automation;

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
        }
        return false;
    }
}
