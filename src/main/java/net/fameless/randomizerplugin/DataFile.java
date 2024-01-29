package net.fameless.randomizerplugin;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.*;

public class DataFile {

    private static File jsonFile;

    public static void init() throws IOException {
        jsonFile = new File(RandomizerPlugin.getInstance().getDataFolder(), "data.json");
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
            JsonObject root = new JsonObject();
            root.addProperty("Author", "Fameless9 (https://github.com/Fameless9)");
            saveJsonFile(root);
        }
    }

    public static JsonObject getRootObject() {
        try {
            return JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().severe("[ChestRandomizer] Failed to read settings file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(RandomizerPlugin.getInstance());
        }
        return new JsonObject();
    }

    public static JsonObject getBlockDropMapObject() {
        if (getRootObject().has("blockDropMap")) {
            return getRootObject().getAsJsonObject("blockDropMap");
        }
        return new JsonObject();
    }

    public static JsonObject getMobDropMap() {
        if (getRootObject().has("mobDropMap")) {
            return getRootObject().getAsJsonObject("mobDropMap");
        }
        return new JsonObject();
    }

    public static JsonObject getRecipeMap() {
        if (getRootObject().has("recipeMap")) {
            return getRootObject().getAsJsonObject("recipeMap");
        }
        return new JsonObject();
    }

    public static JsonArray getPlacedBlocksArray() {
        if (getRootObject().has("placedBlocks")) {
            return getRootObject().getAsJsonArray("placedBlocks");
        }
        return new JsonArray();
    }

    public static JsonArray getOpenedChestsArray() {
        if (getRootObject().has("openedChests")) {
            return getRootObject().getAsJsonArray("openedChests");
        }
        return new JsonArray();
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
            Bukkit.getLogger().severe("[ChestRandomizer] Failed to save settings file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(RandomizerPlugin.getInstance());
        }
    }
}
