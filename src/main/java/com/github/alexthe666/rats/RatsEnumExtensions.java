package com.github.alexthe666.rats;

import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Enum values Rats adds to vanilla enums. These are referenced from META-INF/enumextensions.json.
 */
public class RatsEnumExtensions {
	public static final EnumProxy<Rarity> RATLANTIS_SPECIAL = new EnumProxy<>(Rarity.class, -1, "rats:ratlantis_special", (UnaryOperator<Style>) style -> style.withColor(ChatFormatting.GREEN));
	public static final EnumProxy<MobCategory> RATS = new EnumProxy<>(MobCategory.class, "rats:rats", 25, true, false, 128);
	public static final EnumProxy<Raid.RaiderType> PIPER = new EnumProxy<>(Raid.RaiderType.class, (Supplier<EntityType<? extends Raider>>) () -> RatsEntityRegistry.PIED_PIPER.get(), new int[]{0, 0, 1, 0, 0, 1, 1, 2});
}
