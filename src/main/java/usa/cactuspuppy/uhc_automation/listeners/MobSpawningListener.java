package usa.cactuspuppy.uhc_automation.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import usa.cactuspuppy.uhc_automation.GameInstance;
import usa.cactuspuppy.uhc_automation.Main;

public class MobSpawningListener implements Listener {
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();
        if (!entity.getLocation().getWorld().equals(Main.getInstance().getGameInstance().getWorld())) return;
        if (!entity.getType().equals(EntityType.PHANTOM) && !entity.getType().equals(EntityType.ZOMBIE)) return;

        if (entity.getType().equals(EntityType.PHANTOM) && Main.getInstance().getConfig().getBoolean("mob-disable.phantom", false)) {
            if (GameInstance.isDEBUG()) Main.getInstance().getLogger().finer("Phantom (" + entity.getUniqueId().toString() + ") prevented from spawning.");
            e.setCancelled(true);
        } else if (entity.getType().equals(EntityType.ZOMBIE) && Main.getInstance().getConfig().getBoolean("mob-disable.baby-zombie", false)) {
            Zombie zombie = (Zombie) entity;
            if (zombie.isBaby()) {
                if (GameInstance.isDEBUG()) Main.getInstance().getLogger().finer("Zombie (" + zombie.getUniqueId().toString() + ") converted from baby to adult.");
                zombie.setBaby(false);
            }
        }
    }
}
