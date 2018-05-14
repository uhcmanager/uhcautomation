package usa.cactuspuppy.uhc_automation;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CommandTest implements CommandExecutor {
    private Main m;

    public CommandTest(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Sent test command!");
            UHCUtils.exeCmd(Bukkit.getServer(), m.gi.getWorld(), "say hi");
        } else {
            String cmd = StringUtils.join(args, " ");
            UHCUtils.exeCmd(Bukkit.getServer(), m.gi.getWorld(), cmd);
        }
        return true;
    }
}
