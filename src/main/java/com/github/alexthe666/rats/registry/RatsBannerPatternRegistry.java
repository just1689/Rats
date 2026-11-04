package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

//banner patterns are datapack registry entries since 1.20.5, they're generated through datagen
public class RatsBannerPatternRegistry {

	public static final ResourceKey<BannerPattern> RAT_PATTERN = create("rat");
	public static final ResourceKey<BannerPattern> CHEESE_PATTERN = create("cheese");
	public static final ResourceKey<BannerPattern> RAT_AND_CROSSBONES_BANNER = create("rat_and_crossbones");
	public static final ResourceKey<BannerPattern> RAT_AND_SICKLE_BANNER = create("rat_and_sickle");

	private static ResourceKey<BannerPattern> create(String name) {
		return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, name));
	}

	public static void bootstrap(BootstrapContext<BannerPattern> context) {
		register(context, RAT_PATTERN);
		register(context, CHEESE_PATTERN);
		register(context, RAT_AND_CROSSBONES_BANNER);
		register(context, RAT_AND_SICKLE_BANNER);
	}

	private static void register(BootstrapContext<BannerPattern> context, ResourceKey<BannerPattern> key) {
		context.register(key, new BannerPattern(key.location(), "block.minecraft.banner." + key.location().toShortLanguageKey()));
	}
}
