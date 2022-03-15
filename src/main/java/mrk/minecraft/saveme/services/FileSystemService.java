package mrk.minecraft.saveme.services;

import mrk.minecraft.saveme.Main;
import net.lingala.zip4j.model.ExcludeFileFilter;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileFilter;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.lingala.zip4j.ZipFile;

public class FileSystemService {

    private final JavaPlugin plugin;

    public FileSystemService(JavaPlugin plugin) throws Exception {
        this.plugin = plugin;
        tryCreateFolder();
    }

    private void tryCreateFolder() throws Exception {
        try {
            File folder = plugin.getDataFolder();
            if (!folder.exists() && !folder.mkdirs()) {
                throw new Exception("Couldn't create plugin folder.");
            }
            if (!folder.canWrite() || !folder.canRead()) {
                throw new Exception("Plugin folder is created but has no permissions.");
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private Path getFolderPath() {
        return Paths.get(plugin.getDataFolder().getAbsolutePath());
    }

    public boolean saveWorlds(List<File> worldsToSave, String fileName) {
        int maxBackupsToKeep = ((Main) plugin).getConfigService().getNumberOfMaxBackupsToKeep();
        File[] worlds = getFolderPath().toFile().listFiles(x -> x.getName().contains(".zip"));
        if (worlds != null && worlds.length >= maxBackupsToKeep) {
            Arrays.sort(worlds, Comparator.comparingLong(File::lastModified));
            if(!worlds[0].delete()){
                Bukkit.getLogger().warning(ChatColor.RED + "Couldn't delete file: " + worlds[0].getName());
            }
            else{
                Bukkit.getLogger().info("File " + worlds[0].getName() + " deleted.");
            }
        }
        return saveZipFiles(worldsToSave, fileName);
    }

    private boolean saveZipFiles(List<File> files, String fileName) {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
            ZipFile zipFile = new ZipFile(Paths.get(getFolderPath().toString(), fileName + ".zip").toFile());
            ExcludeFileFilter excludeFileFilter = f -> f.getName().equals("session.lock");
            zipParameters.setExcludeFileFilter(excludeFileFilter);
            for (File fileToSave : files) {
                zipFile.addFolder(fileToSave, zipParameters);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to saveFile: " + fileName + " " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean saveZipFile(File fileToSave, String fileName) {
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionLevel(CompressionLevel.ULTRA);
            ZipFile zipFile = new ZipFile(Paths.get(getFolderPath().toString(), fileName + ".zip").toFile());
            zipFile.addFolder(fileToSave, zipParameters);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to saveFile: " + fileName + " " + e.getMessage());
            return false;
        }
        return true;
    }

}
