package com.trafficcraft.horizontaltraffic.mixin.client;

import com.trafficcraft.horizontaltraffic.client.model.RotationModelManager;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {

    @Inject(method = "getBlockModel", at = @At("RETURN"), cancellable = true)
    private void onGetBlockModel(BlockState state, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel original = cir.getReturnValue();
        BakedModel custom = RotationModelManager.getModel(state, original);
        if (custom != null) {
            cir.setReturnValue(custom);
        }
    }

    @Inject(method = "replaceCache", at = @At("HEAD"))
    private void onReplaceCache(Map<BlockState, BakedModel> map, CallbackInfo ci) {
        RotationModelManager.clearCache();
    }
}
