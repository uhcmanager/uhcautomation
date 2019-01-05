package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Group {
    @Getter private Set<UUID> players = new HashSet<>();

    public Group(UUID... initPlayers) {
        players.addAll(Arrays.asList(initPlayers));
    }

    public Group(Player... initPlayers) {
        players.addAll(Arrays.stream(initPlayers).map(Player::getUniqueId).collect(Collectors.toList()));
    }
}
