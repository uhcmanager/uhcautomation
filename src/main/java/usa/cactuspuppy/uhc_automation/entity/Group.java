package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import org.bukkit.entity.Player;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Group extends UniqueEntity {
    @Getter private Set<UUID> players = new HashSet<>();

    public Group(GameInstance gameInstance, UUID... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.asList(initPlayers));
    }

    public Group(GameInstance gameInstance, Player... initPlayers) {
        super(gameInstance);
        players.addAll(Arrays.stream(initPlayers).map(Player::getUniqueId).collect(Collectors.toList()));
    }
}
