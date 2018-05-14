package usa.cactuspuppy.uhc_automation;

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
        m.gi.setGameWorld(args[0]);
        return true;
    }
}
