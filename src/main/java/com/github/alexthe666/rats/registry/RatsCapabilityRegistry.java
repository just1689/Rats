package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.capability.SelectedRatCapability;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class RatsCapabilityRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, RatsMod.MODID);

	//transient: the selected rat is never saved to disk
	public static final Supplier<AttachmentType<SelectedRatCapability>> SELECTED_RAT = ATTACHMENTS.register("selected_rat", () -> AttachmentType.builder(holder -> {
		SelectedRatCapability cap = new SelectedRatCapability();
		if (holder instanceof Player player) {
			cap.setPlayer(player);
		}
		return cap;
	}).build());

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RatsBlockEntityRegistry.RAT_CRAFTING_TABLE.get(), (table, side) -> table.getItemHandler(side));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RatsBlockEntityRegistry.UPGRADE_COMBINER.get(), (combiner, side) ->
				side == null ? null : new SidedInvWrapper(combiner, side == Direction.UP || side == Direction.DOWN ? side : Direction.NORTH));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RatsBlockEntityRegistry.RAT_QUARRY.get(), (quarry, side) ->
				side == null ? null : new SidedInvWrapper(quarry, Direction.UP));
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RatsBlockEntityRegistry.AUTO_CURDLER.get(), (curdler, side) ->
				side == null ? null : new SidedInvWrapper(curdler, side == Direction.DOWN ? Direction.DOWN : Direction.UP));
		event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, RatsBlockEntityRegistry.AUTO_CURDLER.get(), (curdler, side) -> curdler.getTank());
		event.registerEntity(Capabilities.ItemHandler.ENTITY, RatsEntityRegistry.TAMED_RAT.get(), (rat, context) -> rat.isAlive() ? new InvWrapper(rat.getInventory()) : null);
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, RatsBlockEntityRegistry.RAT_CAGE_WHEEL.get(), (wheel, side) -> wheel.getEnergyStorage());
	}
}
