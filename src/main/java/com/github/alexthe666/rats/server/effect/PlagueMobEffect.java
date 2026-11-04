package com.github.alexthe666.rats.server.effect;

import com.github.alexthe666.rats.registry.RatsEffectRegistry;
import com.github.alexthe666.rats.registry.RatsSoundRegistry;
import com.github.alexthe666.rats.server.message.RatsNetworkHandler;
import com.github.alexthe666.rats.server.message.SyncPlaguePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlagueMobEffect extends MobEffect {

	public PlagueMobEffect() {
		super(MobEffectCategory.HARMFUL, 0x445637);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration > 0;
	}

	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public void onEffectStarted(LivingEntity entity, int amplifier) {
		MobEffectInstance effect = entity.getEffect(RatsEffectRegistry.PLAGUE);
		if (!entity.isRemoved() && effect != null && entity.level() instanceof ServerLevel) {
			entity.playSound(RatsSoundRegistry.PLAGUE_SPREAD.get(), 1.0F, 1.0F);
			PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncPlaguePacket(entity.getId(), effect));
		}
		super.onEffectStarted(entity, amplifier);
	}

	//effects no longer know when they're removed, this is called from MobEffectEvent.Remove and MobEffectEvent.Expired
	public static void onEffectRemoved(LivingEntity entity) {
		if (!entity.isRemoved() && entity.level() instanceof ServerLevel) {
			PacketDistributor.sendToPlayersTrackingEntity(entity, new SyncPlaguePacket(entity.getId(), new MobEffectInstance(RatsEffectRegistry.PLAGUE, 0)));
		}
	}
}
