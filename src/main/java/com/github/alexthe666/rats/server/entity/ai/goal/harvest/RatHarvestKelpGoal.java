package com.github.alexthe666.rats.server.entity.ai.goal.harvest;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.github.alexthe666.rats.server.misc.RatUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Cuts kelp stems that have stopped growing. The rat breaks the second block from the bottom, so everything above it drops
 * and the bottom block is left behind to regrow (it becomes a new kelp tip with a fresh random height limit).
 */
public class RatHarvestKelpGoal extends BaseRatHarvestGoal {
	//give up on a stem the rat can't reach instead of trying forever
	private static final int MAX_TICKS_PER_TARGET = 400;

	private final TamedRat rat;
	private int ticksOnTarget;

	public RatHarvestKelpGoal(TamedRat rat) {
		super(rat);
		this.rat = rat;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (!RatUpgradeUtils.hasUpgrade(this.rat, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get())) return false;
		if (!super.canUse() || !this.checkTheBasics(this.rat.getDepositPos().isPresent(), this.rat.getDepositPos().isPresent())) {
			return false;
		}
		this.resetTarget();
		return this.getTargetBlock() != null;
	}

	private void resetTarget() {
		List<BlockPos> allBlocks = new ArrayList<>();
		int radius = this.rat.getRadius();
		BlockPos center = this.rat.getSearchCenter();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
			BlockPos cut = getCutPos(this.rat.level(), pos);
			if (cut != null && RatUtils.canRatBreakBlock(this.rat.level(), cut, this.rat)) {
				allBlocks.add(cut);
			}
		}
		if (!allBlocks.isEmpty()) {
			allBlocks.sort(this.getTargetSorter());
			this.setTargetBlock(allBlocks.get(0));
		}
	}

	@Override
	public void start() {
		this.ticksOnTarget = 0;
	}

	@Override
	public boolean canContinueToUse() {
		return this.checkTheBasics(false, false) && this.getTargetBlock() != null && this.rat.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
				&& RatUpgradeUtils.hasUpgrade(this.rat, RatsItemRegistry.RAT_UPGRADE_AQUATIC.get());
	}

	@Override
	public void tick() {
		BlockPos target = this.getTargetBlock();
		if (target == null) return;
		if (++this.ticksOnTarget > MAX_TICKS_PER_TARGET || !target.equals(getCutPos(this.rat.level(), target.below()))) {
			this.setTargetBlock(null);
			this.stop();
			return;
		}
		this.rat.getNavigation().moveTo(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, 1.25D);
		double distance = this.rat.getRatDistanceCenterSq(target.getX(), target.getY(), target.getZ());
		if (distance < this.rat.getRatHarvestDistance(0.0D)) {
			//the blocks above lose their support and drop as items too
			this.rat.level().destroyBlock(target, true);
			this.setTargetBlock(null);
			this.stop();
		}
	}

	/**
	 * If {@code bottom} is the bottom block of a kelp stem that is ready to harvest, returns the block to cut (the one above it), otherwise null.
	 * A stem is ready once it has stopped growing: its tip has reached its maximum age, or there is no water above the tip.
	 * It must also be at least 2 blocks tall so the bottom block can be left behind.
	 */
	@Nullable
	public static BlockPos getCutPos(Level level, BlockPos bottom) {
		if (!isKelp(level.getBlockState(bottom)) || isKelp(level.getBlockState(bottom.below()))) return null;
		BlockPos cut = bottom.above();
		if (!isKelp(level.getBlockState(cut))) return null;

		BlockPos tip = cut;
		while (isKelp(level.getBlockState(tip.above()))) {
			tip = tip.above();
		}
		BlockState tipState = level.getBlockState(tip);
		boolean maxAge = !tipState.is(Blocks.KELP) || tipState.getValue(GrowingPlantHeadBlock.AGE) >= GrowingPlantHeadBlock.MAX_AGE;
		boolean blockedAbove = !level.getBlockState(tip.above()).is(Blocks.WATER);
		return maxAge || blockedAbove ? cut.immutable() : null;
	}

	private static boolean isKelp(BlockState state) {
		return state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT);
	}
}
