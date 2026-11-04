package com.github.alexthe666.rats.server.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Vanilla's projectile dispense behavior now requires the item to implement ProjectileItem.
 * This keeps the old behavior: create a projectile and shoot it out of the dispenser.
 */
public abstract class SimpleProjectileDispenseBehavior extends DefaultDispenseItemBehavior {

	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Level level = source.level();
		Position position = DispenserBlock.getDispensePosition(source);
		Direction direction = source.state().getValue(DispenserBlock.FACING);
		Projectile projectile = this.getProjectile(level, position, stack);
		projectile.setPos(position.x(), position.y(), position.z());
		projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
		level.addFreshEntity(projectile);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(BlockSource source) {
		source.level().levelEvent(1002, source.pos(), 0);
	}

	protected abstract Projectile getProjectile(Level level, Position position, ItemStack stack);

	protected float getUncertainty() {
		return 6.0F;
	}

	protected float getPower() {
		return 1.1F;
	}
}
