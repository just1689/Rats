package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ChangeRatlantisStatusPacket(boolean enabled) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ChangeRatlantisStatusPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "change_ratlantis_status"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChangeRatlantisStatusPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), ChangeRatlantisStatusPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static ChangeRatlantisStatusPacket decode(RegistryFriendlyByteBuf buf) {
		return new ChangeRatlantisStatusPacket(buf.readBoolean());
	}

	public static void encode(ChangeRatlantisStatusPacket message, RegistryFriendlyByteBuf buf) {
		buf.writeBoolean(message.enabled());
	}

	public static class Handler {
		public static void handle(ChangeRatlantisStatusPacket message, IPayloadContext context) {
			context.enqueueWork(() -> RatsMod.RATLANTIS_DATAPACK_ENABLED = message.enabled());
		}
	}

}
