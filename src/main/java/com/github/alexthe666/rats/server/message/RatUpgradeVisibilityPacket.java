package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RatUpgradeVisibilityPacket(int ratId, EquipmentSlot slot, boolean visible) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<RatUpgradeVisibilityPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "rat_upgrade_visibility"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RatUpgradeVisibilityPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), RatUpgradeVisibilityPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static RatUpgradeVisibilityPacket decode(RegistryFriendlyByteBuf buf) {
		return new RatUpgradeVisibilityPacket(buf.readInt(), EquipmentSlot.byName(buf.readUtf().toLowerCase(Locale.ROOT)), buf.readBoolean());
	}

	public static void encode(RatUpgradeVisibilityPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.ratId());
		buf.writeUtf(packet.slot().name());
		buf.writeBoolean(packet.visible());
	}

	public static class Handler {
		public static void handle(RatUpgradeVisibilityPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null) {
					Entity entity = player.level().getEntity(packet.ratId());
					if (entity instanceof TamedRat rat) {
						rat.setSlotVisibility(packet.slot(), packet.visible());
					}
				}
			});
		}
	}
}
