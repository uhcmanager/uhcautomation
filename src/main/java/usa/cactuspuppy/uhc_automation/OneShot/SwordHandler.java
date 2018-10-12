package usa.cactuspuppy.uhc_automation.OneShot;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Main;

import java.util.List;
import java.util.Random;

public class SwordHandler implements Listener {
    @Getter private final static String lore = ChatColor.AQUA
            + "From lines of code, I draw my force" +
            "\nStraight and true, I fly my course" +
            "\nMy target best flee from you, my friend" +
            "\nFor a single blow from me, their life, I shall end";

    public static Location spawnPlayerKiller(GameInstance gameInstance) {
        World world = gameInstance.getWorld();
        Random random = new Random();
        int x = random.nextInt(gameInstance.getFinalSize() * 2) - gameInstance.getFinalSize() + 1;
        int z = random.nextInt(gameInstance.getFinalSize() * 2) - gameInstance.getFinalSize() + 1;
        int y = world.getHighestBlockYAt(x, z) + 1;
        Location location = new Location(world, x, y, z);
        Block block = world.getBlockAt(location);
        block.setType(Material.CHEST);
        if (!(block instanceof Chest)) {
            Main.getInstance().getLogger().warning("Could not summon player-killer chest.");
            return null;
        }
        Chest chest = (Chest) block;
        chest.getBlockInventory().addItem(createPlayerKiller());
    }

    public static ItemStack createPlayerKiller() {
        ItemStack rv = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta rvMeta = rv.getItemMeta();
        List<String> rvLore = rvMeta.getLore();
        rvLore.add(lore);
        rvMeta.setLore(rvLore);

        return rv;
    }
}
