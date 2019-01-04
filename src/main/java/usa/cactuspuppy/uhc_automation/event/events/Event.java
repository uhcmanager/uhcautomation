package usa.cactuspuppy.uhc_automation.event.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usa.cactuspuppy.uhc_automation.GameInstance;

@AllArgsConstructor
public abstract class Event {
    @Getter private GameInstance gameInstance;
}
