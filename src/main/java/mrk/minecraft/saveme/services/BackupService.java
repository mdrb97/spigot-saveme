package mrk.minecraft.saveme.services;

import mrk.minecraft.saveme.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BackupService {
    private final String NAME_TAG = "{file_name}";
    private final JavaPlugin plugin;
    private boolean isBackingUp = false;

    public BackupService(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void doBackups() {
        List<World> worlds = Bukkit.getServer().getWorlds();
        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
            LocalDateTime now = LocalDateTime.now();
            String fileNameTemplate = makeTaggedString(NAME_TAG, dtf.format(now));
            File currentWorkingDirectory = new File(Paths.get(".").toAbsolutePath().normalize().toString());
            if(isBackingUp){
                Bukkit.getLogger().info("Already running a backup. Skipping for now.");
                return;
            }
            isBackingUp = true;
            List<File> worldFolders = new ArrayList<>();
            for(World world : worlds){
                world.setAutoSave(false);
                Path worldPath = Paths.get(currentWorkingDirectory.toURI()).relativize(Paths.get(world.getWorldFolder().toURI()));
                worldFolders.add(worldPath.toFile());
            }
            if(!((Main)plugin)
                    .getFileSystemService()
                    .saveWorlds(worldFolders, processTaggedString(fileNameTemplate, NAME_TAG, "worlds")))
                Bukkit.getLogger().info("Failed to save world backups");
            else
                Bukkit.getLogger().info("Saved world backups");

        }catch (Exception ex){
            Bukkit.getLogger().warning("Exception detected on doBackups " + ex.getMessage());
        }finally {
            for(World world : worlds) {
                world.setAutoSave(true);
            }
            isBackingUp = false;
        }

    }

    private String makeTaggedString(String tag, String value){
        return tag + value;
    }

    private String processTaggedString(String taggedString, String tag, String value){
        return taggedString.replace(tag, value);
    }
}
