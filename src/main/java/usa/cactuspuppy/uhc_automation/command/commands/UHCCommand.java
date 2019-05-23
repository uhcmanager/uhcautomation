package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface UHCCommand extends TabCompleter {
    String getUsage();

    String getPurpose();

    String getMoreInfo();

    boolean onCommand(CommandSender commandSender, String alias, String[] args);

    boolean hasPermission(CommandSender commandSender, String alias, String[] args);

    @Override
    @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args);
}
