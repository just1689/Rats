package com.github.alexthe666.rats.server.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Item stacks no longer carry an NBT tag in 1.21, Rats keeps its own item data inside the {@code minecraft:custom_data} component.
 * The vanilla datafixer moves 1.20 item tags into this component, so existing items keep their data.
 */
public final class StackData {

	private StackData() {
	}

	public static boolean has(ItemStack stack) {
		return stack.has(DataComponents.CUSTOM_DATA);
	}

	/**
	 * @return a copy of the stack's custom data, or null if it has none. Changes to the returned tag are not saved, use {@link #update} for that.
	 */
	@Nullable
	public static CompoundTag get(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? null : data.copyTag();
	}

	/**
	 * @return a copy of the stack's custom data, or an empty tag
	 */
	public static CompoundTag getOrEmpty(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static void set(ItemStack stack, @Nullable CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			stack.remove(DataComponents.CUSTOM_DATA);
		} else {
			CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
		}
	}

	public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
	}
}
