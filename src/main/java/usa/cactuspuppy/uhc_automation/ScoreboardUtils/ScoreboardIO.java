package usa.cactuspuppy.uhc_automation.ScoreboardUtils;

import org.bukkit.scoreboard.Scoreboard;
import usa.cactuspuppy.uhc_automation.Main;

import java.io.*;
import java.util.Optional;

public class ScoreboardIO {
    public static boolean saveScoreboardToFile() {
        Scoreboard scoreboard = Main.getInstance().getGameInstance().getScoreboard();
        String outLoc = Main.getInstance().getDataFolder() + "/scoreboard.dat";
        File outFile = new File(outLoc);
        if (outFile.isFile() && !outFile.delete()) {
            Main.getInstance().getLogger().severe("Could not override existing scoreboard data at " + outFile.getPath());
            return false;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(outLoc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(new ScoreboardWrapper(scoreboard));
            out.close();
            fileOut.close();
            Main.getInstance().getLogger().info("Scoreboard data saved to " + outLoc);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Failed to save scoreboard data!");
            return false;
        }
        return true;
    }

    public static Optional<Scoreboard> readScoreboardFromFile() {
        String inLoc = Main.getInstance().getDataFolder() + "/scoreboard.dat";
        Scoreboard scoreboard = null;
        try {
            FileInputStream fileIn = new FileInputStream(inLoc);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            scoreboard = (Scoreboard) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Could not load scoreboard data from " + inLoc);
            return Optional.empty();
        } catch (ClassNotFoundException e) {
            Main.getInstance().getLogger().severe("Did not find expected scoreboard class at " + inLoc);
        }
        return Optional.ofNullable(scoreboard);
    }

    public static boolean scoreboardDataExists() {
        String sbLoc = Main.getInstance().getDataFolder() + "/scoreboard.dat";
        return (new File(sbLoc)).isFile();
    }
}
