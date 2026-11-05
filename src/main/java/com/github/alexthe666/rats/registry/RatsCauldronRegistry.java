package com.github.alexthe666.rats.registry;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface RatsCauldronRegistry extends CauldronInteraction {

	CauldronInteraction.InteractionMap MILK = CauldronInteraction.newInteractionMap("rats_milk");
	CauldronInteraction.InteractionMap CHEESE = CauldronInteraction.newInteractionMap("rats_cheese");
	CauldronInteraction.InteractionMap BLUE_CHEESE = CauldronInteraction.newInteractionMap("rats_blue_cheese");
	CauldronInteraction.InteractionMap NETHER_CHEESE = CauldronInteraction.newInteractionMap("rats_nether_cheese");

	TagKey<Block> HERBS_AND_HARVEST_HEAT_SOURCES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("herbsandharvest", "heat_sources"));

	CauldronInteraction FILL_MILK = (state, level, pos, player, hand, stack) -> {
		if (!level.isClientSide()) {
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
			player.awardStat(Stats.USE_CAULDRON);
			level.setBlockAndUpdate(pos, RatsBlockRegistry.MILK_CAULDRON.get().defaultBlockState());
			level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
		}
		return ItemInteractionResult.sidedSuccess(level.isClientSide());
	};

	/**
	 * Registers the milk bucket -> milk cauldron interaction. This must run after every other mod's common setup,
	 * since other mods (such as Herbs and Harvest) also claim milk buckets on empty cauldrons and the last one to register wins.
	 * If another mod already claimed it, Rats only takes over for unheated cauldrons and hands heated ones back to that mod.
	 */
	static void registerMilkBucket() {
		CauldronInteraction previous = EMPTY.map().containsKey(Items.MILK_BUCKET) ? EMPTY.map().get(Items.MILK_BUCKET) : null;
		if (previous == null) {
			EMPTY.map().put(Items.MILK_BUCKET, FILL_MILK);
			return;
		}
		EMPTY.map().put(Items.MILK_BUCKET, (state, level, pos, player, hand, stack) -> {
			if (isHeated(level.getBlockState(pos.below()))) {
				return previous.interact(state, level, pos, player, hand, stack);
			}
			return FILL_MILK.interact(state, level, pos, player, hand, stack);
		});
	}

	static boolean isHeated(BlockState below) {
		return below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE) || below.is(Blocks.FIRE) || below.is(Blocks.SOUL_FIRE) ||
			below.is(Blocks.MAGMA_BLOCK) || below.is(HERBS_AND_HARVEST_HEAT_SOURCES);
	}

	static void init() {
		MILK.map().put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.MILK_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
				level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		});

		CHEESE.map().put(Items.SUGAR, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				level.setBlockAndUpdate(pos, RatsBlockRegistry.BLUE_CHEESE_CAULDRON.get().defaultBlockState());
				player.awardStat(Stats.USE_CAULDRON);
				if (!player.isCreative()) stack.shrink(1);
				level.playSound(null, pos, RatsSoundRegistry.BLUE_CHEESE_MADE.get(), SoundSource.BLOCKS);
				level.playSound(null, pos, RatsSoundRegistry.CHEESE_MADE.get(), SoundSource.BLOCKS, 1.0F, 0.75F);
				level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
			} else {
				for (int i = 0; i < 10; i++) {
					level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SUGAR)),
							pos.getX() + level.getRandom().nextFloat(),
							pos.getY() + 0.9375D,
							pos.getZ() + level.getRandom().nextFloat(),
							0.0D, level.getRandom().nextFloat() * 0.25D + 0.1F, 0.0D);
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		});

		CHEESE.map().put(Items.LAVA_BUCKET, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
				player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
				level.setBlockAndUpdate(pos, RatsBlockRegistry.NETHER_CHEESE_CAULDRON.get().defaultBlockState());
				player.awardStat(Stats.USE_CAULDRON);
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				level.playSound(null, pos, RatsSoundRegistry.NETHER_CHEESE_MADE.get(), SoundSource.BLOCKS);
				level.playSound(null, pos, RatsSoundRegistry.CHEESE_MADE.get(), SoundSource.BLOCKS, 1.0F, 0.5F);
				level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
			} else {
				for (int i = 0; i < 10; i++) {
					level.addParticle(ParticleTypes.LAVA,
							pos.getX() + level.getRandom().nextFloat(),
							pos.getY() + 0.9375D,
							pos.getZ() + level.getRandom().nextFloat(),
							level.getRandom().nextFloat(), level.getRandom().nextFloat(), level.getRandom().nextFloat());
				}
			}
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		});
	}
}
