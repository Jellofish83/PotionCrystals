package com.github.jellofish83.potioncrystals.item;

import com.github.jellofish83.potioncrystals.PotionCrystals;
import com.github.jellofish83.potioncrystals.item.custom.PotionCrystalItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    /*
        DeferredRegister is Forge's system for safely registering the game objects, it queues registrations and submits
        them at the correct time during startup to prevent crashes that would occur from registering the objects too early.
        ForgeRegistries.ITEMS tells DeferredRegister that I am registering Item objects specifically.
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PotionCrystals.MOD_ID);

    /*
        Only one item is registered here. This item will act as the base item, or the template for all potion crystal variants.
        The item itself don't "know" which variant it actually represents, that information is stored as NBT data on
        each individual ItemStack at runtime. ClientModEvents handles the color tinting based on that NBT data and
        PotionCrystalItem handles behavior (throwing, naming, creative tab population).
    */
    public static final RegistryObject<Item> POTION_CRYSTAL = ITEMS.register("potion_crystal",
            () -> new PotionCrystalItem(new Item.Properties().tab(CreativeModeTab.TAB_BREWING)));

    // This is a helper method that registers this class to the main class's eventBus so Forge cal process it
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
