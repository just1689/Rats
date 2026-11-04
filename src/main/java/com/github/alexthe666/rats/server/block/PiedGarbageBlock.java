package com.github.alexthe666.rats.server.block;

import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

public class PiedGarbageBlock extends AbstractGarbageBlock {

	public static final MapCodec<PiedGarbageBlock> CODEC = simpleCodec(PiedGarbageBlock::new);

	@Override
	protected MapCodec<PiedGarbageBlock> codec() {
		return CODEC;
	}

	public PiedGarbageBlock(BlockBehaviour.Properties properties) {
		super(properties, 1.0D);
	}

	@Override
	protected EntityType<? extends PathfinderMob> getEntityToSpawn() {
		return RatsEntityRegistry.PIED_PIPER.get();
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter getter, BlockPos pos) {
		return 0x4B4035;
	}
}
