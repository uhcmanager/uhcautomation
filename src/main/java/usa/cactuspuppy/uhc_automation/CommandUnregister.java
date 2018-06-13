package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class CommandUnregister implements CommandExecutor {
    private Main m;

    public CommandUnregister(Main main) {
        m = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }
        String name = args[0];
        if (!UHCUtils.validUsername(name)) {
            commandSender.sendMessage(ChatColor.RED + name + " is not a valid username!");
            return true;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                OfflinePlayer p = Bukkit.getPlayer(name);
                if (p != null) {
                    UUID u = p.getUniqueId();
                    m.gi.blacklistPlayer(u);
                    commandSender.sendMessage(ChatColor.GREEN + "Unregistered " + ChatColor.WHITE + p.getName() + ChatColor.GREEN +  " from the UHC.");
                    commandSender.sendMessage(ChatColor.YELLOW + "Remember that this player can now only be readded to the UHC with /uhcreg");
                    m.getLogger().info("Blacklisted " + p.getName() + " from " + m.gi.getWorld().getName() + "'s UHC");
                } else {
                    commandSender.sendMessage(ChatColor.YELLOW + "Unable to find " + ChatColor.WHITE + name + ChatColor.YELLOW + " in the server database, requesting UUID from Mojang now...");
                    try {
                        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                        StringBuilder responseBuilder = new StringBuilder();

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        int responseCode = connection.getResponseCode();
                        if (responseCode != 200) {
                            commandSender.sendMessage(ChatColor.DARK_RED + "An error occurred while accessing the Mojang Database! Error code: " + ChatColor.RED + responseCode);
                            m.getLogger().info("Error while accessing Mojang database. Queried Name: " + name + " - Response Code: " + responseCode);
                        }
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line = reader.readLine();
                            while (line != null) {
                                responseBuilder.append(line);
                                line = reader.readLine();
                            }
                        }
                        JSONObject response = (JSONObject) JSONValue.parseWithException(responseBuilder.toString());
                        String uuidString = (String) response.get("id");
                        uuidString = uuidString.replaceAll("(.{8})(.{4})(.{4})(.{4})(.+)", "$1-$2-$3-$4-$5");
                        UUID u = UUID.fromString(uuidString);
                        String name = (String) response.get("name");
                        m.gi.blacklistPlayer(u);
                        commandSender.sendMessage(ChatColor.GREEN + "Unregistered " + ChatColor.WHITE + name + ChatColor.GREEN +  " from the " + m.getConfig().getString("event-name"));
                        commandSender.sendMessage(ChatColor.YELLOW + "Remember that this player can now only be readded to the UHC with /uhcreg");
                        m.getLogger().info("Blacklisted " + name + " from the " + m.getConfig().getString("event-name"));
                        UHCUtils.broadcastMessage(m.gi, ChatColor.RED + name + " has been removed from the UHC.");
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                        commandSender.sendMessage(ChatColor.DARK_RED + "An error occurred, please try again later.");
                    }
                }
            }
        }.runTaskAsynchronously(m);
        return true;
    }
}
