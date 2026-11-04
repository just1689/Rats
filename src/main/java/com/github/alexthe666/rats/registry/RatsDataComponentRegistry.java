package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RatsDataComponentRegistry {
	public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(RatsMod.MODID);

	//the resource stored inside an ore rat nugget
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> ORE_ITEM = COMPONENTS.registerComponentType("ore_item",
			builder -> builder.persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).cacheEncoding());
}
