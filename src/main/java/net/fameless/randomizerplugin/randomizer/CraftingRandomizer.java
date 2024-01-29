package net.fameless.randomizerplugin.randomizer;

import com.google.gson.JsonElement;
import net.fameless.randomizerplugin.DataFile;
import net.fameless.randomizerplugin.SettingsMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CraftingRandomizer implements Listener {

    private final List<Material> availableItems;
    private final HashMap<Material, Material> recipeHashMap = new HashMap<>();
    private final Random random = new Random();
    private final SettingsMenu settingsMenu;

    public CraftingRandomizer(SettingsMenu settingsMenu, List<Material> availableItems) {
        this.settingsMenu = settingsMenu;
        this.availableItems = availableItems;

        for (Map.Entry<String, JsonElement> entry : DataFile.getRecipeMap().entrySet()) {
            recipeHashMap.put(Material.valueOf(entry.getKey()), Material.valueOf(entry.getValue().getAsString()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!settingsMenu.isRandomizeCrafting()) return;
        if (event.isRepair()) return;
        if (event.getRecipe() == null) return;
        if (!recipeHashMap.containsKey(event.getRecipe().getResult().getType())) {
            Material newDrop = availableItems.get(random.nextInt(availableItems.size()));
            recipeHashMap.put(event.getRecipe().getResult().getType(), newDrop);
            event.getInventory().setResult(new ItemStack(newDrop));
            return;
        }
        event.getInventory().setResult(new ItemStack(recipeHashMap.get(event.getRecipe().getResult().getType())));
    }

    public HashMap<Material, Material> getMaterialHashMap() {
        return recipeHashMap;
    }
}
