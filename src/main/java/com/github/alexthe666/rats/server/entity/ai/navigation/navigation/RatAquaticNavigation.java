package com.github.alexthe666.rats.server.entity.ai.navigation.navigation;

import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Navigation for rats with the 'Aquatic' upgrade. Paths may stop exploring once they are as long as the rat's follow range (12 blocks),
 * which isn't enough to walk from a deposit chest, dive into a pool and swim to its far side or bottom. Let paths cover the rat's work area instead (up to {@link #MAX_RANGE} blocks).
 */
public class RatAquaticNavigation extends AmphibiousPathNavigation {
	//keeps path searches cheap for rats with a very large work radius
	private static final int MAX_RANGE = 48;
	private final TamedRat rat;

	public RatAquaticNavigation(TamedRat rat, Level level) {
		super(rat, level);
		this.rat = rat;
		//swimming paths search the whole water volume, so they need more nodes than walking ones
		this.setMaxVisitedNodesMultiplier(4.0F);
	}

	@Nullable
	@Override
	protected Path createPath(Set<BlockPos> targets, int regionOffset, boolean offsetUpward, int accuracy, float followRange) {
		return super.createPath(targets, regionOffset, offsetUpward, accuracy, Math.max(followRange, Math.min(this.rat.getRadius() * 2 + 8, MAX_RANGE)));
	}
}
