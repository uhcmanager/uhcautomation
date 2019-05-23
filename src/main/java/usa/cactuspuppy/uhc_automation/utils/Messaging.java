package usa.cactuspuppy.uhc_automation.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Objects;
import java.util.Random;

public final class Messaging {
    public static String[] START_MSGS =
            {"Live and let die!", "Go for broke!", "Ready Player One!", "No time like the present!",
                    "This ought to be a match to remember!", "Eliminate Other Players", "Capture Objective A", "Triumph or Die!",
                    "Hack and Slash!", "Lives Remaining: 0", "Tear them apart!", "May the odds be ever in your favor!",
                    "Once more unto the breach", "Go Get 'Em, Tiger!", "Time to kick the tires and light the fires!", "Git 'er Done!",
                    "Alea Iacta Est", "Cry havoc and let slip the dogs of war", "Let Slip the Dogs of War!", "Veni, vidi, vici.",
                    "Oooh hoo hoo hoo... this'll be good!", "Fire In The Hole!", "Fire at will!", "Fight for the Assassins!",
                    "Kill or be killed!", "THEY SHALL NOT PASS", "If anyone is gonna win, it's gonna be you.",
                    "Who's ready for some fireworks!", "LEEEROY JENKINS", "Release the hounds!",
                    "Good luck, don't die!", "Good luck, have fun!", "Just Do It!", "FOR THE HORDE!", "For The Alliance!", "Watch out for bears!",
                    "How do YOU want to do this?", "Know yourself, know thy enemy, and you shall win.", "For Aiur!",
                    "One Punch is all you need!", "Roll for Initiative!", "You know you have to do it to 'em", "Watch out for boars!", "CHAAAAAAARGE",
                    "Ready for Battle!", "Ready for Combat!", "D.Va, ready for combat!", "Fight!", "Go for Broke!"};
    //TODO: Replace with imported start msgs

    public static String getStartMsg(Random random) {
        int index = random.nextInt(START_MSGS.length);
        return START_MSGS[index];
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public static void broadcastMessage(GameInstance instance, String msg) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.sendMessage(msg));
    }

    public static void broadcastTitle(GameInstance instance, String title, String subtitle, int in, int stay, int out) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.sendTitle(title, subtitle, in, stay, out));
    }

    public static void broadcastSound(GameInstance instance, String sound, float volume, float pitch) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.playSound(p.getLocation(), sound, volume, pitch));
    }

    public static void broadcastSound(GameInstance instance, Sound sound, float volume, float pitch) {
        instance.getAllPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.playSound(p.getLocation(), sound, volume, pitch));
    }
}
