package com.github.alexthe666.rats.server.world;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class PiratTreeGrower {
	//keys are created here instead of referencing RatlantisConfiguredFeatureRegistry, its static init needs blocks that arent registered yet
	public static final TreeGrower PIRAT = new TreeGrower("rats:pirat",
			Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "large_ghost_pirat_tree"))),
			Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "ghost_pirat_tree"))),
			Optional.empty());
}
