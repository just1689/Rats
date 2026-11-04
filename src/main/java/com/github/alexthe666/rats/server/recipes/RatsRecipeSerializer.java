package com.github.alexthe666.rats.server.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;

//keeps the 1.20 json layout: {"group": "", "ingredient": {...}, "result": "item_id", "count": 1}
public class RatsRecipeSerializer<T extends BaseRatRecipe> implements RecipeSerializer<T> {
	private final MapCodec<T> codec;
	private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

	public RatsRecipeSerializer(SingleItemMaker<T> factory) {
		this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(SingleItemRecipe::getGroup),
				Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.getIngredients().getFirst()),
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("result").forGetter(recipe -> recipe.getResult().getItem()),
				Codec.INT.optionalFieldOf("count", 1).forGetter(recipe -> recipe.getResult().getCount())
		).apply(instance, (group, ingredient, item, count) -> factory.create(group, ingredient, new ItemStack(item, count))));
		this.streamCodec = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, SingleItemRecipe::getGroup,
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.getIngredients().getFirst(),
				ItemStack.STREAM_CODEC, BaseRatRecipe::getResult,
				factory::create);
	}

	@Override
	public MapCodec<T> codec() {
		return this.codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
		return this.streamCodec;
	}

	public interface SingleItemMaker<T extends SingleItemRecipe> {
		T create(String group, Ingredient input, ItemStack output);
	}
}
