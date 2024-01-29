package net.fameless.randomizerplugin;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.*;

public class SettingsFile {

    private static final File jsonFile = new File(RandomizerPlugin.getInstance().getDataFolder(), "data.json");

    public static void init() throws IOException {
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject root = new JsonObject();
            JsonObject settingsObject = new JsonObject();
            if (!settingsObject.has("chests")) {
                settingsObject.addProperty("chests", false);
            }
            if (!settingsObject.has("blocks")) {
                settingsObject.addProperty("blocks", false);
            }
            if (!settingsObject.has("mobs")) {
                settingsObject.addProperty("mobs", false);
            }
            root.add("settings", settingsObject);
            saveJsonFile(root);
        }
    }

    public static JsonObject getRootObject() {
        try {
            return JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("[Randomizer] Failed to read settings file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(RandomizerPlugin.getInstance());
        }
        return new JsonObject();
    }

    public static JsonObject getSettingsObject() {
        if (getRootObject().has("settings")) {
            return getRootObject().getAsJsonObject("settings");
        }
        return new JsonObject();
    }

    public static void saveJsonFile(JsonObject rootObject) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(rootObject, writer);
        } catch (IOException e) {
            Bukkit.getLogger().severe("[Randomizer] Failed to save settings file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(RandomizerPlugin.getInstance());
        }
    }
}
