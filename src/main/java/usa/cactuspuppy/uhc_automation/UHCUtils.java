package usa.cactuspuppy.uhc_automation;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class UHCUtils {
    /**
     * Assistance with phrases provided by: Djinnjurr, krohn67, daniodle, Aurabeth, Remuko, Wyntr, DreDeveraux
     */
    public static final String[] START_MSGS =
            {"Live and let die!", "Go for broke!", "Ready Player One!", "No time like the present!",
                    "This ought to be a match to remember!", "Eliminate Other Players", "Capture Objective A", "Triumph or Die!",
                    "Hack and Slash!", "Lives Remaining: 1", "Tear them apart!", "May the odds be ever in your favor!",
                    "Once more unto the breach", "Go Get 'Em, Tiger!", "Time to kick the tires and light the fires!", "Git 'er Done!",
                    "Alea Iacta Est", "Cry havoc and let slip the dogs of war", "Let Slip the Dogs of War!", "Veni, vidi, vici.",
                    "Oooh hoo hoo hoo... this'll be good!", "Fire In The Hole!", "Fire at will!", "Fight for the Assassins!",
                    "Kill or be killed!", "THEY SHALL NOT PASS", "If anyone is gonna win, it's gonna be you.",
                    "Who's ready for some fireworks!", "LEEEROY JENKINS", "[inspirational message]", "Release the hounds!",
                    "Good luck, don't die!", "Just Do It!", "FOR THE HORDE!", "For The Alliance!", "Watch out for bears!",
                    "How do YOU want to do this?", "Know yourself, know thy enemy, and you shall win.", "For Aiur!",
                    "One Punch is all you need!", "Roll for Initiative!", "You know you have to do it to 'em", "Watch out for boars!"};
    //do not instantiate
    public UHCUtils() { }

    public static Set<UUID> getWorldPlayers(World w) {
        HashSet<UUID> rv = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(w)) {
                rv.add(p.getUniqueId());
            }
        }
        return rv;
    }

    public static Set<UUID> getWorldLivePlayers(World w, Set<UUID> players) {
        HashSet<UUID> rv = new HashSet<>();
        for (UUID u : players) {
            Player p = Bukkit.getPlayer(u);
            if (p.getGameMode().equals(GameMode.SURVIVAL) && p.getWorld().equals(w)) {
                rv.add(p.getUniqueId());
            }
        }
        return rv;
    }

    public static World getWorldFromString(Main m, Server s, String name) {
        for (World w : s.getWorlds()) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        World w = s.getWorlds().get(0);
        m.getLogger().log(Level.WARNING, "Could not find world " + name
                + ". Defaulting to world " + w.getName());
        m.getConfig().set("world", w.getName());
        m.saveConfig();
        return w;
    }

    public static void exeCmd(Server s, World w, String cmd) {
        /*s.getLogger().log(Level.INFO, "Attempting to run command: '" + cmd + "'");
        MinecraftServer nmsServer = ((CraftServer) s).getServer();
        WorldServer nmsWorld = ((CraftWorld) w).getHandle();
        EntityPlayer ep = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(UUID.randomUUID(), "UHC_Automation"), new PlayerInteractManager(nmsWorld));
        ep.setLocation(0, 0, 0, 0, 0);
        CraftPlayer d = new CraftPlayer((CraftServer) s, ep);
        d.setOp(true);
        try {
           d.performCommand(cmd);
        } catch (CommandException e) { }*/
        s.dispatchCommand(s.getConsoleSender(), cmd);
    }

    public static void exeCmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    /**
     * @source ConnorLinfoot
     * @param player to send title actionbar to
     * @param message to display
     */
    public static void sendActionBar(Player player, String message){
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.CHAT);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

    public static String randomStartMSG() {
        Random r = new Random();
        int msg = r.nextInt(START_MSGS.length);
        return START_MSGS[msg];
    }
}
