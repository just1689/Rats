package com.github.alexthe666.rats.server.block;

import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import com.github.alexthe666.rats.server.entity.rat.Rat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ColorParticleOption;

public class CursedGarbageBlock extends AbstractGarbageBlock {

	public static final MapCodec<CursedGarbageBlock> CODEC = simpleCodec(CursedGarbageBlock::new);

	@Override
	protected MapCodec<CursedGarbageBlock> codec() {
		return CODEC;
	}

	public CursedGarbageBlock(BlockBehaviour.Properties properties) {
		super(properties, 1.0D);
	}

	@Override
	protected EntityType<? extends PathfinderMob> getEntityToSpawn() {
		return RatsEntityRegistry.RAT.get();
	}

	@Override
	protected void postInitSpawn(PathfinderMob mob, RandomSource random) {
		((Rat) mob).setPlagued(true);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);
		if (random.nextInt(20) == 0) {
			BlockPos blockpos = pos.above();
			if (level.getBlockState(blockpos).isAir()) {
				double d0 = (double) pos.getX() + (double) random.nextFloat();
				double d1 = (double) pos.getY() + 1.05D;
				double d2 = (double) pos.getZ() + (double) random.nextFloat();
				level.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float) (0), (float) (random.nextGaussian() * 0.05D + 0.75D), (float) (0)), d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public int getDustColor(BlockState state, BlockGetter getter, BlockPos pos) {
		return 0x5E6323;
	}
}
