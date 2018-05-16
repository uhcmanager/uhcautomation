package usa.cactuspuppy.uhc_automation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTime implements CommandExecutor {
    private Main m;

    public CommandTime(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        long currTime = System.currentTimeMillis();
        if (!m.gi.isActive()) {
            commandSender.sendMessage(ChatColor.RED + "Game is not active yet!");
            return true;
        }
        long timeElapsed = currTime - m.gi.startT;
        int timeElapsedSecs = (int) timeElapsed / 1000;
        int hours = timeElapsedSecs / 3600;
        int mins = timeElapsedSecs / 60;
        int secs = timeElapsedSecs % 60;
        commandSender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD
                + "Time Elapsed: " + ChatColor.RESET + hours + " Hours " + mins + " Minutes " + secs + " Seconds");
        return true;
    }
}
