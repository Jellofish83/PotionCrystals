package com.github.jellofish83.potioncrystals.item.custom;

import com.github.jellofish83.potioncrystals.entity.custom.PotionCrystalEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

/*
    Extends ThrowablePotionItem (which itself extends PotionItem) so that the item natively understands potion NBT data
    on its ItemStack and fillItemCategory() is inherited and is automatically used on all potion variants in the
    creative tab without needing to writing any extra code
 */

public class PotionCrystalItem extends ThrowablePotionItem {
    public PotionCrystalItem(Properties builder) {
        super(builder);
    }

    // Called when player right clicks, this is where the throwing mechanism is defined
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        // Get the ItemStack the player is currently holding in the main hand (right hand), ItemStack is the item the player is holding

        // This ItemStack carries the potion NBT data which will be passed to the entity
        ItemStack itemStack = player.getItemInHand(hand);
        // Play the splash potion throw sound at the player's position
        // Passing null as the first argument means the player will also hear the sound
        // The end of the line is randomizing the pitch
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW,
                SoundSource.NEUTRAL, 0.5F, 0.4F / world.getRandom().nextFloat() * 0.4F + 0.8F);

        // Entity spawning should only happen on the server side. isClientSide returns true on the client, so it's inverted to run server-only logic
        if(!world.isClientSide) {
            // Creates a new projectile entity which is owned by the player
            PotionCrystalEntity potionCrystalEntity = new PotionCrystalEntity(world, player);
            // Pass the full ItemStack (including its potion NBT) to the entity, this is how the entity "knows" which potion crystal was thrown
            potionCrystalEntity.setItem(itemStack);
            // Set the trajectory based on where the player is looking
            // The arguments are: shooter, x-rotation (pitch), y-rotation (yaw), offset, velocity, inaccuracy
            potionCrystalEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            // Add the projectile entity into the world
            world.addFreshEntity(potionCrystalEntity);
        }

        // Increase the ITEM_USED status for the player
        player.awardStat(Stats.ITEM_USED.get(this));
        // If the player is not in creative mode (instabuild), then consume one item from the player's inventory
        if(!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        // Return success. sidedSuccess returns CONSUME on server and SUCCESS on client, which is the standard pattern for throwable items
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

    // Add the enchantment glint
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // Overrides the translation key used to look up the item's display name in the lang file.
    // PotionUtils.getPotion(itemStack) reads which potion is stored in the ItemStack's NBT.
    // getName(prefix) appends the potion's registry path to the given prefix, producing a key such as: "item.potioncrystals.potion_crystal.effect.swiftness". That key is then looked up in the lang file (en_us.json or other language files) to make the final display name
    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return PotionUtils.getPotion(itemStack).getName("item.potioncrystals.potion_crystal.effect.");
    }
}
