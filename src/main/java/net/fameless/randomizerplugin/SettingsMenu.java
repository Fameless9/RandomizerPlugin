package net.fameless.randomizerplugin;

import net.fameless.randomizerplugin.util.Head;
import net.fameless.randomizerplugin.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingsMenu implements CommandExecutor, Listener, InventoryHolder {

    public SettingsMenu() {
        this.randomizeChests = DataFile.getSettingsObject().has("chests") && DataFile.getSettingsObject().get("chests").getAsBoolean();
        this.randomizeBlockDrops = DataFile.getSettingsObject().has("blocks") && DataFile.getSettingsObject().get("blocks").getAsBoolean();
        this.randomizeMobDrops = DataFile.getSettingsObject().has("mobs") && DataFile.getSettingsObject().get("mobs").getAsBoolean();
        this.randomizeCrafting = DataFile.getSettingsObject().has("crafting") && DataFile.getSettingsObject().get("crafting").getAsBoolean();
        this.onlyNaturallyGenerated = !DataFile.getSettingsObject().has("onlyNatural") || DataFile.getSettingsObject().get("onlyNatural").getAsBoolean();
    }

    private boolean randomizeChests;
    private boolean randomizeBlockDrops;
    private boolean randomizeMobDrops;
    private boolean randomizeCrafting;
    private boolean onlyNaturallyGenerated;

    public boolean isRandomizeChests() {
        return randomizeChests;
    }

    public void setRandomizeChests(boolean randomizeChests) {
        this.randomizeChests = randomizeChests;
    }

    public boolean isRandomizeBlockDrops() {
        return randomizeBlockDrops;
    }

    public void setRandomizeBlockDrops(boolean randomizeBlockDrops) {
        this.randomizeBlockDrops = randomizeBlockDrops;
    }

    public boolean isRandomizeMobDrops() {
        return randomizeMobDrops;
    }

    public void setRandomizeMobDrops(boolean randomizeMobDrops) {
        this.randomizeMobDrops = randomizeMobDrops;
    }

    public boolean isRandomizeCrafting() {
        return randomizeCrafting;
    }

    public void setRandomizeCrafting(boolean randomizeCrafting) {
        this.randomizeCrafting = randomizeCrafting;
    }

    public boolean isOnlyNaturallyGenerated() {
        return onlyNaturallyGenerated;
    }

    public void setOnlyNaturallyGenerated(boolean onlyNaturallyGenerated) {
        this.onlyNaturallyGenerated = onlyNaturallyGenerated;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!sender.hasPermission("randomizer.settings")) {
            sender.sendMessage(Component.text("Lacking permission: randomizer.settings", NamedTextColor.RED));
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players may use this command.", NamedTextColor.RED));
            return false;
        }
        ((Player) sender).openInventory(settingsInventory());
        return true;
    }

    private Inventory settingsInventory() {
        Inventory inventory = Bukkit.getServer().createInventory(this, 18, Component.text(
                "Settings", NamedTextColor.BLUE, TextDecoration.BOLD));

        inventory.setItem(0, ItemBuilder.buildItem(new ItemStack(Material.CHEST), Component.text("Randomize Chests", NamedTextColor.BLUE),
                true, Component.text("Randomize chest contents", NamedTextColor.GRAY),
                (isRandomizeChests() ? Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD))));
        inventory.setItem(1, ItemBuilder.buildItem(new ItemStack(Material.OAK_LOG), Component.text("Randomize Block Drops", NamedTextColor.BLUE),
                true, Component.text("Randomize drops from blocks", NamedTextColor.GRAY),
                (isRandomizeBlockDrops() ? Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD))));
        inventory.setItem(2, ItemBuilder.buildItem(new ItemStack(Material.SHEEP_SPAWN_EGG), Component.text("Randomize Mob Drops",
                        NamedTextColor.BLUE), true, Component.text("Randomize drops from mobs", NamedTextColor.GRAY),
                (isRandomizeMobDrops() ? Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD))));
        inventory.setItem(3, ItemBuilder.buildItem(new ItemStack(Material.CRAFTING_TABLE), Component.text("Randomize Crafting", NamedTextColor.BLUE),
                true, Component.text("Randomize items that are formed by recipes", NamedTextColor.GRAY),
                (isRandomizeCrafting() ? Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD))));
        inventory.setItem(9, ItemBuilder.buildItem(new ItemStack(Material.PLAYER_HEAD), Component.text("Only Naturally Generated", NamedTextColor.BLUE),
                true, Component.text("Only randomize chests that were not placed by players", NamedTextColor.GRAY),
                (isOnlyNaturallyGenerated() ? Component.text("ENABLED", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD))));
        inventory.setItem(17, ItemBuilder.buildItem(Head.INFO.getAsItemStack(), Component.text("INFO", NamedTextColor.BLUE, TextDecoration.BOLD),
                true, Component.text("Author: ", NamedTextColor.GRAY).append(Component.text("Fameless9", NamedTextColor.BLUE)),
                Component.text("Version: ", NamedTextColor.GRAY).append(Component.text(RandomizerPlugin.getInstance().getDescription().getVersion(), NamedTextColor.BLUE))));

        return inventory;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsMenu)) return;
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 0: {
                setRandomizeChests(!isRandomizeChests());
                Bukkit.broadcast(Component.text("Randomize Chests", NamedTextColor.BLUE).append(Component.text(" has been ", NamedTextColor.GRAY).append(
                        (isRandomizeChests() ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED)).append(
                                Component.text(".", NamedTextColor.GRAY)))));
                break;
            }
            case 1: {
                setRandomizeBlockDrops(!isRandomizeBlockDrops());
                Bukkit.broadcast(Component.text("Randomize Block Drops", NamedTextColor.BLUE).append(Component.text(" has been ", NamedTextColor.GRAY).append(
                        (isRandomizeBlockDrops() ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED)).append(
                                Component.text(".", NamedTextColor.GRAY)))));
                break;
            }
            case 2: {
                setRandomizeMobDrops(!isRandomizeMobDrops());
                Bukkit.broadcast(Component.text("Randomize Mob Drops", NamedTextColor.BLUE).append(Component.text(" has been ", NamedTextColor.GRAY).append(
                        (isRandomizeMobDrops() ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED)).append(
                                Component.text(".", NamedTextColor.GRAY)))));
                break;
            }
            case 3: {
                setRandomizeCrafting(!isRandomizeCrafting());
                Bukkit.broadcast(Component.text("Randomize Crafting", NamedTextColor.BLUE).append(Component.text(" has been ", NamedTextColor.GRAY).append(
                        (isRandomizeCrafting() ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED)).append(
                                Component.text(".", NamedTextColor.GRAY)))));
                break;
            }
            case 9: {
                setOnlyNaturallyGenerated(!isOnlyNaturallyGenerated());
                break;
            }
        }
        event.getWhoClicked().openInventory(settingsInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return settingsInventory();
    }
}
