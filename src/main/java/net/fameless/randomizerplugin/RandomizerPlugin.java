package net.fameless.randomizerplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fameless.randomizerplugin.randomizer.BlockDropRandomizer;
import net.fameless.randomizerplugin.randomizer.ChestRandomizer;
import net.fameless.randomizerplugin.randomizer.CraftingRandomizer;
import net.fameless.randomizerplugin.randomizer.MobDropRandomizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RandomizerPlugin extends JavaPlugin {

    private static RandomizerPlugin instance;

    private ChestRandomizer chestRandomizer;
    private BlockDropRandomizer blockDropRandomizer;
    private MobDropRandomizer mobDropRandomizer;
    private CraftingRandomizer craftingRandomizer;
    private SettingsMenu settingsMenu;
    private final List<Material> availableItems = new ArrayList<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        initAvailableItems();

        try {
            DataFile.init();
        } catch (IOException e) {
            getLogger().severe("[ChestRandomizer] Failed to init Settings file. Shutting down.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        settingsMenu = new SettingsMenu();
        chestRandomizer = new ChestRandomizer(settingsMenu, availableItems);
        blockDropRandomizer = new BlockDropRandomizer(settingsMenu, availableItems);
        mobDropRandomizer = new MobDropRandomizer(settingsMenu, availableItems);
        craftingRandomizer = new CraftingRandomizer(settingsMenu, availableItems);

        getServer().getPluginManager().registerEvents(settingsMenu, this);
        getServer().getPluginManager().registerEvents(chestRandomizer, this);
        getServer().getPluginManager().registerEvents(blockDropRandomizer, this);
        getServer().getPluginManager().registerEvents(mobDropRandomizer, this);
        getServer().getPluginManager().registerEvents(craftingRandomizer, this);

        getCommand("settings").setExecutor(settingsMenu);
    }

    @Override
    public void onDisable() {
        JsonObject root = DataFile.getRootObject();
        JsonObject settings = DataFile.getSettingsObject();
        JsonArray placedBlocks = new JsonArray();
        JsonArray openedChests = new JsonArray();
        JsonObject blockDropMap = new JsonObject();
        JsonObject mobDropMap = new JsonObject();
        JsonObject craftingDropMap = new JsonObject();

        settings.addProperty("chests", settingsMenu.isRandomizeChests());
        settings.addProperty("blocks", settingsMenu.isRandomizeBlockDrops());
        settings.addProperty("mobs", settingsMenu.isRandomizeMobDrops());
        settings.addProperty("crafting", settingsMenu.isRandomizeCrafting());
        settings.addProperty("onlyNatural", settingsMenu.isOnlyNaturallyGenerated());

        for (String location : chestRandomizer.getPlayerPlacedChests()) {
            placedBlocks.add(location);
        }
        for (String location : chestRandomizer.getOpened()) {
            openedChests.add(location);
        }
        for (Map.Entry<Material, Material> entry : blockDropRandomizer.getMaterialHashMap().entrySet()) {
            blockDropMap.addProperty(entry.getKey().name(), entry.getValue().name());
        }
        for (Map.Entry<EntityType, Material> entry : mobDropRandomizer.getMobHashMap().entrySet()) {
            mobDropMap.addProperty(entry.getKey().name(), entry.getValue().name());
        }
        for (Map.Entry<Material, Material> entry : craftingRandomizer.getMaterialHashMap().entrySet()) {
            craftingDropMap.addProperty(entry.getKey().name(), entry.getValue().name());
        }

        root.add("settings", settings);
        root.add("placedBlocks", placedBlocks);
        root.add("openedChests", openedChests);
        root.add("blockDropMap", blockDropMap);
        root.add("mobDropMap", mobDropMap);
        root.add("recipeMap", craftingDropMap);
        DataFile.saveJsonFile(root);
    }

    private void initAvailableItems() {
        List<String> toExclude = getConfig().getStringList("exclude");
        for (Material material : Material.values()) {
            if (toExclude.contains(material.name())) continue;
            if (material.name().endsWith("CANDLE_CAKE")) continue;
            if (material.name().startsWith("POTTED")) continue;
            if (material.name().contains("WALL") && material.name().contains("TORCH")) continue;
            if (material.name().contains("WALL") && material.name().contains("SIGN")) continue;
            if (material.name().contains("WALL") && material.name().contains("HEAD")) continue;
            if (material.name().contains("WALL") && material.name().contains("CORAL")) continue;
            if (material.name().contains("WALL") && material.name().contains("BANNER")) continue;
            if (material.name().contains("WALL") && material.name().contains("SKULL")) continue;
            if (material.name().endsWith("PLANT") && !material.name().startsWith("CHORUS")) continue;
            if (material.name().endsWith("STEM")) continue;
            availableItems.add(material);
        }
    }

    public static RandomizerPlugin getInstance() {
        return instance;
    }
}
