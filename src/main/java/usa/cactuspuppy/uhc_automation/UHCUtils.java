package usa.cactuspuppy.uhc_automation;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandException;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class UHCUtils {
    public static Set<Player> getWorldPlayers(World w) {
        HashSet<Player> rv = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(w)) {
                rv.add(p);
            }
        }
        return rv;
    }

    public static Set<Player> getWorldLivePlayers(World w, Set<Player> players) {
        HashSet<Player> rv = new HashSet<>();
        for (Player p : players) {
            if (p.getGameMode().equals(GameMode.SURVIVAL) && p.getWorld().equals(w)) {
                rv.add(p);
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
        d.setOp(true);*/
        //try {
//            d.performCommand(cmd);
        //} catch (CommandException e) { }
        s.dispatchCommand(s.getConsoleSender(), cmd);
    }

    public static void exeCmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
}
