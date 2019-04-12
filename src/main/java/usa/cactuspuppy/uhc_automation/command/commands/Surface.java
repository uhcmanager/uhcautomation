package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Surface implements UHCCommand {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }

    @Override
    public String getUsage() {
        return "/uhc surface";
    }

    @Override
    public String getPurpose() {
        return "Teleport to the surface.";
    }

    @Override
    public String getMoreInfo() {
        return "Allows the user to teleport safely to the surface after a brief delay. Will cancel if the player moves or takes damage.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //TODO
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return true;
    }
}
