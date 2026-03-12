package com.github.jellofish83.potioncrystals;

import com.github.jellofish83.potioncrystals.entity.ModEntityTypes;
import com.github.jellofish83.potioncrystals.item.ModItems;
import com.github.jellofish83.potioncrystals.recipe.ModRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/*
    Note: every class (except for this one) will be commented in detail of their functionality and how they work
    This mod is for me to learn some of the potion mechanics of vanilla Minecraft, I will also use it for future references.
    This mod, as of March 2026, also contains custom recipes with dynamic NBT data, which is applicable to potions since
    crafting with potions involves dynamic NBT data.

    I might update this mod so it will have more stuff added, so maybe sometime in the future this mod will not be limited to
    potion mechanisms of Minecraft, but also NBT data, Tiers, etc.
 */

// The value here should match an entry in the META-INF/mods.toml file
@Mod(PotionCrystals.MOD_ID)
public class PotionCrystals
{
    public static final String MOD_ID = "potioncrystals"; // Set a constant MOD_ID so it's easier to reference later
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public PotionCrystals()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModEntityTypes.register(eventBus);
        ModRecipes.register(eventBus);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
