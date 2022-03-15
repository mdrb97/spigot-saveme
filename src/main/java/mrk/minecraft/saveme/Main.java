package mrk.minecraft.saveme;

import mrk.minecraft.saveme.services.BackupService;
import mrk.minecraft.saveme.services.ConfigService;
import mrk.minecraft.saveme.services.FileSystemService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
    private BackupService _backupService;
    private FileSystemService _fileSystemService;
    private ConfigService _configService;

    @Override
    public void onEnable(){
        try {
            _configService = new ConfigService(this);
            _fileSystemService = new FileSystemService(this);
            _backupService = new BackupService(this);
        } catch (Exception e) {
            Bukkit.getLogger().warning(ChatColor.RED + "SaveMe FATAL ERROR: " + e.getMessage());
            return;
        }

        Bukkit.getLogger().info(ChatColor.GREEN + "SaveMe loaded up!");
        Bukkit.getLogger().info(ChatColor.GREEN
                + "SaveMe will be using the following configs: Max backups to keep: " + _configService.getNumberOfMaxBackupsToKeep()
                + " Seconds between backups: " + _configService.getSecondsBetweenBackups());

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, () -> {
            getBackupService().doBackups();
        }, getSeconds(10L), getSeconds(_configService.getSecondsBetweenBackups()));
    }

    private long getSeconds(long seconds){
        return 20L * seconds;
    }

    @Override
    public void onDisable(){
        Bukkit.getLogger().info(ChatColor.RED + "SaveMe shutting down...");
    }

    public BackupService getBackupService(){
        return _backupService;
    }

    public FileSystemService getFileSystemService(){
        return _fileSystemService;
    }
    public ConfigService getConfigService(){ return _configService; }
}
