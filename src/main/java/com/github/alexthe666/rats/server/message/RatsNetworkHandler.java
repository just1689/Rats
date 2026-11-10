package com.github.alexthe666.rats.server.message;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RatsNetworkHandler {

	private static final String PROTOCOL_VERSION = "1";

	public static void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
		registrar.playToClient(ChangeRatlantisStatusPacket.TYPE, ChangeRatlantisStatusPacket.STREAM_CODEC, ChangeRatlantisStatusPacket.Handler::handle);
		registrar.playToServer(ClearRatRecipePacket.TYPE, ClearRatRecipePacket.STREAM_CODEC, ClearRatRecipePacket.Handler::handle);
		registrar.playToServer(CycleRatRecipePacket.TYPE, CycleRatRecipePacket.STREAM_CODEC, CycleRatRecipePacket.Handler::handle);
		registrar.playToServer(DismountRatPacket.TYPE, DismountRatPacket.STREAM_CODEC, DismountRatPacket.Handler::handle);
		registrar.playToClient(ManageRatStaffPacket.TYPE, ManageRatStaffPacket.STREAM_CODEC, ManageRatStaffPacket.Handler::handle);
		registrar.playToClient(OpenRatScreenPacket.TYPE, OpenRatScreenPacket.STREAM_CODEC, OpenRatScreenPacket.Handler::handle);
		registrar.playToServer(RatCommandPacket.TYPE, RatCommandPacket.STREAM_CODEC, RatCommandPacket.Handler::handle);
		registrar.playToServer(RatUpgradeVisibilityPacket.TYPE, RatUpgradeVisibilityPacket.STREAM_CODEC, RatUpgradeVisibilityPacket.Handler::handle);
		registrar.playToServer(SetDancingRatPacket.TYPE, SetDancingRatPacket.STREAM_CODEC, SetDancingRatPacket.Handler::handle);
		registrar.playToServer(SyncArmSwingPacket.TYPE, SyncArmSwingPacket.STREAM_CODEC, SyncArmSwingPacket.Handler::handle);
		registrar.playToClient(SyncPlaguePacket.TYPE, SyncPlaguePacket.STREAM_CODEC, SyncPlaguePacket.Handler::handle);
		registrar.playToServer(SyncRatStaffPacket.TYPE, SyncRatStaffPacket.STREAM_CODEC, SyncRatStaffPacket.Handler::handle);
		registrar.playToServer(SyncRatTagPacket.TYPE, SyncRatTagPacket.STREAM_CODEC, SyncRatTagPacket.Handler::handle);
		registrar.playToClient(SyncThrownBlockPacket.TYPE, SyncThrownBlockPacket.STREAM_CODEC, SyncThrownBlockPacket.Handler::handle);
		registrar.playToClient(UpdateCurdlerFluidPacket.TYPE, UpdateCurdlerFluidPacket.STREAM_CODEC, UpdateCurdlerFluidPacket.Handler::handle);
		registrar.playToServer(UpdateMobFilterPacket.TYPE, UpdateMobFilterPacket.STREAM_CODEC, UpdateMobFilterPacket.Handler::handle);
		registrar.playToClient(UpdateRatFluidPacket.TYPE, UpdateRatFluidPacket.STREAM_CODEC, UpdateRatFluidPacket.Handler::handle);
		registrar.playToClient(UpdateRatMusicPacket.TYPE, UpdateRatMusicPacket.STREAM_CODEC, UpdateRatMusicPacket.Handler::handle);
		registrar.playToClient(UpdateSelectedRatPacket.TYPE, UpdateSelectedRatPacket.STREAM_CODEC, UpdateSelectedRatPacket.Handler::handle);
	}
}
