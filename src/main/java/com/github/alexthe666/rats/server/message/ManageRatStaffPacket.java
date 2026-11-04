package com.github.alexthe666.rats.server.message;

import com.github.alexthe666.rats.RatsMod;

import com.github.alexthe666.rats.client.gui.CheeseStaffScreen;
import com.github.alexthe666.rats.client.gui.PatrolStaffScreen;
import com.github.alexthe666.rats.client.gui.RadiusStaffScreen;
import com.github.alexthe666.rats.registry.RatsCapabilityRegistry;
import com.github.alexthe666.rats.server.capability.SelectedRatCapability;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ManageRatStaffPacket(int entityId, BlockPos pos, int dirOrd, boolean clear, boolean openGUI, int staffToOpen) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ManageRatStaffPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RatsMod.MODID, "manage_rat_staff"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ManageRatStaffPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), ManageRatStaffPacket::decode);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}


	public ManageRatStaffPacket(int entityId, BlockPos pos, int dirOrd, boolean clear, boolean openGUI) {
		this(entityId, pos, dirOrd, clear, openGUI, 0);
	}

	public static ManageRatStaffPacket decode(RegistryFriendlyByteBuf buf) {
		return new ManageRatStaffPacket(buf.readInt(), buf.readBlockPos(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
	}

	public static void encode(ManageRatStaffPacket packet, RegistryFriendlyByteBuf buf) {
		buf.writeInt(packet.entityId());
		buf.writeBlockPos(packet.pos());
		buf.writeInt(packet.dirOrd());
		buf.writeBoolean(packet.clear());
		buf.writeBoolean(packet.openGUI());
		buf.writeInt(packet.staffToOpen());
	}

	public static class Handler {

		@SuppressWarnings("Convert2Lambda")
		public static void handle(ManageRatStaffPacket packet, IPayloadContext context) {
			context.enqueueWork(new Runnable() {
				@Override
				public void run() {
					if (packet.clear()) {
						Minecraft.getInstance().player.getData(RatsCapabilityRegistry.SELECTED_RAT).clearSelectedRat();
					} else {
						Entity e = Minecraft.getInstance().player.level().getEntity(packet.entityId());
						if (e instanceof TamedRat rat) {
							if (packet.openGUI()) {
								switch (packet.staffToOpen()) {
									case 1 ->
											Minecraft.getInstance().setScreen(new RadiusStaffScreen(rat, packet.pos()));
									case 2 ->
											Minecraft.getInstance().setScreen(new PatrolStaffScreen(rat, packet.pos()));
									default ->
											Minecraft.getInstance().setScreen(new CheeseStaffScreen(rat, packet.pos(), Direction.values()[packet.dirOrd()]));
								}
							}
						}
					}
				}
			});
		}
	}
}
