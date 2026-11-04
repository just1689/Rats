package com.github.alexthe666.rats.client.gui;

import com.github.alexthe666.rats.server.inventory.RatCraftingTableMenu;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Iterator;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RatCraftingRecipeBookComponent extends RecipeBookComponent {

	@Override
	public void placeRecipe(int width, int height, int result, RecipeHolder<?> recipe, Iterator<Ingredient> iterator, int zeroidk) {
		if (this.menu instanceof RatCraftingTableMenu crafting) {
			//clear grid
			for (int slot = 0; slot < 9; slot++) {
				int finalSlot = slot;
				crafting.getCraftingTable().matrixHandler.ifPresent(handler -> handler.setStackInSlot(finalSlot, new ItemStack(Items.AIR)));
			}

			super.placeRecipe(width, height, result, recipe, iterator, zeroidk);
		}
	}

	@Override
	public void addItemToSlot(Ingredient ingredient, int slotIndex, int zeroidk, int width, int height) {
		if (this.menu instanceof RatCraftingTableMenu crafting) {
			if (!ingredient.isEmpty()) {
				Slot slot = this.menu.slots.get(slotIndex);
				crafting.getCraftingTable().matrixHandler.ifPresent(handler -> handler.setStackInSlot(slot.index - 1, ingredient.getItems()[0]));
			}
		}
	}
}
