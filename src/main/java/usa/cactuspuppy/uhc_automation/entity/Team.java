package usa.cactuspuppy.uhc_automation.entity;

import lombok.Getter;
import usa.cactuspuppy.uhc_automation.game.GameInstance;

import java.util.ArrayList;
import java.util.List;

public class Team extends UniqueEntity {
    @Getter private List<Group> groups;

    public Team(GameInstance gameInstance) {
        super(gameInstance);
        groups = new ArrayList<>();
    }

    public Team(GameInstance gameInstance, List<Group> groups) {
        super(gameInstance);
        this.groups = groups;
    }
}
