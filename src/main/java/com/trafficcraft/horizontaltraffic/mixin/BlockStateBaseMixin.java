package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.block.TrafficLightBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow public abstract Block getBlock();

    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void horizontalTraffic$getLightEmission(CallbackInfoReturnable<Integer> cir) {
        if (this.getBlock() instanceof TrafficLightBlock) {
            cir.setReturnValue(3);
        }
    }
}
