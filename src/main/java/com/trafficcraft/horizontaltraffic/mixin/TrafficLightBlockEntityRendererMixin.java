package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.client.CountdownRenderer;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.trafficcraft.block.TrafficLightBlock;
import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.data.TrafficLightModel;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.client.ber.TrafficLightBlockEntityRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightBlockEntityRenderer.class, remap = false)
public class TrafficLightBlockEntityRendererMixin {
    @Redirect(
            method = "renderBlock",
            at = @At(value = "INVOKE", target = "Lde/mrjulsen/mcdragonlib/client/ber/BERGraphics;packedLight()I", ordinal = 0),
            remap = false
    )
    private int horizontalTraffic$litPackedLight(BERGraphics<?> graphics) {
        return LightTexture.FULL_BRIGHT;
    }

    @Inject(method = "renderBlock", at = @At("TAIL"), remap = false)
    private void onRenderBlockTail(BERGraphics<TrafficLightBlockEntity> graphics, float partialTick, CallbackInfo ci) {
        TrafficLightBlockEntity be = graphics.blockEntity();
        if (be == null) return;
        BlockState state = be.getBlockState();
        if (state == null) return;
        TrafficLightModel model = state.getValue(TrafficLightBlock.MODEL);
        if (model == null) return;
        int lightCount = model.getLightsCount();

        int yellowSlotIndex = -1;
        for (int i = 0; i < be.getColorSlotCount() && i < lightCount; i++) {
            TrafficLightColor slotColor = be.getColorOfSlot(i);
            if (slotColor == TrafficLightColor.YELLOW || (slotColor != null && slotColor.isSimilar(TrafficLightColor.YELLOW))) {
                yellowSlotIndex = i;
                break;
            }
        }

        if (yellowSlotIndex >= 0) {
            graphics.poseStack().pushPose();
            graphics.poseStack().translate(6.0f, 5.5f + yellowSlotIndex * 5.0f, 13.0f);
            CountdownRenderer.renderCountdown(graphics, be);
            graphics.poseStack().popPose();
        }
    }
}

