package com.trafficcraft.horizontaltraffic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;

public class CountdownRenderer {

    public static void renderCountdown(BERGraphics<?> graphics, TrafficLightBlockEntity be) {
        if (be == null) return;
        if (!(be instanceof IGreenFlashConfigurable flashBe)) return;
        if (!flashBe.isCountdownEnabled()) return;
        if (flashBe.isYellowFlashingEnabled()) return;

        int number = flashBe.getCountdownNumber();
        if (number < 1 || number > 9) return;

        boolean isGreen = flashBe.isCountdownGreen();
        if (isGreen && flashBe.isGreenFlashEnabled()) {
            // In the final 3 seconds, blink synchronously with the green light
            if (!be.isColorEnabled(TrafficLightColor.GREEN, true)) {
                return;
            }
        }

        // Green phase displays green digit, Red phase displays red digit
        int textColor = isGreen ? 0xFF00FF00 : 0xFFFF0000;

        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        if (font == null) return;

        String text = Integer.toString(number);
        float textW = font.width(text);
        float scale = 0.36f;

        PoseStack poseStack = graphics.poseStack();
        poseStack.pushPose();
        // Bulb is 4x4 pixels (from 0 to 4 in X and Y), centered at (2.0f, 2.0f)
        // Bulb front face is at Z = 1.0f, offset slightly to 1.05f to avoid z-fighting
        poseStack.translate(2.0f, 2.0f, 1.05f);
        poseStack.scale(scale, scale, 1.0f);

        // Center horizontally and vertically (visible digit glyph is ~7px tall)
        float drawX = -textW / 2.0f;
        float drawY = -3.5f;

        font.drawInBatch(
                text,
                drawX,
                drawY,
                textColor,
                false,
                poseStack.last().pose(),
                graphics.multiBufferSource(),
                Font.DisplayMode.POLYGON_OFFSET,
                0,
                LightTexture.FULL_BRIGHT
        );
        poseStack.popPose();
    }
}
