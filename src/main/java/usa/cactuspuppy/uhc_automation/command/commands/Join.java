package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;
import usa.cactuspuppy.uhc_automation.utils.MojangAPIHook;

import java.util.*;
import java.util.stream.Collectors;

public class Join implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc join <player> [ID/name]";
    }

    @Override
    public String getPurpose() {
        return "Adds a player to the specified game";
    }

    @Override
    public String getMoreInfo() {
        return "Adds a player to the specified game, or if unspecified the game whose lobby world the executor is in.";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        if (args.length < 1) {
            return false;
        }

        UUID player = MojangAPIHook.getUUID(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.RESET + args[0]);
            return true;
        }

        MiscUtils.GetInstanceResult result = MiscUtils.getGameInstance(commandSender, Arrays.copyOfRange(args, 1, args.length));
        if (!result.isUsageCorrect()) {
            return false;
        }
        GameInstance addTo = result.getInstance();
        if (addTo == null) {
            return true;
        }
        addTo.addPlayer(player);
        commandSender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.WHITE + args[0] + ChatColor.GREEN + " to " + ChatColor.WHITE + addTo.getName());
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return commandSender.hasPermission("uhc.manager");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String match = args[0];
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(match)).collect(Collectors.toList());
        } else {
            String[] nameArr = Arrays.copyOfRange(args, 1, args.length);
            String name = String.join(" ", nameArr);
            return GameManager.getActiveGames().values().stream().map(GameInstance::getName).distinct().filter(n -> n.startsWith(name)).sorted().collect(Collectors.toList());
        }
    }
}
