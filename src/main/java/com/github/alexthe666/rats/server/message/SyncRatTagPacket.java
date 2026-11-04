package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncRatTagPacket(int ratId, List<GlobalPos> nodes) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SyncRatTagPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "sync_rat_tag"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncRatTagPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), SyncRatTagPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static SyncRatTagPacket decode(RegistryFriendlyByteBuf buf) {
		return new SyncRatTagPacket(buf.readInt(), buf.readList(FriendlyByteBuf::readGlobalPos));
	}

	public static void encode(SyncRatTagPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.ratId());
		buf.writeCollection(packet.nodes(), FriendlyByteBuf::writeGlobalPos);
	}

	public static class Handler {
		public static void handle(SyncRatTagPacket packet, IPayloadContext context) {
			context.enqueueWork(() -> {
				Player player = context.player();

				if (player != null) {
					Entity entity = player.level().getEntity(packet.ratId());
					if (entity instanceof TamedRat rat) {
						rat.getPatrolNodes().clear();
						rat.getPatrolNodes().addAll(packet.nodes());
						rat.addAdditionalSaveData(new CompoundTag());
					}
				}

			});
		}
	}
}
