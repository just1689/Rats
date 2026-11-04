package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.inventory.RatCraftingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record CycleRatRecipePacket(long blockPos, boolean increase) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<CycleRatRecipePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "cycle_rat_recipe"));
	public static final StreamCodec<RegistryFriendlyByteBuf, CycleRatRecipePacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), CycleRatRecipePacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static CycleRatRecipePacket decode(RegistryFriendlyByteBuf buf) {
		return new CycleRatRecipePacket(buf.readLong(), buf.readBoolean());
	}

	public static void encode(CycleRatRecipePacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeLong(packet.blockPos());
		buf.writeBoolean(packet.increase());
	}

	public static class Handler {
		public static void handle(CycleRatRecipePacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null) {
					if (player.containerMenu instanceof RatCraftingTableMenu table) {
						table.incrementRecipeIndex(packet.increase());
					}
				}
			});
		}
	}
}
