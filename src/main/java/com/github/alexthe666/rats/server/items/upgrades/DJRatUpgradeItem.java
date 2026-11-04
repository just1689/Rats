package com.github.alexthe666.rats.server.items.upgrades;

import com.github.alexthe666.rats.client.model.entity.RatModel;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.EquipmentListenerUpgrade;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.HoldsItemUpgrade;
import com.github.alexthe666.rats.server.items.upgrades.interfaces.TickRatUpgrade;
import com.github.alexthe666.rats.server.message.RatsNetworkHandler;
import com.github.alexthe666.rats.server.message.UpdateRatMusicPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.core.component.DataComponents;
import net.neoforged.neoforge.network.PacketDistributor;

public class DJRatUpgradeItem extends BaseRatUpgradeItem implements HoldsItemUpgrade, TickRatUpgrade, EquipmentListenerUpgrade {

	public DJRatUpgradeItem(Properties properties) {
		super(properties, 1, 1);
	}

	@Override
	public boolean shouldDepositItem(TamedRat rat, ItemStack stack) {
		return !stack.has(DataComponents.JUKEBOX_PLAYABLE);
	}

	@Override
	public void renderHeldItem(EntityRendererProvider.Context context, TamedRat rat, RatModel<?> model, PoseStack stack, MultiBufferSource buffer, int light, float ageInTicks) {
		model.translateToBody(stack);
		stack.scale(-0.35F, -0.35F, 0.35F);
		stack.translate(-0.5F, -0.5F, -0.65F);
		context.getBlockRenderDispatcher().renderSingleBlock(Blocks.JUKEBOX.defaultBlockState(), stack, buffer, light, OverlayTexture.NO_OVERLAY);
	}

	@Override
	public boolean isFakeHandRender() {
		return true;
	}

	@Override
	public void onItemChanged(TamedRat rat, EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
		if (!rat.level().isClientSide() && slot == EquipmentSlot.MAINHAND) {
			if (newStack.has(DataComponents.JUKEBOX_PLAYABLE)) {
				PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) rat.level(), new ChunkPos(rat.blockPosition()), new UpdateRatMusicPacket(rat.getId(), newStack.copyWithCount(1)));
			}
		}
	}

	@Override
	public void tick(TamedRat rat) {
		if (rat.getMainHandItem().has(DataComponents.JUKEBOX_PLAYABLE) && rat.level().isClientSide()) {
			if (rat.tickCount % 10 == 0) {
				rat.level().addParticle(ParticleTypes.NOTE, rat.getX(), rat.getEyeY() + 0.25D, rat.getZ(), rat.getRandom().nextInt(4) / 24.0F, 0.0F, 0.0F);
			}
		}
	}
}
