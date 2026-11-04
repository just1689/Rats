package com.github.alexthe666.rats.client.util;

import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

public class RatRecordSoundInstance extends AbstractTickableSoundInstance {

	private final TamedRat rat;
	private final Item record;

	public RatRecordSoundInstance(TamedRat rat, Item record, JukeboxSong song) {
		super(song.soundEvent().value(), SoundSource.RECORDS, RandomSource.create());
		this.attenuation = Attenuation.LINEAR;
		this.looping = false;
		this.rat = rat;
		this.record = record;
	}

	@Override
	public void tick() {
		if (!this.rat.isRemoved() && RatUpgradeUtils.hasUpgrade(this.rat, RatsItemRegistry.RAT_UPGRADE_DJ.get()) && this.rat.getMainHandItem().is(this.record)) {
			this.x = this.rat.getX();
			this.y = this.rat.getY();
			this.z = this.rat.getZ();
			this.volume = 5.0F;
		} else {
			Minecraft.getInstance().getSoundManager().stop(this);
		}
	}
}
