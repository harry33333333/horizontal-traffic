package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.block.TrafficLightBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightBlock.class, remap = false)
public class TrafficLightBlockMixin {
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void horizontalTraffic$setTrafficLightLuminance(CallbackInfo ci) {
        for (BlockState state : ((Block) (Object) this).getStateDefinition().getPossibleStates()) {
            ((BlockStateBaseAccessor) state).horizontalTraffic$setLightEmission(3);
        }
    }
}
