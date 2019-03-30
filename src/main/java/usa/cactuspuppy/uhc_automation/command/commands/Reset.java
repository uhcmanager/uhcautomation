package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.CommandSender;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;

public class Reset extends UHCCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String alias, String[] args) {
        //Get correct game instance
        MiscUtils.GetInstanceResult result = MiscUtils.getGameInstance(commandSender, args);
        if (result == null) {
            return true;
        } else if (!result.isUsageCorrect()) {
            return false;
        }
        GameInstance instance = result.getInstance();

        instance.updateState(GameStateEvent.RESET);
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender, String alias, String[] args) {
        return false;
    }
}
