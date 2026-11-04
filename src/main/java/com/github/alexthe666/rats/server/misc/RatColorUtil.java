package com.github.alexthe666.rats.server.misc;

import com.google.common.collect.Maps;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.FastColor;

public class RatColorUtil {

	private static final Map<DyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap((DyeColor color) -> color, RatColorUtil::createRatColor)));

	public static float[] getDyeRgb(DyeColor color) {
		return DYE_TO_RGB.get(color);
	}

	private static float[] createRatColor(DyeColor color) {
		if (color == DyeColor.WHITE) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			int rgb = color.getTextureDiffuseColor();
			return new float[]{FastColor.ARGB32.red(rgb) / 255.0F * 0.75F, FastColor.ARGB32.green(rgb) / 255.0F * 0.75F, FastColor.ARGB32.blue(rgb) / 255.0F * 0.75F};
		}
	}
}
