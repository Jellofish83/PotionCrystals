package com.github.jellofish83.potioncrystals.event;

import com.github.jellofish83.potioncrystals.PotionCrystals;
import com.github.jellofish83.potioncrystals.entity.ModEntityTypes;
import com.github.jellofish83.potioncrystals.item.ModItems;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/*
    @Mod.EventBusSubscriber automatically scans this class for @SubscribeEvent methods and registers them to the
    specified event bus. Bus.MOD means these events fire during mod setup or startup (not during gameplay)
    Dist.CLIENT makes sure this class is only loaded on the client side. It's important because rendering and color
    logic does not exist on the server and would crash it
 */
@Mod.EventBusSubscriber(modid = PotionCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    // Registers a renderer for the entity type so it's visible in the world
    // ThrownItemRenderer is vanilla's built in renderer for item projectiles, it renders the entity as a 2D sprite using whatever getDefaultItem() returns
    // Since getDefaultItem() returns POTION_CRYSTAL, and POTION_CRYSTAL has a color handler registered below, the projectile in flight will also be correctly tinted
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.POTION_CRYSTAL.get(), ThrownItemRenderer::new);
    }

    // Registers a color handler for the item so Minecraft knows how to tint it at render time
    // getItemColors().register() takes two arguments:
    //   - An ItemColor lambda, which is called every frame when the item is rendered
    //   - The item(s) to apply this color handler to
    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register(
                // The lambda implements the ItemColor interface's single method: getColor(ItemStack stack, int tintIndex)
                // stack is the specific ItemStack being rendered (carries potion NBT)
                // tintIndex represents which texture layer is being tinted (0 is base or first layer)
                (stack, tintIndex) -> {
                    // PotionUtils.getColor() reads the potion NBT from the stack and returns the correct RGB color as an integer.
                    // This is the same mechanism vanilla uses to apply texture to potions
                    if(tintIndex == 0) {
                        return PotionUtils.getColor(stack);
                    }
                    return 16253176; // Apply the gray color. -1 is also fine because it means no tint, which is the same as applying the gray color
                },
                ModItems.POTION_CRYSTAL.get()
        );
    }
}
