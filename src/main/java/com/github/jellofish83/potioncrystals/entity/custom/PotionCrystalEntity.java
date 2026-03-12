package com.github.jellofish83.potioncrystals.entity.custom;

import com.github.jellofish83.potioncrystals.entity.ModEntityTypes;
import com.github.jellofish83.potioncrystals.item.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/*
    Extends ThrowableItemProjectile, which is Minecraft's base class for item-based projectiles (like thrown snowballs,
    eggs, and potions). This allows me to use the following methods:
    - setItem() or getItem() to carry an ItemStack with the entity
    - shootFromRotation() for trajectory
    - Built in collision detection that calls onHitEntity() and onHit()
 */
public class PotionCrystalEntity extends ThrowableItemProjectile {
    // Constructor used by Forge's entity registration system
    // EntityType is passed in externally, which is required by Forge's registration pipeline
    public PotionCrystalEntity(EntityType<? extends PotionCrystalEntity> entityType, Level world) {
        super(entityType, world);
    }
    // Constructor used when a LivingEntity (i.e., a player) throws the crystal
    // Passes the registered POTION_CRYSTAL entity type to the parent constructor
    public PotionCrystalEntity(Level world, LivingEntity entity) {
        super(ModEntityTypes.POTION_CRYSTAL.get(), entity, world);
    }
    // Constructor used when the entity is spawned without an owner.
    public PotionCrystalEntity(Level world) {
        super(ModEntityTypes.POTION_CRYSTAL.get(), world);
    }

    // Tells the game which item to display as the projectile's visual in the world
    // ThrownItemRenderer (registered in ClientModEvents) uses this to render the 2D sprite (not 3D because it's unnecessary and I'm lazy. Plus it would be too complicated if my only purpose is to learn potions)
    // Returns the base POTION_CRYSTAL item. The color tint is applied separately by ClientModEvents
    @Override
    protected Item getDefaultItem() {
        return ModItems.POTION_CRYSTAL.get();
    }

    // Called when the projectile collides with another entity, this is where potion effects and damage are applied
    @Override
    protected void onHitEntity(EntityHitResult p_37259_) {
        super.onHitEntity(p_37259_);
        // Get the entity that was hit from the collision
        Entity entity = p_37259_.getEntity();
        // Safely get the entity that threw this crystal as a LivingEntity
        // getOwner() returns Entity which is too broad for applyInstantenousEffect()
        // If the owner is not a LivingEntity (i.e., a dispenser has no owner), fall back to null
        // applyInstantenousEffect() is designed to handle null sources smoothly
        LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null; // Condition ? pass : fail; It's just an if statement

        // Only apply potion effects if the hit entity is a LivingEntity
        // Non living entities (like item frames or boats) can't receive potion effects
        if(entity instanceof LivingEntity livingEntity) {
            // Read the list of MobEffectInstances from the ItemStack's potion NBT data, this is how the entity knows which effects to apply based on the thrown crystal
            // Funny side story, when I learned for each loops in my programming class, I was like "oh why would anyone use this this is so useless." Welp look at this now
            for(MobEffectInstance mobEffectInstance : PotionUtils.getMobEffects(this.getItem())) {
                if(mobEffectInstance.getEffect().isInstantenous()) {
                    // Instantaneous effects (Healing, Harming) do not have a duration, they apply their full effect immediately and cannot use addEffect()
                    /* Arguments for applyInstantenousEffect():
                       - source (who caused this, used for kill attribution)
                       - indirect source (null here, used for indirect kills like explosions)
                       - target (the entity receiving the effect)
                       - amplifier (effect level, 0 = level 1, 1 = level 2, etc.)
                       - health modifier (1.0D = full strength)
                     */
                    mobEffectInstance.getEffect().applyInstantenousEffect(owner, null, livingEntity, mobEffectInstance.getAmplifier(), 1.0D);
                } else {
                    // Duration-based effects (Swiftness, Slowness, Poison, etc) are added to the entity's active effects list, wrapping in a new MobEffectInstance copies the effect cleanly rather than sharing the same instance reference
                    livingEntity.addEffect(new MobEffectInstance(mobEffectInstance));
                }
            }
        }

        // Deal a small amount of physical impact damage to any hit entity, regardless of whether it is a LivingEntity
        // DamageSource.thrown() attributes the damage to this projectile entity, with the owner as the indirect cause
        entity.hurt(DamageSource.thrown(this, this.getOwner()), 1.0F);
    }

    // Called when the projectile collides with anything, doesn't matter what it is.
    // onHitEntity() is called first for entity collisions, onHit() was called after
    @Override
    protected void onHit(HitResult p_37260_) {
        super.onHit(p_37260_);
        // Only do server side to avoid desync
        if(!this.level.isClientSide) {
            // Broadcasts event byte 3 to all nearby clients. This triggers the particle/sound effect on impact (handled client-side).
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard(); // Remove the entity from the world after it has hit something
        }
    }
}