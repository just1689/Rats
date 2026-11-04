package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatsArmorMaterialRegistry {
	public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, RatsMod.MODID);
	//durability is no longer part of the armor material, it now lives on the item properties
	private static final Map<ResourceLocation, Integer> DURABILITY_MULTIPLIERS = new HashMap<>();

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PIPER_HAT = register("piper_hat", 25, 1, 1, 1, 2, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(RatsBlockRegistry.PIED_WOOL.get())));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CHEF_TOQUE = register("chef_toque", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Blocks.WHITE_WOOL)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> PLAGUE_MASK = register("plague_mask", 25, 1, 1, 1, 3, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 0.0F, () -> Ingredient.of(new ItemStack(RatsItemRegistry.PLAGUE_ESSENCE.get())));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RATLANTIS = register("ratlantis", 40, 5, 8, 10, 5, 17, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.1F, () -> Ingredient.of(new ItemStack(RatlantisItemRegistry.ORATCHALCUM_INGOT.get())));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FARMER_HAT = register("farmer_hat", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.HAY_BLOCK)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TOP_HAT = register("top_hat", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.BLACK_WOOL)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> FEZ = register("fez", 0, 1, 1, 1, 1, 100, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.RED_WOOL)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SANTA_HAT = register("santa_hat", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.RED_WOOL)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> HALO = register("halo", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.GOLD_NUGGET)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CROWN = register("crown", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.GOLD_INGOT)));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GHOST_HAT = register("ghost_hat", 0, 1, 1, 1, 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(RatlantisItemRegistry.GHOST_PIRAT_ECTOPLASM.get())));
	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> GENERIC_HAT = register("generic_hat", 0, 1, 1, 1, 1, 100, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F, () -> Ingredient.of(new ItemStack(Items.LEATHER)));

	private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, int durability, int boots, int leggings, int chestplate, int helmet, int enchantability, Holder<net.minecraft.sounds.SoundEvent> sound, float toughness, float knockback, java.util.function.Supplier<Ingredient> repairMaterial) {
		DURABILITY_MULTIPLIERS.put(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, name), durability);
		return MATERIALS.register(name, () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
			map.put(ArmorItem.Type.BOOTS, boots);
			map.put(ArmorItem.Type.LEGGINGS, leggings);
			map.put(ArmorItem.Type.CHESTPLATE, chestplate);
			map.put(ArmorItem.Type.HELMET, helmet);
			map.put(ArmorItem.Type.BODY, chestplate);
		}), enchantability, sound, repairMaterial, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, name))), toughness, knockback));
	}

	public static int getDurability(Holder<ArmorMaterial> material, ArmorItem.Type type) {
		int multiplier = material.unwrapKey().map(key -> DURABILITY_MULTIPLIERS.getOrDefault(key.location(), 0)).orElse(0);
		return type.getDurability(multiplier);
	}

	public static String getName(Holder<ArmorMaterial> material) {
		return material.unwrapKey().map(key -> key.location().getPath()).orElse("");
	}
}
