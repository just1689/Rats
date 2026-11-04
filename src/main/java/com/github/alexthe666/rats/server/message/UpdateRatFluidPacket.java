package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.citadel.server.message.PacketBufferUtils;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record UpdateRatFluidPacket(int ratId, FluidStack fluid) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<UpdateRatFluidPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "update_rat_fluid"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRatFluidPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), UpdateRatFluidPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static UpdateRatFluidPacket decode(RegistryFriendlyByteBuf buf) {
		return new UpdateRatFluidPacket(buf.readInt(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buf));
	}

	public static void encode(UpdateRatFluidPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.ratId);
		FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, packet.fluid() == null ? FluidStack.EMPTY : packet.fluid());
	}

	public static class Handler {

		public static void handle(UpdateRatFluidPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null) {
					Entity entity = player.level().getEntity(packet.ratId());
					if (entity instanceof TamedRat rat) {
						rat.transportingFluid = packet.fluid();

					}
				}
			});
		}
	}
}
