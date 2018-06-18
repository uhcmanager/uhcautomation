package usa.cactuspuppy.uhc_automation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStatus implements CommandExecutor {
    private Main m;
    public CommandStatus(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        m.gi.logStatus(commandSender);
        return true;
    }
}
