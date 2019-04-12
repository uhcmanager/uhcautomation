package usa.cactuspuppy.uhc_automation.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usa.cactuspuppy.uhc_automation.game.GameInstance;
import usa.cactuspuppy.uhc_automation.game.GameStateEvent;
import usa.cactuspuppy.uhc_automation.utils.MiscUtils;

import java.util.List;

public class Reset implements UHCCommand {
    @Override
    public String getUsage() {
        return "/uhc reset [name/ID]";
    }

    @Override
    public String getPurpose() {
        return "Resets game to lobby";
    }

    @Override
    public String getMoreInfo() {
        return "Resets the specified game (or game whose lobby world the executor is in) to lobby mode. This command will reset the game no matter the current state of the game.";
    }

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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
