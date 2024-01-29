package net.fameless.randomizerplugin.randomizer;

import com.google.gson.JsonElement;
import net.fameless.randomizerplugin.DataFile;
import net.fameless.randomizerplugin.SettingsMenu;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MobDropRandomizer implements Listener {

    private final List<Material> availableItems;
    private final HashMap<EntityType, Material> mobHashMap = new HashMap<>();
    private final Random random = new Random();
    private final SettingsMenu settingsMenu;

    public MobDropRandomizer(SettingsMenu settingsMenu, List<Material> availableItems) {
        this.availableItems = availableItems;
        this.settingsMenu = settingsMenu;

        for (Map.Entry<String, JsonElement> entry : DataFile.getMobDropMap().entrySet()) {
            mobHashMap.put(EntityType.valueOf(entry.getKey()), Material.valueOf(entry.getValue().getAsString()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!settingsMenu.isRandomizeMobDrops()) return;
        event.getDrops().clear();
        if (!mobHashMap.containsKey(event.getEntityType())) {
            Material newDrop = availableItems.get(random.nextInt(availableItems.size()));
            mobHashMap.put(event.getEntityType(), newDrop);
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(newDrop));
            return;
        }
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(mobHashMap.get(event.getEntity().getType())));
    }

    public HashMap<EntityType, Material> getMobHashMap() {
        return mobHashMap;
    }
}
