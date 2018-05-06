package usa.cactuspuppy.uhc_automation;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    protected GameInstance gi;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initSQL();
        gi = new GameInstance(this);
        this.getCommand("uhcstart").setExecutor(new CommandStart(this));
        Bukkit.getServer().getPluginManager().registerEvents(new WorldChangeListener(), this);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    private void initSQL() {

    }
}
