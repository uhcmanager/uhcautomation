package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandRules {

    public static void onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        commandSender.sendMessage(UHCUtils.getRules(Main.getInstance()));
        return;
    }
}
