package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.block.TrafficLightBlock;
import de.mrjulsen.trafficcraft.item.TrafficLightLinkerItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrafficLightLinkerItem.class, remap = false)
public class TrafficLightLinkerItemMixin {

    @Inject(method = "isTargetBlockAccepted", at = @At("HEAD"), cancellable = true, remap = false)
    private void onIsTargetBlockAccepted(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof TrafficLightBlock) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isSourceBlockAccepted", at = @At("HEAD"), cancellable = true, remap = false)
    private void onIsSourceBlockAccepted(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof TrafficLightBlock) {
            cir.setReturnValue(true);
        }
    }
}
