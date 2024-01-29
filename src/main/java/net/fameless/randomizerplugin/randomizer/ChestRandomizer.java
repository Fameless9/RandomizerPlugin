package net.fameless.randomizerplugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

public class ChestRandomizer implements Listener {

    private final double chance = RandomizerPlugin.getInstance().getConfig().getDouble("slot-contains-item-chance");
    private final int minAmount = RandomizerPlugin.getInstance().getConfig().getInt("chest-item-amount-min");
    private final int maxAmount = RandomizerPlugin.getInstance().getConfig().getInt("chest-item-amount-max");

    private final List<String> opened;
    private final List<String> playerPlacedChests;
    private final List<Material> availableItems;
    private final Random random = new Random();

    private final SettingsMenu settingsMenu;

    public ChestRandomizer(SettingsMenu settingsMenu, List<Material> availableItems) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();

        this.opened = gson.fromJson(DataFile.getOpenedChestsArray(), type);
        this.playerPlacedChests = gson.fromJson(DataFile.getPlacedBlocksArray(), type);
        this.availableItems = availableItems;
        this.settingsMenu = settingsMenu;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestOpen(InventoryOpenEvent event) {
        if (!settingsMenu.isRandomizeChests()) return;
        InventoryHolder holder = event.getInventory().getHolder();
        Location location;

        if (holder instanceof Chest) {
            location = ((Chest) holder).getLocation();
        } else if (holder instanceof StorageMinecart) {
            location = ((StorageMinecart) holder).getLocation();
        } else return;

        if (hasBeenOpened(location)) return;
        if (settingsMenu.isOnlyNaturallyGenerated() && isPlacedByPlayer(location)) return;

        Inventory chestInv = event.getInventory();
        chestInv.clear();

        for (int i = 0; i < chestInv.getSize(); i++) {
            double randomizedValue = random.nextDouble(0, 1);
            if (randomizedValue > (1 - chance)) {
                Material item = availableItems.get(random.nextInt(availableItems.size()));
                int max = Math.min(maxAmount, item.getMaxStackSize());
                int min = Math.max(minAmount, 1);
                int amount;

                if (max > 64) max = 64;
                if (min > 64) min = 64;

                if (max != min) {
                    if (min > max) {
                        amount = random.nextInt(max, min);
                    } else {
                        amount = random.nextInt(min, max);
                    }
                } else {
                    amount = minAmount;
                }

                chestInv.setItem(i, new ItemStack(item, amount));
            }
        }
        addOpened(location);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().equals(Material.CHEST) || event.getBlockPlaced().getType().equals(Material.CHEST_MINECART)) {
            addPlayerPlaced(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        removeOpened(event.getBlock().getLocation());
        removePlayerPlaced(event.getBlock().getLocation());
    }

    private boolean hasBeenOpened(Location location) {
        return opened.contains(location.toString());
    }

    private boolean isPlacedByPlayer(Location location) {
        return playerPlacedChests.contains(location.toString());
    }

    private void addPlayerPlaced(Location location) {
        playerPlacedChests.add(location.toString());
    }

    private void removePlayerPlaced(Location location) {
        playerPlacedChests.remove(location.toString());
    }

    private void addOpened(Location location) {
        opened.add(location.toString());
    }

    private void removeOpened(Location location) {
        opened.remove(location.toString());
    }

    public List<String> getOpened() {
        return opened;
    }

    public List<String> getPlayerPlacedChests() {
        return playerPlacedChests;
    }
}
