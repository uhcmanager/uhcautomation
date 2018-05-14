package usa.cactuspuppy.uhc_automation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLobby implements CommandExecutor {
    private Main m;
    public CommandLobby(Main main) {
        m = main;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        m.gi.prep();
        return true;
    }
}
