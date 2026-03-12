package com.github.jellofish83.potioncrystals.entity;

import com.github.jellofish83.potioncrystals.PotionCrystals;
import com.github.jellofish83.potioncrystals.entity.custom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    // Same logic as ModItems
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, PotionCrystals.MOD_ID);

    /*
        Registers one generic entity type for all potion crystal projectiles. Like the item, there is only one entity type,
        the potion data is carried on the ItemStack inside the entity, not in the entity type itself.
        EntityType.Builder.of() takes two arguments:
        - A constructor reference (PotionCrystalEntity::new) telling Forge how to create the entity
        - A MobCategory — MISC is used for projectiles and other non-mob entities

        .sized() defines the entity's hitbox width and height in blocks.
        .fireImmune() prevents the projectile from burning mid air in fire or lava.
        .build() finalizes the EntityType with a registry name string.
     */
    public static final RegistryObject<EntityType<PotionCrystalEntity>> POTION_CRYSTAL = ENTITY_TYPES.register("potion_crystal",
            () -> EntityType.Builder.<PotionCrystalEntity>of(PotionCrystalEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).fireImmune()
                    .build(new ResourceLocation(PotionCrystals.MOD_ID, "potion_crystal").toString()));

    // Helper method to register this class to the main class
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
