package usa.cactuspuppy.uhc_automation.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class UHCCommand {
    @Getter String name = "";
    @Getter boolean adminOnly = true;

    public abstract void onCommand(CommandSender sender, Command command, String alias, String[] args);
}
