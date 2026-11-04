package com.github.alexthe666.rats.server.block.entity;

import com.github.alexthe666.rats.registry.RatsBlockEntityRegistry;
import com.github.alexthe666.rats.registry.RatsItemRegistry;
import com.github.alexthe666.rats.server.entity.rat.TamedRat;
import com.github.alexthe666.rats.server.inventory.RatCraftingTableMenu;
import com.github.alexthe666.rats.server.inventory.container.CraftingContainerWrapper;
import com.github.alexthe666.rats.server.inventory.container.TableItemHandlers;
import com.github.alexthe666.rats.server.misc.RatUpgradeUtils;
import com.github.alexthe666.rats.server.misc.RatsLangConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeHolder;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked", "unused"})
public class RatCraftingTableBlockEntity extends BlockEntity implements MenuProvider, RecipeCraftingHolder, Clearable {

	private static final Component DEFAULT_NAME = Component.translatable(RatsLangConstants.RAT_CRAFTING_TABLE);
	private Component customName;
	public int prevCookTime;
	private boolean hasRat;
	public boolean hasValidRecipe;
	private int cookTime;
	protected final StackedContents itemHelper = new StackedContents();
	protected Optional<RecipeHolder<CraftingRecipe>> guideRecipe = Optional.empty();
	protected Optional<RecipeHolder<CraftingRecipe>> recipeUsed = Optional.empty();
	protected List<RecipeHolder<CraftingRecipe>> possibleRecipes = List.of();
	public int totalCookTime = 200;
	private int selectedRecipeIndex = 0;
	private final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> RatCraftingTableBlockEntity.this.cookTime;
				case 1 -> RatCraftingTableBlockEntity.this.totalCookTime;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> RatCraftingTableBlockEntity.this.cookTime = value;
				case 1 -> RatCraftingTableBlockEntity.this.totalCookTime = value;
			}

		}

		public int getCount() {
			return 2;
		}
	};

	private final static EmptyItemHandler EMPTYHANDLER = new EmptyItemHandler();
	public final Optional<IItemHandlerModifiable> bufferHandler = Optional.of(new TableItemHandlers.BufferHandler(this));
	public final Optional<IItemHandlerModifiable> matrixHandler = Optional.of(new TableItemHandlers.MatrixHandler(this));
	public final Optional<IItemHandlerModifiable> resultHandler = Optional.of(new TableItemHandlers.ResultHandler(this));

	protected final Optional<IItemHandlerModifiable> combinedHandler = Optional.of(
			new CombinedInvWrapper(this.matrixHandler.orElse(EMPTYHANDLER), this.bufferHandler.orElse(EMPTYHANDLER)));
	public final Optional<CraftingContainer> matrixWrapper = Optional.of(
			new CraftingContainerWrapper(this.matrixHandler.orElse(EMPTYHANDLER)));

	public RatCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(RatsBlockEntityRegistry.RAT_CRAFTING_TABLE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, RatCraftingTableBlockEntity te) {
		te.hasRat = false;
		te.totalCookTime = 200;

		for (TamedRat rat : level.getEntitiesOfClass(TamedRat.class, new AABB(pos.getX(), (double) pos.getY() + 1, pos.getZ(), (double) pos.getX() + 1, (double) pos.getY() + 2, (double) pos.getZ() + 1))) {
			if (RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_CRAFTING.get())) {
				te.hasRat = true;
				if (RatUpgradeUtils.hasUpgrade(rat, RatsItemRegistry.RAT_UPGRADE_SPEED.get())) {
					te.totalCookTime = 100;
				}
			}
		}

		if (!level.isClientSide()) {
			te.prevCookTime = te.cookTime;

			if (te.getRecipeUsed() != null && te.hasRat && te.cookTime < te.totalCookTime) {
				te.cookTime++;
			} else {
				te.cookTime = Mth.clamp(te.cookTime - 2, 0, te.totalCookTime);
			}
			if (te.cookTime >= te.totalCookTime) {
				te.cookTime = 0;
				ItemStack addStack = te.recipeUsed.map(r -> r.value().assemble(te.matrixWrapper.orElseThrow().asCraftInput(), level.registryAccess())).orElse(ItemStack.EMPTY);
				te.resultHandler.ifPresent(h -> h.setStackInSlot(0, addStack.copyWithCount(addStack.getCount() + h.getStackInSlot(0).getCount())));
				te.consumeIngredients(null);
				te.updateRecipe();
			}
		}
	}

	public boolean hasRat() {
		return this.hasRat;
	}

	public int getCookTime() {
		return this.cookTime;
	}

	public void updateHelper() {
		this.bufferHandler.ifPresent(handler -> ((TableItemHandlers.BufferHandler) handler).fillStackedContents(this.itemHelper));
		this.checkIfRecipeIsValid(this.recipeUsed, this.itemHelper);
	}

	public void updateRecipe() {
		AtomicBoolean flag = new AtomicBoolean(true);
		if (this.getLevel() != null) {
			this.matrixWrapper.ifPresent(w -> {
				this.possibleRecipes = this.getLevel().getRecipeManager().getRecipesFor(RecipeType.CRAFTING, w.asCraftInput(), this.getLevel());
				if (this.possibleRecipes.isEmpty()) {
					flag.set(false);
				} else {
					this.selectedRecipeIndex = Mth.clamp(this.selectedRecipeIndex, 0, this.possibleRecipes.size() - 1);
					this.guideRecipe = Optional.of(this.possibleRecipes.get(this.selectedRecipeIndex));
					if (!this.checkIfResultFits(this.getLevel(), this.guideRecipe)) {
						flag.set(false);
					}
					this.recipeUsed = Optional.of(this.possibleRecipes.get(this.selectedRecipeIndex))
							.filter(r -> this.setRecipeUsed(this.getLevel(), null, r)); // Set new recipe or null if missing/can't craft
				}
			});
			if (flag.get()) {
				this.checkIfRecipeIsValid(this.recipeUsed, this.itemHelper);
				if (!this.hasValidRecipe)
					this.setRecipeUsed(null);
			} else {
				this.guideRecipe = Optional.empty();
				this.setRecipeUsed(null);
			}
		}
	}

	private boolean checkIfResultFits(Level level, Optional<RecipeHolder<CraftingRecipe>> recipe) {
		if (recipe.isPresent() && this.resultHandler.isPresent()) {
			ItemStack checkStack = this.resultHandler.get().getStackInSlot(0);
			ItemStack resultStack = recipe.get().value().getResultItem(level.registryAccess());
			return (ItemStack.isSameItemSameComponents(checkStack, resultStack) && checkStack.getCount() + resultStack.getCount() <= checkStack.getMaxStackSize()) || checkStack.isEmpty();
		}
		return false;
	}

	private void checkIfRecipeIsValid(Optional<RecipeHolder<CraftingRecipe>> recipe, StackedContents helper) {
		this.hasValidRecipe = recipe.isPresent() && helper.getBiggestCraftableStack(recipe.get(), null) > 0;
	}

	@Override
	public void clearContent() {
		this.combinedHandler.ifPresent(h -> {
			for (int i = 0; i < h.getSlots(); i++) {
				h.setStackInSlot(i, ItemStack.EMPTY);
			}
		});
		this.updateRecipe();
	}

	public void incrementSelectedRecipe(boolean negative) {
		if (negative) {
			this.selectedRecipeIndex--;
		} else {
			this.selectedRecipeIndex++;
		}
		this.updateRecipe();
	}

	@Override
	public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
		this.recipeUsed = Optional.ofNullable((RecipeHolder<CraftingRecipe>) recipe);
	}

	@Override
	public boolean setRecipeUsed(Level level, @Nullable ServerPlayer player, RecipeHolder<?> recipe) {
		return !level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) || recipe.value().isSpecial();
	}

	@Nullable
	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return this.recipeUsed.orElse(null);
	}

	public Optional<RecipeHolder<CraftingRecipe>> getGuideRecipe() {
		return this.guideRecipe;
	}

	public List<RecipeHolder<CraftingRecipe>> getPossibleRecipes() {
		return this.possibleRecipes;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		this.updateRecipe();
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.bufferHandler.ifPresent(handler -> ((INBTSerializable<CompoundTag>) handler).deserializeNBT(registries, tag.getCompound("Buffer")));
		this.matrixHandler.ifPresent(handler -> ((INBTSerializable<CompoundTag>) handler).deserializeNBT(registries, tag.getCompound("Matrix")));
		this.resultHandler.ifPresent(handler -> ((INBTSerializable<CompoundTag>) handler).deserializeNBT(registries, tag.getCompound("Result")));
		if (tag.contains("CustomName", 8)) {
			this.customName = Component.Serializer.fromJson(tag.getString("CustomName"), registries);
		}
		this.cookTime = tag.getInt("CookTime");
		this.selectedRecipeIndex = tag.getInt("SelectedRecipe");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.bufferHandler.ifPresent(h -> tag.put("Buffer", ((INBTSerializable<CompoundTag>) h).serializeNBT(registries)));
		this.matrixHandler.ifPresent(h -> tag.put("Matrix", ((INBTSerializable<CompoundTag>) h).serializeNBT(registries)));
		this.resultHandler.ifPresent(h -> tag.put("Result", ((INBTSerializable<CompoundTag>) h).serializeNBT(registries)));
		if (this.hasCustomName()) {
			tag.putString("CustomName", Component.Serializer.toJson(this.customName, registries));
		}
		tag.putInt("CookTime", this.cookTime);
		tag.putInt("SelectedRecipe", this.selectedRecipeIndex);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return this.hasCustomName() ? this.customName : DEFAULT_NAME;
	}

	public boolean hasCustomName() {
		return this.customName != null;
	}

	public void setCustomName(@Nullable Component name) {
		this.customName = name;
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new RatCraftingTableMenu(id, inventory, this, this.dataAccess);
	}

	public IItemHandlerModifiable getItemHandler(@Nullable Direction side) {
		return side == Direction.DOWN ? this.resultHandler.orElseThrow() : this.bufferHandler.orElseThrow();
	}

	public void consumeIngredients(@Nullable Player player) {
		this.recipeUsed.ifPresent(recipe -> {
			NonNullList<ItemStack> remainingStacks = this.matrixWrapper.map(w -> recipe.value().getRemainingItems(w.asCraftInput()))
					.orElse(NonNullList.withSize(0, ItemStack.EMPTY));

			if (this.hasValidRecipe) {
				this.bufferHandler.ifPresent(h ->
						recipe.value().getIngredients().forEach(i -> {
							for (int j = 0; j < h.getSlots(); j++) {
								if (i.test(h.getStackInSlot(j))) {
									h.extractItem(j, 1, false);
									break;
								}
							}
						}));
			}

			// Handle container items
			IntStream.range(0, remainingStacks.size())
					.mapToObj(i -> {
						ItemStack stack = remainingStacks.get(i);
						return this.hasValidRecipe ? stack : this.matrixHandler.map(h -> h.insertItem(i, stack, false)).orElse(stack);
					}) // Insert back the corresponding matrix slot if crafted from there
					.filter(stack -> !stack.isEmpty())
					.map(stack -> this.bufferHandler.map(h ->
							ItemHandlerHelper.insertItemStacked(h, stack, false)).orElse(stack))
					.filter(stack -> !stack.isEmpty())
					.forEach(stack -> {
						if (player != null) {
							ItemHandlerHelper.giveItemToPlayer(player, stack);
						} else {
							this.outputStack(stack);
						}
					});
		});
	}

	private void outputStack(ItemStack stack) {
		ItemStack newStack = this.bufferHandler
				.map(h -> ItemHandlerHelper.insertItemStacked(h, stack, false))
				.orElse(stack);

		if (!newStack.isEmpty() && this.getLevel() != null) {
			ItemEntity item = new ItemEntity(this.getLevel(), this.getBlockPos().getX() + 0.5F, this.getBlockPos().getY() + 1.0D, this.getBlockPos().getZ() + 0.5F, newStack);
			item.setDeltaMovement(Vec3.ZERO);
			item.setNoPickUpDelay();
			item.setExtendedLifetime();
			this.getLevel().addFreshEntity(item);
		}
	}
}
