package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.client.util.RatRecordSoundInstance;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;


public record UpdateRatMusicPacket(int id, ItemStack record) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<UpdateRatMusicPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "update_rat_music"));
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRatMusicPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), UpdateRatMusicPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public static UpdateRatMusicPacket decode(RegistryFriendlyByteBuf buf) {
		return new UpdateRatMusicPacket(buf.readInt(), ItemStack.STREAM_CODEC.decode(buf));
	}

	public static void encode(UpdateRatMusicPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.id());
		ItemStack.STREAM_CODEC.encode(buf, packet.record());
	}

	public static class Handler {
		@SuppressWarnings("Convert2Lambda")
		public static void handle(UpdateRatMusicPacket packet, IPayloadContext context) {
			context.enqueueWork(new Runnable() {
				@Override
				public void run() {
					if (Minecraft.getInstance().level != null) {
						Entity entity = Minecraft.getInstance().level.getEntity(packet.id());
						if (entity instanceof TamedRat rat) {
							JukeboxSong.fromStack(rat.registryAccess(), packet.record()).ifPresent(song -> {
								Minecraft.getInstance().getSoundManager().queueTickingSound(new RatRecordSoundInstance(rat, packet.record().getItem(), song.value()));
								Minecraft.getInstance().gui.setNowPlaying(song.value().description());
							});
						}
					}
				}
			});
		}
	}
}
