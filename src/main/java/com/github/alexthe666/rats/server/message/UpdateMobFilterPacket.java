package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.items.upgrades.MobFilterUpgradeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateMobFilterPacket(InteractionHand hand, boolean whitelist, List<String> mobs) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<UpdateMobFilterPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "update_mob_filter"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMobFilterPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), UpdateMobFilterPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static UpdateMobFilterPacket decode(RegistryFriendlyByteBuf buf) {
		return new UpdateMobFilterPacket(buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buf.readBoolean(), buf.readList(FriendlyByteBuf::readUtf));
	}

	public static void encode(UpdateMobFilterPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeBoolean(packet.hand() == InteractionHand.MAIN_HAND);
		buf.writeBoolean(packet.whitelist());
		buf.writeCollection(packet.mobs(), FriendlyByteBuf::writeUtf);
	}

	public static class Handler {
		public static void handle(UpdateMobFilterPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) context.player();
				if (player != null && player.getItemInHand(packet.hand()).getItem() instanceof MobFilterUpgradeItem) {
					ItemStack stack = player.getItemInHand(packet.hand());
					MobFilterUpgradeItem.setWhitelist(stack, packet.whitelist());
					MobFilterUpgradeItem.setMobs(stack, packet.mobs());
				}
			});
		}
	}
}
