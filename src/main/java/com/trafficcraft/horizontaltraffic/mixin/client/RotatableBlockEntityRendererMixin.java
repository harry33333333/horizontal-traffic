package com.trafficcraft.horizontaltraffic.mixin.client;

import com.mojang.math.Axis;
import com.trafficcraft.horizontaltraffic.util.Rotation45Helper;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatableBlockEntityRenderer.class)
public class RotatableBlockEntityRendererMixin {

    @Inject(
            method = "renderSafe",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void applyDiagonalRotation(BERGraphics<?> graphics, float partialTick, CallbackInfo ci) {
        BlockEntity be = graphics.blockEntity();
        if (be != null && be.getBlockState() != null) {
            BlockState state = be.getBlockState();
            if (state.hasProperty(Rotation45Helper.DIAGONAL) && state.getValue(Rotation45Helper.DIAGONAL)) {
                graphics.poseStack().mulPose(Axis.YP.rotationDegrees(45.0f));
            }
        }
    }
}
