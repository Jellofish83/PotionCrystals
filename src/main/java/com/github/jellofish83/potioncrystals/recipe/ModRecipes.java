package com.github.jellofish83.potioncrystals.recipe;

import com.github.jellofish83.potioncrystals.PotionCrystals;
import com.github.jellofish83.potioncrystals.recipe.custom.PotionCrystalRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    // Same logic as ModItems. But a recipe serializer is telling Forge how to read a recipe from JSON and create that recipe object in Java
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PotionCrystals.MOD_ID);

    // Registers the custom recipe serializer under the name "potion_crystal_recipe"
    // This name must match the "type" field in the recipe JSON file:
    //   "type": "potioncrystals:potion_crystal_recipe"
    // SimpleRecipeSerializer is a Forge helper that handles fromJson() and fromNetwork() automatically for recipes that store no extra data in their JSON.
    // Since all the recipe logic are in Java (matches() and assemble()), the JSON file only needs the type field, which makes using SimpleRecipeSerializer the best way to do it
    // PotionCrystalRecipe::new is a constructor reference that acts as a factory
    // SimpleRecipeSerializer uses it to create new PotionCrystalRecipe instances when loading the recipe from the JSON file, passing in the ResourceLocation (the recipe's registry name) as the argument
    public static final RegistryObject<RecipeSerializer<PotionCrystalRecipe>> POTION_CRYSTAL_RECIPE =
            RECIPES.register("potion_crystal_recipe", () -> new SimpleRecipeSerializer<>(PotionCrystalRecipe::new));

    // Helper method to register this in the main class
    public static void register(IEventBus eventBus) {
        RECIPES.register(eventBus);
    }
}
