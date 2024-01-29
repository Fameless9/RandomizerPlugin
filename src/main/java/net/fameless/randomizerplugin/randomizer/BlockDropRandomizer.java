package net.fameless.randomizerplugin;

import com.google.gson.JsonElement;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockDropRandomizer implements Listener {

    private final List<Material> availableItems;
    private final HashMap<Material, Material> materialHashMap = new HashMap<>();
    private final Random random = new Random();
    private final SettingsMenu settingsMenu;

    public BlockDropRandomizer(SettingsMenu settingsMenu, List<Material> availableItems) {
        this.settingsMenu = settingsMenu;
        this.availableItems = availableItems;

        for (Map.Entry<String, JsonElement> entry : DataFile.getBlockDropMapObject().entrySet()) {
            materialHashMap.put(Material.valueOf(entry.getKey()), Material.valueOf(entry.getValue().getAsString()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!settingsMenu.isRandomizeBlockDrops()) return;
        event.setDropItems(false);
        if (!materialHashMap.containsKey(event.getBlock().getType())) {
            Material newDrop = availableItems.get(random.nextInt(availableItems.size()));
            materialHashMap.put(event.getBlock().getType(), newDrop);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(newDrop));
            return;
        }
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(materialHashMap.get(event.getBlock().getType())));
    }

    public HashMap<Material, Material> getMaterialHashMap() {
        return materialHashMap;
    }
}
