package net.fameless.randomizerplugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    public static ItemStack buildItem(ItemStack itemStack, Component name, boolean hideAttributes, Component ...lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        if (hideAttributes) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        List<Component> lores = new ArrayList<>();
        for (Component c : lore) {
            lores.add(c.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        meta.lore(lores);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
