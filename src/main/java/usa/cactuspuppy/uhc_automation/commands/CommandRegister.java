package usa.cactuspuppy.uhc_automation.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.Main;

public class CommandRegister extends UHCCommand {
    public CommandRegister() {
        name = "register";
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /uhc " + alias + " <player>");
        }
        String playerName = args[0];
        Player p = Bukkit.getPlayer(playerName);
        if (p == null || !p.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Could not find player " + ChatColor.WHITE +  playerName + ChatColor.RED + ". Make sure the player is online.");
            return;
        }
        Main.getInstance().getGameInstance().getBlacklistPlayers().remove(p.getUniqueId());
        Main.getInstance().getGameInstance().registerPlayer(p);
        sender.sendMessage(ChatColor.GREEN + "Successfully registered " + playerName + " in " + Main.getInstance().getConfig().getString("event-name"));
        Main.getInstance().getLogger().info("Registered " + playerName + " into " + Main.getInstance().getConfig().getString("event-name"));
        p.sendTitle(ChatColor.GOLD + "Welcome", "to " + Main.getInstance().getConfig().getString("event-name"), 20, 60, 20);
        p.setHealth(19);
        p.setHealth(20);
        p.sendMessage(ChatColor.YELLOW + "You have been manually added to " + Main.getInstance().getConfig().getString("event-name"));
        return;
    }
}
