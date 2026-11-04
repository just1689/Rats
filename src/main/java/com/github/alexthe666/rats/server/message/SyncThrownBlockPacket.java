package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.entity.projectile.ThrownBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record SyncThrownBlockPacket(int blockEntityId, long blockPos) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SyncThrownBlockPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "sync_thrown_block"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncThrownBlockPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), SyncThrownBlockPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static SyncThrownBlockPacket decode(RegistryFriendlyByteBuf buf) {
		return new SyncThrownBlockPacket(buf.readInt(), buf.readLong());
	}

	public static void encode(SyncThrownBlockPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.blockEntityId());
		buf.writeLong(packet.blockPos());
	}

	public static class Handler {

		public static void handle(SyncThrownBlockPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();
				if (player != null) {
					Entity entity = player.level().getEntity(packet.blockEntityId());
					if (entity instanceof ThrownBlock blocc) {
						BlockPos pos = BlockPos.of(packet.blockPos());
						blocc.setHeldBlockState(player.level().getBlockState(pos));
					}
				}
			});
		}
	}
}