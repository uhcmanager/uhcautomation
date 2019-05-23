package usa.cactuspuppy.uhc_automation.oneshot;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
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
    private Block chest;

    public SwordHandler() {
        instance = this;
    }

    public Location spawnPlayerKiller(GameInstance gameInstance) {
        World world = gameInstance.getWorld();
        Random random = new Random();
        int bound;
        if (gameInstance.getFinalSize() <= 1) {
            bound = 1;
        } else {
            bound = (gameInstance.getFinalSize() + 1) / 2;
        }
        int x = random.nextInt(bound);
        int z = random.nextInt(bound);
        int y = world.getHighestBlockYAt(x, z) + 1;
        if (y < 2) y = 2;
        Location location = new Location(world, x, y, z);
        Block block = world.getBlockAt(location);
        block.setType(Material.CHEST);
        world.getBlockAt(x, y - 1, z).setType(Material.BEACON);
        for (int x1 = x - 1; x1 <= x + 1; x1++) {
            for (int z1 = z - 1; z1 <= z + 1; z1++) {
                world.getBlockAt(x1, y - 2, z1).setType(Material.IRON_BLOCK);
            }
        }
        if (!(block.getType().equals(Material.CHEST))) {
            Main.getInstance().getLogger().warning("Could not summon player-killer chest.");
            return null;
        }
        Chest chest = (Chest) block.getState();
        chest.getBlockInventory().addItem(createPlayerKiller());
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        this.chest = block;
        return location;
    }

    @EventHandler
    public void damageHandler(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
        Player attacker = (Player) e.getDamager();
        Player target = (Player) e.getEntity();
        if (!isPlayerKiller(attacker.getInventory().getItemInMainHand())) return;
        if (isNotTarget(target) && !Main.getInstance().getConfig().getBoolean("one-shot.other-damage")) {
            e.setCancelled(true);
            return;
        }
        target.setHealth(0);
        UHCUtils.broadcastMessageWithSound(ChatColor.DARK_RED + target.getDisplayName() + " has been struck down!", Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
    }

    @EventHandler
    public void inventoryHandler(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();
        if (isNotTarget(p) && !onTargetTeam(p)) {
            if (isPlayerKiller(i)) {
                //TODO: Deactivate beacon
                World world = chest.getWorld();
                int x = chest.getX();
                int y = chest.getY() - 2;
                int z = chest.getZ();
                world.getBlockAt(x, y, z).setType(Material.OBSIDIAN);
            }
            return;
        }
        if (isPlayerKiller(i)) e.setCancelled(true);
    }

    @EventHandler
    public void pickupHandler(EntityPickupItemEvent e) {
        if (!e.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player p = (Player) e.getEntity();
        if (isNotTarget(p) && !onTargetTeam(p)) return;
        ItemStack itemStack = e.getItem().getItemStack();
        if (!isPlayerKiller(itemStack)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (isSpawnedStructure(e.getBlock())) e.setCancelled(true);
    }

    @EventHandler
    public void blockBurn(BlockIgniteEvent e) {
        if (isSpawnedStructure(e.getBlock())) e.setCancelled(true);
    }

    private boolean isNotTarget(Player p) {
        return !p.getName().equals(getTargetName());
    }

    private boolean onTargetTeam(Player check) {
        if (!Main.getInstance().getGameInstance().isTeamMode()) return false;
        if (Bukkit.getPlayer(getTargetName()) == null) return false;
        Team team1 = Main.getInstance().getGameInstance().getScoreboard().getEntryTeam(check.getName());
        Team team2 = Main.getInstance().getGameInstance().getScoreboard().getEntryTeam(getTargetName());
        if (team1 == null || team2 == null) return false;
        return team1.equals(team2);
    }

    private String getTargetName() {
        String string = Main.getInstance().getConfig().getString("one-shot.target");
        if (string == null) return null;
        if (string.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
            Player p = Bukkit.getPlayer(UUID.fromString(string));
            if (p == null) return null;
            return p.getName();
        }
        return string;
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
        rvLore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "Target: " + ChatColor.RESET + ChatColor.UNDERLINE + getTargetName());
        rvMeta.setLore(rvLore);
        rv.setItemMeta(rvMeta);

        return rv;
    }

    private boolean isPlayerKiller(ItemStack item) {
        if (item == null) return false;
        if (item.getType() != Material.DIAMOND_SWORD) return false;
        List<String> itemLore = item.getItemMeta().getLore();
        for (String lorette : lore) {
            if (!itemLore.contains(lorette)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSpawnedStructure(Block block) {
        if (block.getY() == chest.getY() || block.getY() == chest.getY() - 1) {
            return block.getX() == chest.getX() && block.getZ() == chest.getZ();
        } else if (block.getY() == chest.getY() - 2) {
            return Math.abs(block.getX() - chest.getX()) <= 1 && Math.abs(block.getZ() - chest.getZ()) <= 1;
        }
        return false;
    }

    public void removeSpawnedStructure() {
        for (int y = chest.getY() - 2; y <= chest.getY(); y++) {
            if (y == chest.getY() - 2) {
                for (int x = chest.getX() - 1; x <= chest.getX() + 1; x++) {
                    for (int z = chest.getZ() - 1; z <= chest.getZ() + 1; z++) {
                        chest.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            } else {
                chest.getWorld().getBlockAt(chest.getX(), y, chest.getZ()).setType(Material.AIR);
            }
        }
    }
}
