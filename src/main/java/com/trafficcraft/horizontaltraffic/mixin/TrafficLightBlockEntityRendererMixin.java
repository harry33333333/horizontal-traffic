package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.trafficcraft.client.ber.TrafficLightBlockEntityRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
