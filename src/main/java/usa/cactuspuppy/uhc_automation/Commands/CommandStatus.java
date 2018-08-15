package usa.cactuspuppy.uhc_automation.Commands;

import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.Main;

@NoArgsConstructor
public class CommandStatus {
    public static void onCommand(CommandSender commandSender) {
        Main.getInstance().getGameInstance().logStatus(commandSender);
    }
}
