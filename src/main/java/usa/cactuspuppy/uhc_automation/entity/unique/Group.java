package usa.cactuspuppy.uhc_automation.entity.unique;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.event.game.group.*;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Group extends UniqueEntity {
    private Set<UUID> players = new HashSet<>();
    @Getter private UHCTeam team;
    @Getter @Setter private int num;

    public Group(GameInstance gameInstance, UUID... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.asList(initPlayers));
        Bukkit.getServer().getPluginManager().callEvent(new GroupCreateEvent(gameInstance, this));
    }

    public Group(GameInstance gameInstance, Player... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.stream(initPlayers).map(Player::getUniqueId).collect(Collectors.toList()));
        Bukkit.getServer().getPluginManager().callEvent(new GroupCreateEvent(gameInstance, this));
    }

    public int size() {
        return players.size();
    }

    public static void merge(Group g1, Group g2) {
        assert g1.getGameInstance().equals(g2.getGameInstance());
        Group eater = (g2.size() > g1.size() ? g2 : g1);
        Group eatee = (g2.size() > g1.size() ? g1 : g2);
        eater.addPlayers(eatee.getPlayers().toArray(new UUID[0]));
        eatee.removeAllPlayers();
        eatee.delete();
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(players);
    }

    public void addPlayers(UUID... players) {
        this.players.addAll(Arrays.asList(players));
        Bukkit.getServer().getPluginManager().callEvent(new GroupAddPlayersEvent(getGameInstance(), this, players));
    }

    public void removePlayers(UUID... players) {
        this.players.removeAll(Arrays.asList(players));
        Bukkit.getServer().getPluginManager().callEvent(new GroupRemovePlayersEvent(getGameInstance(), this, players));
    }

    public void removeAllPlayers() { removePlayers(players.toArray(new UUID[0])); }

    public void setTeam(UHCTeam t) {
        team.removeGroups(this);
        team = t;
        team.addGroups(this);
        Bukkit.getServer().getPluginManager().callEvent(new GroupSetTeamEvent(getGameInstance(), this, team));
    }

    public void delete() {
        UniqueEntity.removeEntity(this);
        team.removeGroups(this);
        for (UUID u : players) new Group(getGameInstance(), u);
        Bukkit.getServer().getPluginManager().callEvent(new GroupDeleteEvent(getGameInstance(), this));
    }
}
