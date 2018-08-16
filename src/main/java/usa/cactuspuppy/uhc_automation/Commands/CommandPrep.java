package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class CommandPrep {
    public static boolean onCommand(CommandSender commandSender, String[] args) {
        if (Main.getInstance().getGameInstance().isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is currently active, use /uhcstop to stop the game or wait until the current game is finished before attempt to prep the world.");
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("load")) { generateChunks(); }

        return true;
    }

    private void generateChunks() {
        Main.getInstance().getLogger().info(ChatColor.RED + "Beginning world pre-load");
        World
    }
}
