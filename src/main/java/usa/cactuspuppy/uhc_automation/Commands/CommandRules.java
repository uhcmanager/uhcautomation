package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

@NoArgsConstructor
public class CommandRules implements CommandExecutor {
    public static String numDelimiter = ") ";
    public static String rulesTitlePrefix = "\n" + ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
    public static String ruleNumPrefix = ChatColor.GREEN.toString();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        commandSender.sendMessage(UHCUtils.getRules(Main.getInstance()));
        return true;
    }
}
