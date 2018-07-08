package usa.cactuspuppy.uhc_automation.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

public class CommandRules implements CommandExecutor {
    private Main m;
    public static String numDelimiter = ") ";
    public static String rulesTitlePrefix = "\n" + ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
    public static String ruleNumPrefix = ChatColor.GREEN.toString();

    public CommandRules(Main m) {
        this.m = m;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        commandSender.sendMessage(UHCUtils.getRules(m));
        return true;
    }
}
