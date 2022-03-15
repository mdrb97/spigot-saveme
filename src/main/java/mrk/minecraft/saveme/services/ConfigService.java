package mrk.minecraft.saveme.services;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigService {

    private final JavaPlugin plugin;

    public ConfigService(JavaPlugin plugin){
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
    }

    public int getNumberOfMaxBackupsToKeep(){
        return plugin.getConfig().getInt("max-backups-to-keep", 10);
    }

    public long getSecondsBetweenBackups(){
        return plugin.getConfig().getLong("seconds-between-backups", 1800L);
    }
}
