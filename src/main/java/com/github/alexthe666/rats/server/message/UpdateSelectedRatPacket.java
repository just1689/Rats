package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.registry.RatsCapabilityRegistry;
import com.github.alexthe666.rats.server.capability.SelectedRat;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record UpdateSelectedRatPacket(int entityId, int ratId) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<UpdateSelectedRatPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "update_selected_rat"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSelectedRatPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), UpdateSelectedRatPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public UpdateSelectedRatPacket(Entity entity, SelectedRat cap) {
		this(entity.getId(), cap.getSelectedRat().getId());
	}

	public static UpdateSelectedRatPacket decode(RegistryFriendlyByteBuf buf) {
		return new UpdateSelectedRatPacket(buf.readInt(), buf.readInt());
	}

	public static void encode(UpdateSelectedRatPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.entityId());
		buf.writeInt(packet.ratId());
	}

	public static class Handler {

		@SuppressWarnings("Convert2Lambda")
		public static void handle(UpdateSelectedRatPacket packet, IPayloadContext context) {
			context.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Entity entity = Minecraft.getInstance().level.getEntity(packet.entityId());
					if (entity instanceof LivingEntity) {
						entity.getData(RatsCapabilityRegistry.SELECTED_RAT).setSelectedRat((TamedRat) Minecraft.getInstance().level.getEntity(packet.ratId()));
					}
				}
			});
		}
	}
}
