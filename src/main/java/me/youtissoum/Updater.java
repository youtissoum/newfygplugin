package me.youtissoum;

import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.net.HttpURLConnection.HTTP_OK;

class NoArtifactsException extends Exception {}

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
    private final String authorization;

    public Updater(Plugin plugin, File file, String AUTHOR, String REPOSITORY, String ARTIFACT, String authorization) {
        this.plugin = plugin;
        this.file = file;
        this.updateFolder = this.plugin.getServer().getUpdateFolderFile();

        this.AUTHOR = AUTHOR;
        this.REPOSITORY = REPOSITORY;
        this.ARTIFACT = ARTIFACT;
        this.authorization = Objects.requireNonNullElse(authorization, "");

    }

    public void update() {
        if(authorization == null || authorization.equalsIgnoreCase("")) {
            plugin.getLogger().severe("no GitHub authorization token given, cannot update");
            return;
        }

        final File folder = this.updateFolder;

        deleteOldFiles();
        if(!folder.exists()) {
            this.fileIOOrError(folder, folder.mkdir(), true);
        }
        downloadFile();

        final File dFile = new File(folder.getAbsolutePath(), this.file.getName());
        this.plugin.getLogger().warning(this.plugin.getName() + " has been updated, please restart the server");
    }

    private void downloadFile() {
        BufferedReader reader;
        String line;
        int artifactId = -1;

        try {
            artifactId = getArtifactId();
        } catch(IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Invalid URL");
            e.printStackTrace();
            return;
        } catch(NoArtifactsException e) {
            this.plugin.getLogger().severe("No artifacts found !");
            return;
        }

        ZipInputStream zipFile;

        try {
            URL url = new URL("https://api.github.com/repos/" + this.AUTHOR + "/" + this.REPOSITORY + "/actions/artifacts/" + artifactId + "/zip");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/vnd.github+json");
            con.setRequestProperty("User-Agent", "request");
            con.setRequestProperty("Authorization", "Bearer " + authorization);

            this.plugin.getLogger().info("Downloading update from : " + "https://api.github.com/repos/" + this.AUTHOR + "/" + this.REPOSITORY + "/actions/artifacts/" + artifactId + "/zip");

            con.connect();
            int status = con.getResponseCode();

            if (status == 429) {
                throw new IOException("rate limit has been reached, please wait a bit");
            } else if(status != HTTP_OK) { throw new IOException("GitHub did not send the correct status code : " + String.valueOf(status)); }

            zipFile = new ZipInputStream(con.getInputStream());
        } catch(IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Invalid URL");
            e.printStackTrace();
            return;
        }

        ZipEntry entry;
        try {
            while ((entry = zipFile.getNextEntry()) != null) {
                if(entry.getName().equals(file.getName())) {
                    FileOutputStream fout = new FileOutputStream(new File(this.updateFolder, file.getName()));

                    byte[] buffer = new byte[1024];

                    int len;
                    while((len = zipFile.read(buffer)) > 0) {
                        fout.write(buffer, 0, len);
                    }
                    return;
                }
            }
        } catch(IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not read github output");
        }

        plugin.getLogger().severe("not suitable file found in zip");
    }

    private int getArtifactId() throws IOException, NoArtifactsException {
        URL artifactsUrl = new URL("https://api.github.com/repos/" + this.AUTHOR + "/" + this.REPOSITORY + "/actions/artifacts");
        HttpURLConnection con = (HttpURLConnection) artifactsUrl.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/vnd.github+json");
        con.setRequestProperty("User-Agent", "request");
        con.setRequestProperty("Authorization", "Bearer " + authorization);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        con.disconnect();

        JSONObject artifactList = new JSONObject(response.toString());

        if(!artifactList.has("total_count") || artifactList.getInt("total_count") < 1) {
            this.plugin.getLogger().severe("No artifacts found !");
            throw new NoArtifactsException();
        }

        JSONArray artifacts = artifactList.getJSONArray("artifacts");

        for(int i=0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.getJSONObject(i);
            if(artifact.getString("name").equalsIgnoreCase(this.ARTIFACT)) {
                return artifact.getInt("id");
            }
        }

        throw new NoArtifactsException();
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