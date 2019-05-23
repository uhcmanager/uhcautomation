package usa.cactuspuppy.uhc_automation.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandRules extends UHCCommand {

    public CommandRules() {
        name = "rules";
        adminOnly = false;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        sender.sendMessage(UHCUtils.getRules(Main.getInstance()));
    }
}
