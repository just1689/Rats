package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record RatCommandPacket(int ratId, int newCommand) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<RatCommandPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "rat_command"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RatCommandPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), RatCommandPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static RatCommandPacket decode(RegistryFriendlyByteBuf buf) {
		return new RatCommandPacket(buf.readInt(), buf.readInt());
	}

	public static void encode(RatCommandPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.ratId());
		buf.writeInt(packet.newCommand());
	}

	public static class Handler {
		public static void handle(RatCommandPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null) {
					Entity entity = player.level().getEntity(packet.ratId());
					if (entity instanceof TamedRat rat) {
						rat.setCommandInteger(packet.newCommand());
					}
				}
			});
		}
	}
}
