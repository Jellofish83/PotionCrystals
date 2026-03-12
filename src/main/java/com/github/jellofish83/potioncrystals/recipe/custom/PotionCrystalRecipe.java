package com.github.jellofish83.potioncrystals.recipe.custom;

import com.github.jellofish83.potioncrystals.item.ModItems;
import com.github.jellofish83.potioncrystals.recipe.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/*
    Extends CustomRecipe, which is Minecraft's base class for recipes with dynamic outputs that cannot be expressed as
    static JSON (outputs that depend on input NBT). CustomRecipe handles the model of being a crafting table recipe,
    so implement the logic specific to the recipe is the only action needed
*/
public class PotionCrystalRecipe extends CustomRecipe {
    // Constructor required by CustomRecipe
    // The ResourceLocation is the recipe's registry name, derived from the JSON filename
    // For example: data/potioncrystals/recipes/potion_crystal_recipe.json is just ResourceLocation("potioncrystals", "potion_crystal_recipe")
    // SimpleRecipeSerializer passes this in automatically when loading from JSON
    public PotionCrystalRecipe(ResourceLocation p_43833_) {
        super(p_43833_);
    }

    // Called every time the crafting grid changes to check if the current arrangement of items is a valid recipe
    // Returns true if either of the two valid orientations is detected
    //
    // Orientation 1 (Q - quartz, I - ice, P - potion):
    //   [ ][Q][ ]
    //   [I][P][I]
    //   [ ][ ][ ]
    //
    // Orientation 2:
    //   [ ][ ][ ]
    //   [ ][Q][ ]
    //   [I][P][I]
    //
    // Slot numbering in a 3x3 grid (0-based, left to right, top to bottom):
    //   0 | 1 | 2
    //   3 | 4 | 5
    //   6 | 7 | 8
    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return matchesOrientation(container, 1, 4, 3, 5)
                || matchesOrientation(container, 4, 7, 6, 8);
    }

    // Called when the player takes the output from the crafting result slot and is responsible for building and returning the output ItemStack
    // Since the output depends on which potion was used as input, it's needed to
    // 1. Determine which orientation is active to find the potion slot
    // 2. Read the potion type from the input ItemStack's NBT
    // 3. Give the potion type onto the output crystals
    @Override
    public ItemStack assemble(CraftingContainer container) {
        int potionSlot;

        // Determine which slot the potion is in based on which orientation is valid
        // Orientation 1 has the potion in slot 4, orientation 2 has it in slot 7
        if(matchesOrientation(container, 1, 4, 3, 5)) {
            potionSlot = 4;
        } else {
            potionSlot = 7;
        }

        // Get the potion ItemStack from the correct slot. This ItemStack carries the potion type as NBT data
        ItemStack potionStack = container.getItem(potionSlot);
        // Create the output item, which are 3 potion crystals with no NBT data yet
        ItemStack output = new ItemStack(ModItems.POTION_CRYSTAL.get(), 3);
        // PotionUtils.getPotion() reads the potion type from the input stack's NBT
        // PotionUtils.setPotion() gives that same potion type onto the output stack's NBT
        // The result is 3 potion crystals carrying the same potion data as the input
        return PotionUtils.setPotion(output, PotionUtils.getPotion(potionStack));
    }

    // Tells Minecraft the min grid size required to fit this recipe
    // This recipe needs at least 3 columns (for the two ice blocks and the items in between them) and at least 2 rows (for the quartz row and the ice/potion row).
    // Returns false for grids that are too small (for instance, the 2x2 inventory grid)
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 2;
    }

    // Returns the serializer responsible for reading and writing this recipe
    // This links the recipe class back to the registered serializer in ModRecipes, which Forge uses when saving/syncing recipes to clients
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POTION_CRYSTAL_RECIPE.get();
    }

    // Helper method that checks whether the crafting grid matches one specific orientation of the recipe.
    // Called twice from matches() with different slot numbers for each orientation
    //
    // Parameters:
    //   container  — the crafting grid to check
    //   quartzSlot — the slot expected to contain quartz
    //   potionSlot — the slot expected to contain any non empty potion
    //   iceSlot1   — the slot expected to contain the first ice block
    //   iceSlot2   — the slot expected to contain the second ice block
    //
    // Returns true only if ALL four slots contain the correct items simultaneously
    private boolean matchesOrientation(CraftingContainer container, int quartzSlot, int potionSlot, int iceSlot1, int iceSlot2) {
        // Get the ItemStack in the potion slot to read its NBT
        ItemStack itemStack = container.getItem(potionSlot);
        return container.getItem(quartzSlot).is(Items.QUARTZ) && PotionUtils.getPotion(itemStack) != Potions.EMPTY
                && container.getItem(iceSlot1).is(Items.ICE) && container.getItem(iceSlot2).is(Items.ICE);
    }
}
