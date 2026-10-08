package com.trafficcraft.horizontaltraffic.mixin;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class)
public interface BlockStateBaseAccessor {
    @Accessor("lightEmission")
    void horizontalTraffic$setLightEmission(int lightEmission);
}
