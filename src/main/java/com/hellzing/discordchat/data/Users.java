package com.hellzing.discordchat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Users
{
    @Getter
    private static final File usersFile = new File(Config.getConfigFolder().getAbsolutePath() + File.separatorChar + "Users.json");

    @Getter
    private static Users instance;

    @Getter
    @Expose
    @SerializedName("Linked Users (Minecraft name : Discord ID)")
    private Map<String, String> linkedUsers = new HashMap<>();

    private static Users loadConfigFile() throws IOException
    {
        // Create users instance
        Users users = new Users();

        // Check if a config file exists
        if (!usersFile.exists())
        {
            // Create file within directory
            //noinspection ResultOfMethodCallIgnored
            usersFile.createNewFile();
        }

        // Parse the config
        val configJson = FileUtils.readFileToString(usersFile, "utf-8");
        val parsedConfig = Config.getGson().fromJson(configJson, Users.class);
        if (parsedConfig != null)
        {
            // Apply parsed config object
            users = parsedConfig;
        }

        // Apply instance
        return users;
    }

    public static void saveConfig()
    {
        try
        {
            // Save the config file
            FileUtils.writeStringToFile(usersFile, Config.getGson().toJson(instance, Users.class), "utf-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void reloadConfig() throws IOException
    {
        // Load the config file
        instance = loadConfigFile();
        saveConfig();
    }
}
