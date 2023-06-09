package me.youtissoum;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A class that automatically updates your plugin using github artifacts
 *
 * @author youtissoum
 */
public class Updater {
    private final String AUTHOR;
    private final String REPOSITORY;
    private final String ARTIFACT;

    private final Plugin plugin;
    private final File file;
    private final File updateFolder;

    public Updater(Plugin plugin, File file, String AUTHOR, String REPOSITORY, String ARTIFACT) {
        this.plugin = plugin;
        this.file = file;
        this.updateFolder = this.plugin.getServer().getUpdateFolderFile();

        this.AUTHOR = AUTHOR;
        this.REPOSITORY = REPOSITORY;
        this.ARTIFACT = ARTIFACT;
    }

    public void update() throws IOException {
        final File folder = this.updateFolder;

        deleteOldFiles();
        if(!folder.exists()) {
            this.fileIOOrError(folder, folder.mkdir(), true);
        }

        downloadUpdate();

        final File dFile = new File(folder.getAbsolutePath(), this.file.getName());
        this.plugin.getLogger().warning(this.plugin.getName() + " has been updated, please restart the server");
    }

    private void downloadUpdate() throws IOException {
        URL url = new URL("https://nightly.link/" + this.AUTHOR + "/" + this.REPOSITORY + "/workflows/build/main/" + this.ARTIFACT + ".zip");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        this.plugin.getLogger().info("Downloading update from : " + url.toString());

        con.connect();
        int status = con.getResponseCode();

        if (status == 429) {
            throw new IOException("rate limit has been reached, please wait a bit");
        } else if(status != HTTP_OK) { throw new IOException("Status code other than OK received : " + String.valueOf(status)); }

        ZipInputStream zipFile = new ZipInputStream(con.getInputStream());

        ZipEntry entry;
        while ((entry = zipFile.getNextEntry()) != null) {
            if (entry.getName().equals(file.getName())) {
                FileOutputStream fout = new FileOutputStream(new File(this.updateFolder, file.getName()));

                byte[] buffer = new byte[1024];

                int len;
                while ((len = zipFile.read(buffer)) > 0) {
                    fout.write(buffer, 0, len);
                }
                return;
            }
        }
    }

    private void deleteOldFiles() {
        //Just a quick check to make sure we didn't leave any files from last time...
        File[] list = listFilesOrError(this.updateFolder);
        for (final File xFile : list) {
            if (xFile.getName().endsWith(".zip")) {
                this.fileIOOrError(xFile, xFile.mkdir(), true);
            }
        }
    }

    private File[] listFilesOrError(File folder) {
        File[] contents = folder.listFiles();
        if (contents == null) {
            this.plugin.getLogger().severe("The updater could not access files at: " + this.updateFolder.getAbsolutePath());
            return new File[0];
        } else {
            return contents;
        }
    }

    private void fileIOOrError(File file, boolean result, boolean create) {
        if(!result) {
            this.plugin.getLogger().severe("The updater could not " + (create ? "create" : "delete") + " file at: " + file.getAbsolutePath());
        }
    }
}