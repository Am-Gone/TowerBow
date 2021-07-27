package fr.Hygon.TowerBow.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ItemsList {
    GAPPLE(Material.GOLDEN_APPLE, Component.text("Golden Apple")
            .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false), () -> {
        ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta gappleMeta = gapple.getItemMeta();
        gappleMeta.displayName(Component.text("Golden Apple")
                .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false));
        gapple.setItemMeta(gappleMeta);

        return gapple;
    }),
    COBBLESTONE(Material.COBBLESTONE, Component.text("Blocks")
            .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false), () -> {
        ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 32);
        ItemMeta cobblestoneMeta = cobblestone.getItemMeta();
        cobblestoneMeta.displayName(Component.text("Blocks")
                .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false));
        cobblestone.setItemMeta(cobblestoneMeta);

        return cobblestone;
    }),
    BOW(Material.BOW, Component.text("Bow")
            .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false), () -> {
        ItemStack bow = new ItemStack(Material.BOW, 1);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.displayName(Component.text("§6Bow")
                .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false));
        bowMeta.setUnbreakable(true);
        bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
        bow.setItemMeta(bowMeta);

        return bow;
    }),
    PICKAXE(Material.STONE_PICKAXE, Component.text("Stone Pickaxe")
            .color(TextColor.color(250, 170, 0)).decoration(TextDecoration.ITALIC, false), () -> {
        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE, 1);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setDisplayName("§6Stone Pickaxe");
        pickaxeMeta.setUnbreakable(true);
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, 2, false);
        pickaxeMeta.addEnchant(Enchantment.DAMAGE_ALL, 3, false);
        pickaxeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier("generic.attackSpeed", 10, AttributeModifier.Operation.ADD_NUMBER));
        pickaxeMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attackDamage", 1, AttributeModifier.Operation.ADD_NUMBER));
        pickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pickaxe.setItemMeta(pickaxeMeta);

        return pickaxe;
    }),
    IRON_HELMET(Material.IRON_HELMET, Component.text(""), () -> {
        ItemStack ironHelmet = new ItemStack(Material.IRON_HELMET);
        ItemMeta ironHelmetMeta = ironHelmet.getItemMeta();
        ironHelmetMeta.displayName(Component.text(""));
        ironHelmetMeta.setUnbreakable(true);
        ironHelmetMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        ironHelmet.setItemMeta(ironHelmetMeta);

        return ironHelmet;
    }),
    IRON_CHEST(Material.IRON_CHESTPLATE, Component.text(""), () -> {
        ItemStack ironChest = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta ironChestMeta = ironChest.getItemMeta();
        ironChestMeta.displayName(Component.text(""));
        ironChestMeta.setUnbreakable(true);
        ironChestMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        ironChest.setItemMeta(ironChestMeta);

        return ironChest;
    }),
    IRON_LEGGINGS(Material.IRON_LEGGINGS, Component.text(""), () -> {
        ItemStack ironLegs = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta ironLegsMeta = ironLegs.getItemMeta();
        ironLegsMeta.displayName(Component.text(""));
        ironLegsMeta.setUnbreakable(true);
        ironLegsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        ironLegs.setItemMeta(ironLegsMeta);

        return ironLegs;
    }),
    IRON_BOOTS(Material.IRON_BOOTS, Component.text(""), () -> {
        ItemStack ironBoots = new ItemStack(Material.IRON_BOOTS);
        ItemMeta ironBootsMeta = ironBoots.getItemMeta();
        ironBootsMeta.displayName(Component.text(""));
        ironBootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 2, true);
        ironBootsMeta.setUnbreakable(true);
        ironBootsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        ironBoots.setItemMeta(ironBootsMeta);

        return ironBoots;
    });

    private final Material material;
    private final Component name;
    private final ItemsCode itemsCode;

    ItemsList(Material material, Component name, ItemsCode itemsCode) {
        this.material = material;
        this.name = name;
        this.itemsCode = itemsCode;
    }

    public Material getMaterial() {
        return material;
    }

    public Component getName() {
        return name;
    }

    public ItemStack getPreparedItemStack() {
        return itemsCode.getItemStack();
    }
}
