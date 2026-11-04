package com.github.alexthe666.rats.server.items.upgrades.interfaces;

import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemContainerContents;

public interface CombinedUpgrade {

	static NonNullList<ItemStack> getUpgrades(ItemStack stack, int size) {
		NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);
		stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);
		return list;
	}

	static void setUpgrades(ItemStack stack, List<ItemStack> upgrades) {
		stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(upgrades));
	}

	int getUpgradeSlots();

	default void addTooltip(ItemStack stack, List<Component> tooltip) {
		if (stack.has(DataComponents.CONTAINER)) {
			NonNullList<ItemStack> nonnulllist = CombinedUpgrade.getUpgrades(stack, this.getUpgradeSlots());
			int i = 0;
			for (ItemStack itemstack : nonnulllist) {
				if (!itemstack.isEmpty()) {
					if (i <= 4) {
						++i;
						tooltip.add(Component.literal(String.format("%s", itemstack.getDisplayName().getString())));
					} else {
						break;
					}
				}
			}
			if (nonnulllist.stream().filter(stack1 -> !stack1.isEmpty()).toList().size() > 5) {
				tooltip.add(Component.translatable(RatsLangConstants.AND_MORE, nonnulllist.stream().filter(stack1 -> !stack1.isEmpty()).toList().size() - 5).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
