package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.event.EventDistributor;
import usa.cactuspuppy.uhc_automation.event.game.group.GroupCreateEvent;
import usa.cactuspuppy.uhc_automation.event.game.group.GroupDeleteEvent;
import usa.cactuspuppy.uhc_automation.event.game.group.GroupMergeEvent;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Group extends UniqueEntity {
    @Getter private Set<UUID> players = new HashSet<>();
    @Getter @Setter private Team team;
    @Getter @Setter private int num;

    public Group(GameInstance gameInstance, UUID... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.asList(initPlayers));
        EventDistributor.distributeEvent(new GroupCreateEvent(gameInstance, this));
    }

    public Group(GameInstance gameInstance, Player... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.stream(initPlayers).map(Player::getUniqueId).collect(Collectors.toList()));
        EventDistributor.distributeEvent(new GroupCreateEvent(gameInstance, this));
    }

    public int size() {
        return players.size();
    }

    public static void merge(Group g1, Group g2) {
        assert g1.getGameInstance().equals(g2.getGameInstance());
        Group eater = (g2.size() > g1.size() ? g2 : g1);
        Group eatee = (g2.size() > g1.size() ? g1 : g2);
        eater.addPlayers(eatee.getPlayers().toArray(new UUID[0]));
        eatee.removePlayers(eatee.getPlayers().toArray(new UUID[0]));
        EventDistributor.distributeEvent(new GroupMergeEvent(g1.getGameInstance(), g1, g2));
        eatee.delete();
    }

    public void addPlayers(Player... players) {
        this.players.addAll(Arrays.stream(players).map(Player::getUniqueId).collect(Collectors.toList()));
    }

    public void addPlayers(UUID... players) {
        this.players.addAll(Arrays.asList(players));
    }

    public void removePlayers(Player... players) {
        this.players.removeAll(Arrays.stream(players).map(Player::getUniqueId).collect(Collectors.toList()));
    }

    public void removePlayers(UUID... players) {
        this.players.removeAll(Arrays.asList(players));
    }

    public void delete() {
        UniqueEntity.removeEntity(this);
        team.removeGroups(this);
        for (UUID u : players) new Group(getGameInstance(), u);
        EventDistributor.distributeEvent(new GroupDeleteEvent(getGameInstance(), this));
    }
}
