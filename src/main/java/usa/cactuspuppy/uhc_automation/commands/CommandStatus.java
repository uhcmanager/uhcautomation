package usa.cactuspuppy.uhc_automation.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;

public class CommandStatus extends UHCCommand {
    public CommandStatus() {
        name = "status";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        Main.getInstance().getGameInstance().logStatus(sender);
    }
}
