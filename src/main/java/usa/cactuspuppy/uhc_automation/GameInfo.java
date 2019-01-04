package usa.cactuspuppy.uhc_automation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class GameInfo implements Serializable {
    private String name;
    private String displayName;

}
