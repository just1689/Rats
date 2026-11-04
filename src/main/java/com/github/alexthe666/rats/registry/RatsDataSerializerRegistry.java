package com.github.alexthe666.rats.registry;

import io.netty.buffer.ByteBuf;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class RatsDataSerializerRegistry {

	public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, RatsMod.MODID);

	public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<List<GlobalPos>>> GLOBAL_POS_LIST = DATA_SERIALIZERS.register("global_pos_list", () -> new EntityDataSerializer<>() {
		private static final StreamCodec<ByteBuf, List<GlobalPos>> CODEC = GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list());

		@Override
		public StreamCodec<? super RegistryFriendlyByteBuf, List<GlobalPos>> codec() {
			return CODEC;
		}

		@Override
		public List<GlobalPos> copy(List<GlobalPos> list) {
			return new ArrayList<>(list);
		}
	});
}
