package usa.cactuspuppy.uhc_automation.game.tasks.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import usa.cactuspuppy.uhc_automation.Main;
import usa.cactuspuppy.uhc_automation.event.game.player.PlayerJoinEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameManager;
import usa.cactuspuppy.uhc_automation.game.types.UHC;

import java.util.Objects;

public class UHC_LobbyListener extends ListenerTask {
    private UHC uhc;

    public UHC_LobbyListener(UHC uhcInstance) {
        super(uhcInstance);
        uhc = uhcInstance;
    }

    //Add joiners to the game
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        //Check that player is joining lobby world
        if (!Objects.equals(p.getWorld(), getGameInstance().getMainWorld())) {
            return;
        }
        //Add player to game
        if (p.getGameMode().equals(GameMode.SURVIVAL)) {
            getGameInstance().addPlayer(p.getUniqueId());
        } else {
            getGameInstance().addSpectator(p.getUniqueId());
        }
        //Greet player
        p.sendTitle(ChatColor.GOLD + "Welcome", "to " + getGameInstance().getName(), 10, 80, 20);
    }

    //Check for gamemode changing
    @EventHandler
    public void onGamemode(PlayerGameModeChangeEvent event) {
        Player p = event.getPlayer();
        if (!getGameInstance().getAllPlayers().contains(p.getUniqueId())) {
            return;
        }
        if (event.getNewGameMode().equals(GameMode.SURVIVAL)) {
            getGameInstance().removeSpectator(p.getUniqueId(), false);
            getGameInstance().addPlayer(p.getUniqueId());
        } else if (getGameInstance().getAlivePlayers().contains(p.getUniqueId())) {
            getGameInstance().moveAliveToSpec(p.getUniqueId());
            getGameInstance().getUtils().broadcastChat(ChatColor.WHITE.toString() + ChatColor.BOLD + "[" + ChatColor.GOLD + "INFO" + ChatColor.WHITE + "] " + ChatColor.GREEN + p.getDisplayName() + ChatColor.WHITE + " is now spectating");
        }
    }

    //Check for world changing
    @EventHandler
    public void onWorldChange(PlayerTeleportEvent event) {
        if (event.getTo() == null) {
            return;
        }
        if (Objects.equals(event.getFrom().getWorld(), event.getTo().getWorld())) {
            return;
        }
        Player p = event.getPlayer();
        GameInstance playerGame = GameManager.getPlayerGame(p.getUniqueId());
        World dest = event.getTo().getWorld();
        if (playerGame != null) {
            if (playerGame.getGameID() == getGameInstance().getGameID()) {
                if (!dest.equals(getGameInstance().getMainWorld())
                        && !getGameInstance().getOtherWorlds().contains(dest.getUID())) {
                    getGameInstance().removePlayer(p.getUniqueId());
                }
            } else if (dest.equals(getGameInstance().getMainWorld())) {
                playerGame.removePlayer(p.getUniqueId());
            }
        }
        if (dest.equals(getGameInstance().getMainWorld())) {
            if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                getGameInstance().addPlayer(p.getUniqueId());
            } else {
                getGameInstance().addSpectator(p.getUniqueId());
            }
        }
    }

    //Respawn fast
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!uhc.getAllPlayers().contains(e.getEntity().getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            e.getEntity().spigot().respawn();
            int x = uhc.getCenterX();
            int z = uhc.getCenterZ();
            int y = uhc.getMainWorld().getHighestBlockYAt(x, z);
            e.getEntity().teleport(new Location(uhc.getMainWorld(), x, y, z));
        }, 1L);
        e.setDeathMessage("[" + ChatColor.RED + "DEATH" + ChatColor.RESET + "]" + e.getDeathMessage());
    }

    //Prevent hunger
    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {
        if (!uhc.getAllPlayers().contains(e.getEntity().getUniqueId())) {
            return;
        }
        e.setCancelled(true);
    }
}
