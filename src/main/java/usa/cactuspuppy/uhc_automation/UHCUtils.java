package usa.cactuspuppy.uhc_automation;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

    public static World getWorldFromString(Main m, Server s, String name) {
        for (World w : s.getWorlds()) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        World w = s.getWorlds().get(0);
        m.getLogger().log(Level.SEVERE, "Could not find world " + name
                + "\nDefaulting to world " + w.getName());
        m.getConfig().set("world", w.getName());
        return w;
    }

    public static void exeCmd(Server s, World w, String cmd) {
        MinecraftServer ms = ((CraftServer) s).getServer();
        WorldServer ws = ((CraftWorld) w).getHandle();
        EntityPlayer ep = new EntityPlayer(ms, ws, new GameProfile(UUID.randomUUID(), "."), new PlayerInteractManager(ws));
        Location loc = new Location(w, 0.0, 0.0, 0.0);
        ep.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        ep.teleportTo(loc, false);
        CraftPlayer cp = new CraftPlayer((CraftServer) s, ep);
        s.dispatchCommand(cp.getPlayer(), cmd);
    }
}
