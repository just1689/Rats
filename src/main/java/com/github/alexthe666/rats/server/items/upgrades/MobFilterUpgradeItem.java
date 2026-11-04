package com.github.alexthe666.rats.server.items.upgrades;

import com.github.alexthe666.rats.client.events.ModClientEvents;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import com.github.alexthe666.rats.server.misc.StackData;

public class MobFilterUpgradeItem extends BaseRatUpgradeItem {
	public MobFilterUpgradeItem(Properties properties) {
		super(properties, 1, 2);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide() && player.getItemInHand(hand).is(this)) {
			ModClientEvents.openMobFilterScreen(hand);
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}
		return super.use(level, player, hand);
	}

	public static boolean isWhitelist(ItemStack stack) {
		return StackData.has(stack) && StackData.getOrEmpty(stack).getBoolean("Whitelist");
	}

	public static void setWhitelist(ItemStack stack, boolean whitelist) {
		StackData.update(stack, tag -> tag.putBoolean("Whitelist", whitelist));
	}

	public static List<String> getSelectedMobs(ItemStack stack) {
		if (!StackData.has(stack)) return new ArrayList<>();
		List<String> mobs = new ArrayList<>();
		ListTag tag = StackData.getOrEmpty(stack).getList("Mobs", Tag.TAG_STRING);
		for (int i = 0; i < tag.size(); ++i) {
			String s = tag.getString(i);
			mobs.add(s);
		}
		return mobs;
	}

	public static void setMobs(ItemStack stack, List<String> mobs) {
		ListTag list = new ListTag();
		for (String mob : mobs) {
			if (BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(mob))) {
				list.add(StringTag.valueOf(mob));
			}
		}
		StackData.update(stack, tag -> tag.put("Mobs", list));
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable(RatsLangConstants.MOB_FILTER_MODE, Component.translatable(isWhitelist(stack) ? RatsLangConstants.MOB_FILTER_WHITELIST : RatsLangConstants.MOB_FILTER_BLACKLIST)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(RatsLangConstants.MOB_FILTER_SELECTED_MOBS).withStyle(ChatFormatting.GRAY));
		if (!getSelectedMobs(stack).isEmpty()) {
			List<String> mobs = getSelectedMobs(stack);
			for (int i = 0; i < mobs.size(); i++) {
				if (i < 3) {
					tooltip.add(CommonComponents.space().append(Component.literal(mobs.get(i)).withStyle(ChatFormatting.GRAY)));
				} else {
					break;
				}
			}
			if (mobs.size() > 3) {
				tooltip.add(CommonComponents.space().append(Component.translatable(RatsLangConstants.AND_MORE, mobs.size() - 3).withStyle(ChatFormatting.GRAY)));
			}
		}
	}
}
