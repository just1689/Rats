package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.events.ForgeEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncArmSwingPacket(ItemStack stack) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SyncArmSwingPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "sync_arm_swing"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncArmSwingPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), SyncArmSwingPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static SyncArmSwingPacket decode(RegistryFriendlyByteBuf buf) {
		return new SyncArmSwingPacket(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
	}

	public static void encode(SyncArmSwingPacket packet, RegistryFriendlyByteBuf buf) {
		ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.stack());
	}

	public static class Handler {
		public static void handle(SyncArmSwingPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> ForgeEvents.handleArmSwing(packet.stack(), Objects.requireNonNull(context.player())));
		}
	}
}
