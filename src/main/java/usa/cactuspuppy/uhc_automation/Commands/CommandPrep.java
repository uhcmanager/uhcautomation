package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class CommandPrep {
    public static boolean onCommand(CommandSender commandSender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        } else {
            Main.getInstance().getGameInstance().prep();
        }
        return true;
    }
}
