package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import com.github.alexthe666.rats.registry.RatsDataComponentRegistry;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class OreRatNuggetItem extends Item {

	public OreRatNuggetItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		level.playSound(null, player.getX(), player.getY(), player.getZ(), RatsSoundRegistry.RAT_NUGGET_ORE.get(), SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
		ItemStack poopStack = getStoredItem(itemstack, new ItemStack(Items.IRON_INGOT));
		if (!player.getInventory().add(poopStack)) {
			player.drop(poopStack, false);
		}
		if (!player.isCreative()) {
			itemstack.shrink(1);
		}
		return InteractionResultHolder.success(itemstack);
	}

	public static ItemStack getStoredItem(ItemStack poopItem, ItemStack fallback) {
		ItemContainerContents contents = poopItem.get(RatsDataComponentRegistry.ORE_ITEM.get());
		if (contents != null) {
			ItemStack oreItem = contents.copyOne();
			if (oreItem.isEmpty()) {
				return fallback;
			} else {
				return oreItem;
			}
		}
		return fallback;
	}

	public static ItemStack getIngot(Level level, ItemStack stack) {
		SmeltingRecipe recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).map(RecipeHolder::value).orElse(null);
		if (recipe != null && !recipe.getResultItem(level.registryAccess()).isEmpty()) {
			return recipe.getResultItem(level.registryAccess()).copy();
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack saveResourceToNugget(ItemStack resource) {
		ItemStack stack = new ItemStack(RatsItemRegistry.RAT_NUGGET_ORE.get());
		stack.set(RatsDataComponentRegistry.ORE_ITEM.get(), ItemContainerContents.fromItems(List.of(resource.copy())));
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
		if (flag.isCreative()) {
			ItemStack ingot = getStoredItem(stack, new ItemStack(Items.AIR));
			tooltip.add(Component.translatable(RatsLangConstants.ORE_NUGGET_CONTAINS, ingot.getDisplayName().getString()).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable("item.rats.rat_nugget_ore.desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}
	}
}
