package com.github.alexthe666.rats.server.items;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * Items no longer have a client extension hook in NeoForge 1.21.
 * Items implementing this are registered through {@link net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent}.
 */
public interface ClientExtensionsItem {
	void registerClientExtensions(Consumer<IClientItemExtensions> consumer);
}
