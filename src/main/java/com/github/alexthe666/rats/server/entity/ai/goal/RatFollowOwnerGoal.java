package com.github.alexthe666.rats.server.entity.ai.goal;

import com.github.alexthe666.rats.server.entity.RatMount;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;

public class RatFollowOwnerGoal extends FollowOwnerGoal {
	private final TamedRat rat;
	private final double speedModifier;
	private final float startDistance;
	private final float stopDistance;
	private int timeToRecalcPath;

	public RatFollowOwnerGoal(TamedRat rat, double speedModifier, float startDist, float stopDist) {
		super(rat, speedModifier, startDist, stopDist);
		this.rat = rat;
		this.speedModifier = speedModifier;
		this.startDistance = startDist;
		this.stopDistance = stopDist;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	//vanilla doesnt let tamed mobs follow their owner while riding something, but rats can ride mounts
	@Override
	public boolean canUse() {
		LivingEntity owner = this.rat.getOwner();
		if (owner == null || this.unableToMove() || this.rat.distanceToSqr(owner) < (double) (this.startDistance * this.startDistance)) {
			return false;
		}
		this.owner = owner;
		return this.rat.canMove() && this.rat.isFollowing();
	}

	@Override
	public boolean canContinueToUse() {
		return !this.rat.getNavigation().isDone() && !this.unableToMove() && this.rat.distanceToSqr(this.owner) > (double) (this.stopDistance * this.stopDistance);
	}

	public boolean unableToMove() {
		return this.rat.isOrderedToSit() || this.rat.getVehicle() instanceof Player || this.rat.isLeashed();
	}

	@Override
	public void start() {
		super.start();
		this.rat.getNavigation().stop();
		this.timeToRecalcPath = 0;
	}

	@Override
	public void tick() {
		this.rat.getLookControl().setLookAt(this.owner, 10.0F, (float) this.rat.getMaxHeadXRot());
		if (this.rat.isFollowing()) {
			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = this.adjustedTickDelay(10);
				if (this.rat.distanceToSqr(this.owner) >= (this.rat.hasFlightUpgrade() ? 800.0D : 400.0D)) {
					if (!this.maybeTeleportMount()) {
						this.rat.tryToTeleportToOwner();
					}
				} else {
					if (this.rat.hasFlightUpgrade()) {
						this.rat.getNavigation().moveTo(this.owner.getX(), this.owner.getY() + 2.5D, this.owner.getZ(), this.speedModifier);
						this.rat.setFlying(true);
					} else {
						this.rat.getNavigation().moveTo(this.owner, this.speedModifier);
					}
				}
			}
		}
	}

	private boolean maybeTeleportMount() {
		if (this.rat.getVehicle() instanceof RatMount mount && mount.shouldTeleportWhenFarAway()) {
			BlockPos blockpos = this.owner.blockPosition();

			for (int i = 0; i < 10; ++i) {
				int j = this.randomIntInclusive(-3, 3);
				int k = this.randomIntInclusive(-1, 1);
				int l = this.randomIntInclusive(-3, 3);
				boolean flag = this.attemptTeleportEntity(this.rat.getVehicle(), blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
				if (flag) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean attemptTeleportEntity(Entity mount, int x, int y, int z) {
		if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
			return false;
		} else if (!this.rat.canTeleportTo(new BlockPos(x, y, z))) {
			return false;
		} else {
			mount.moveTo((double) x + 0.5D, y, (double) z + 0.5D, this.rat.getYRot(), this.rat.getXRot());
			if (mount instanceof Mob mob) mob.getNavigation().stop();
			this.rat.getNavigation().stop();
			return true;
		}
	}

	private int randomIntInclusive(int min, int max) {
		return this.rat.getRandom().nextInt(max - min + 1) + min;
	}
}
