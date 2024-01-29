package net.fameless.randomizerplugin.randomizer;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.google.gson.JsonElement;
import net.fameless.randomizerplugin.DataFile;
import net.fameless.randomizerplugin.SettingsMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    public void onBlockDestroy(BlockDestroyEvent event) {
        if (!settingsMenu.isRandomizeBlockDrops()) return;
        event.setWillDrop(false);
        drop(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!settingsMenu.isRandomizeBlockDrops()) return;
        event.setDropItems(false);
        drop(event.getBlock());
    }

    public HashMap<Material, Material> getMaterialHashMap() {
        return materialHashMap;
    }

    private void drop(Block block) {
        if (!materialHashMap.containsKey(block.getType())) {
            Material newDrop = availableItems.get(random.nextInt(availableItems.size()));
            materialHashMap.put(block.getType(), newDrop);
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(newDrop));
            return;
        }
        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(materialHashMap.get(block.getType())));
    }
}
