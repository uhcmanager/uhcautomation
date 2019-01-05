package usa.cactuspuppy.uhc_automation.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

public final class MojangAPIHook {
    public static UUID getUUID(String username) {
        OfflinePlayer p = Bukkit.getPlayer(username);
        if (p != null) {
            return p.getUniqueId();
        } else {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
                StringBuilder responseBuilder = new StringBuilder();

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                if (responseCode != 200) {
                    Logger.logWarning(MojangAPIHook.class, "Bad response code while querying Mojang API.\n" +
                            "Code: " + responseCode + "\n" +
                            "Message: " + responseMessage + "\n" +
                            "Queried Name: " + username, Optional.empty());
                    return null;
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
                return UUID.fromString(uuidString);
            } catch (MalformedURLException e) {
                Logger.logWarning(MojangAPIHook.class, "API URL invalid!", Optional.of(e));
            } catch (IOException e) {
                Logger.logWarning(MojangAPIHook.class, "Issue retrieving JSON payload", Optional.of(e));
            } catch (ParseException e) {
                Logger.logWarning(MojangAPIHook.class, "Issue parsing JSON payload", Optional.of(e));
            }
            return null;
        }
    }
}
