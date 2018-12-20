package usa.cactuspuppy.uhc_automation.Commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.*;

@org.bukkit.plugin.java.annotation.command.Command(name = "surface")
public class CommandSurface implements CommandExecutor, TabCompleter {
    private static final int TP_WAIT_TIME = 10;
    private static final Map<UUID, PlayerTPInfo> TP_QUEUE = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!Main.getInstance().getConfig().getBoolean("surface", false)) {
            sender.sendMessage(ChatColor.RED + "/surface is not enabled! Ask an admin if you think this is incorrect.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command is player only!");
            return true;
        }
        if (!Main.getInstance().getGameInstance().isBorderShrinking()) {
            sender.sendMessage(ChatColor.RED + "Cannot surface until border begins to shrink!");
            return true;
        }
        if (!Main.getInstance().getGameInstance().getLivePlayers().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only live players may utilize this command.");
            return true;
        }
        if (TP_QUEUE.keySet().contains(((Player) sender).getUniqueId())) {
            TP_QUEUE.remove(((Player) sender).getUniqueId());
            sender.sendMessage(ChatColor.AQUA + "Surfacing cancelled! Use /surface to begin surfacing again.");
            return true;
        }
        TP_QUEUE.put(((Player) sender).getUniqueId(), new PlayerTPInfo(TP_WAIT_TIME, System.currentTimeMillis() + TP_WAIT_TIME * 1000));
        sender.sendMessage(ChatColor.GOLD + "Surfacing initiated. You must stand still and take no damage for the next " + TP_WAIT_TIME + " seconds for surfacing to be successful.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    public static void startHelper() {
        PlayerTPHelper ptpHelper = new PlayerTPHelper();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), ptpHelper, 0L, 2L);
        Bukkit.getPluginManager().registerEvents(ptpHelper, Main.getInstance());
    }

    private static class PlayerTPHelper implements Listener, Runnable {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            for (UUID u : TP_QUEUE.keySet()) {
                Player p = Bukkit.getPlayer(u);
                if (p == null || !p.isOnline()) {
                    TP_QUEUE.remove(u);
                    continue;
                }
                PlayerTPInfo ptpInfo = TP_QUEUE.get(u);
                if (now >= ptpInfo.getTpTime()) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "Surfacing...");
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1.75F);
                    surfacePlayer(p);
                    return;
                }
                if (now < ptpInfo.getTpTime() - ptpInfo.getNextSecond() * 1000) continue;
                p.sendMessage(ChatColor.YELLOW + "Surfacing in " + UHCUtils.secsToFormatString(ptpInfo.nextSecond));
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                ptpInfo.setNextSecond(ptpInfo.getNextSecond() - 1);
            }
        }

        private void surfacePlayer(Player p) {
            Location curr = UHCUtils.highestBlock(p.getLocation());
            p.teleport(new Location(curr.getWorld(), curr.getBlockX() + 0.5, curr.getWorld().getHighestBlockYAt(curr.getBlockX(), curr.getBlockZ()), curr.getBlockZ() + 0.5));
            TP_QUEUE.remove(p.getUniqueId());
        }

        @EventHandler
        public void onPlayerDamage(EntityDamageEvent e) {
            if (!(e instanceof Player)) return;
            Player p = (Player) e;
            if (!TP_QUEUE.keySet().contains(p.getUniqueId())) return;
            TP_QUEUE.remove(p.getUniqueId());
            p.sendMessage(ChatColor.RED + "Surfacing cancelled; you took damage!");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }

        private static final double MOVE_THRESHOLD = 0.01;

        @EventHandler
        public void onPlayerMove(PlayerMoveEvent e) {
            if (!TP_QUEUE.keySet().contains(e.getPlayer().getUniqueId())) return;
            if (e.getFrom().getWorld().equals(e.getTo().getWorld()) && Math.abs(e.getFrom().getX() - e.getTo().getX()) < MOVE_THRESHOLD && (e.getFrom().getZ() - e.getTo().getZ()) < MOVE_THRESHOLD) return;
            TP_QUEUE.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(ChatColor.RED + "Surfacing cancelled; you moved!");
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }
    }

    @Data
    @AllArgsConstructor
    private static class PlayerTPInfo {
        private int nextSecond;
        private long tpTime;
    }
}
