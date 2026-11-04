package com.github.alexthe666.rats.data;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsBiomeModifierRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.github.alexthe666.rats.registry.RatsBannerPatternRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import net.minecraft.core.registries.Registries;

public class BiomeModifierGenerator extends DatapackBuiltinEntriesProvider {

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, RatsBiomeModifierRegistry::bootstrap)
			.add(Registries.BANNER_PATTERN, RatsBannerPatternRegistry::bootstrap)
			.add(Registries.JUKEBOX_SONG, RatsSoundRegistry::bootstrapSongs);


	public BiomeModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, BUILDER, Set.of(RatsMod.MODID));
	}
}
