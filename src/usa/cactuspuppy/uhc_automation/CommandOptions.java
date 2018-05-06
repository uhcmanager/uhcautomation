package usa.cactuspuppy.uhc_automation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandOptions implements CommandExecutor {
    private Main main;
    private static final String[] options = {"initSize", "finalSize", "timeToBorderShrink", "teamMode"};

    public CommandOptions(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return false;
        }
        return false;
    }
}
