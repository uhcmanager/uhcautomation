package usa.cactuspuppy.uhc_automation.OneShot;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.UHCUtils;

import java.util.*;

public class SwordHandler implements Listener {
    @Getter private static SwordHandler instance;
    @Getter private final static String[] lore = {ChatColor.AQUA + "From lines of code, I draw my force",
            ChatColor.AQUA + "Straight and true, I fly my course",
            ChatColor.AQUA + "My target had best flee from you, my friend",
            ChatColor.AQUA + "For with a single blow from me, their life I shall end",
            ChatColor.BLUE.toString() + ChatColor.WHITE.toString() + ChatColor.BLUE};

    public SwordHandler() {
        instance = this;
    }

    public Location spawnPlayerKiller(GameInstance gameInstance) {
        World world = gameInstance.getWorld();
        Random random = new Random();
        int x = random.nextInt(gameInstance.getFinalSize() - 1) - (gameInstance.getFinalSize() / 2);
        int z = random.nextInt(gameInstance.getFinalSize() - 1) - (gameInstance.getFinalSize() / 2);
        int y = world.getHighestBlockYAt(x, z) + 1;
        Location location = new Location(world, x, y, z);
        Block block = world.getBlockAt(location);
        block.setType(Material.CHEST);
        if (!(block.getType().equals(Material.CHEST))) {
            Main.getInstance().getLogger().warning("Could not summon player-killer chest.");
            return null;
        }
        Chest chest = (Chest) block.getState();
        chest.getBlockInventory().addItem(createPlayerKiller());
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        return location;
    }

    @EventHandler
    public void damageHandler(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
        Player attacker = (Player) e.getDamager();
        Player target = (Player) e.getEntity();
        if (!isPlayerKiller(attacker.getInventory().getItemInMainHand())) return;
        if (!isTarget(target) && !Main.getInstance().getConfig().getBoolean("one-shot.other-damage")) {
            e.setCancelled(true);
            return;
        }
        target.setHealth(0);
        UHCUtils.broadcastMessageWithSound(ChatColor.DARK_RED + target.getDisplayName() + " has been struck down!", Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
    }

    private boolean isTarget(Player p) {
        String string = Main.getInstance().getConfig().getString("one-shot.target");
        if (string == null) return false;
        if (string.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
            return p.getUniqueId().equals(UUID.fromString(string));
        }
        return p.getName().equals(string);
    }

    private ItemStack createPlayerKiller() {
        ItemStack rv = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta rvMeta = rv.getItemMeta();
        rvMeta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "BLADE OF DESTINY");
        List<String> rvLore = rvMeta.getLore();
        if (rvLore == null) {
            rvLore = new ArrayList<>();
        }
        rvLore.addAll(Arrays.asList(lore));
        rvMeta.setLore(rvLore);
        rv.setItemMeta(rvMeta);

        return rv;
    }

    private boolean isPlayerKiller(ItemStack item) {
        if (item.getType() != Material.DIAMOND_SWORD) return false;
        List<String> itemLore = item.getItemMeta().getLore();
        for (String lorette : lore) {
            if (!itemLore.contains(lorette)) {
                return false;
            }
        }
        return true;
    }
}
