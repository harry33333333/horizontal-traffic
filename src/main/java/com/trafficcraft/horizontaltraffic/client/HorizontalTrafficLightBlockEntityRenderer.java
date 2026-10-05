package com.trafficcraft.horizontaltraffic.client;

import com.trafficcraft.horizontaltraffic.block.HorizontalTrafficLightBlock;
import com.trafficcraft.horizontaltraffic.block.entity.HorizontalTrafficLightBlockEntity;
import com.trafficcraft.horizontaltraffic.data.HorizontalLightPosition;
import de.mrjulsen.mcdragonlib.client.ber.BERGraphics;
import de.mrjulsen.mcdragonlib.client.ber.RotatableBlockEntityRenderer;
import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.data.TrafficLightIcon;
import de.mrjulsen.trafficcraft.block.data.TrafficLightModel;
import de.mrjulsen.trafficcraft.client.TrafficLightTextureManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class HorizontalTrafficLightBlockEntityRenderer extends RotatableBlockEntityRenderer<HorizontalTrafficLightBlockEntity> {

    public HorizontalTrafficLightBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getViewDistance() {
        return super.getViewDistance() * 2;
    }

    @Override
    public void renderBlock(BERGraphics<HorizontalTrafficLightBlockEntity> graphics, float partialTick) {
        HorizontalTrafficLightBlockEntity be = graphics.blockEntity();
        if (be == null) return;
        BlockState state = be.getBlockState();
        if (state == null) return;

        TrafficLightModel model = state.getValue(HorizontalTrafficLightBlock.MODEL);
        HorizontalLightPosition pos = state.getValue(HorizontalTrafficLightBlock.POSITION);

        float bulbY = pos.getRenderY();
        int lightCount = model.getLightsCount();

        // From left to right: Red (slot 0), Yellow (slot 1), Green (slot 2)
        float[] bulbXOffsets;
        if (lightCount == 3) {
            bulbXOffsets = new float[]{ 1.5f, 6.0f, 10.5f };
        } else if (lightCount == 2) {
            bulbXOffsets = new float[]{ 3.5f, 8.5f };
        } else {
            bulbXOffsets = new float[]{ 6.0f };
        }

        for (int i = 0; i < be.getColorSlotCount() && i < lightCount && i < bulbXOffsets.length; i++) {
            graphics.poseStack().pushPose();
            graphics.poseStack().translate(bulbXOffsets[i], bulbY, 13.0f);

            TrafficLightColor slotColor = be.getColorOfSlot(i);
            if (slotColor != null && be.isColorEnabled(slotColor, true)) {
                new TrafficLightTextureManager.TrafficLightTextureKey(be.getIcon(), slotColor)
                        .render(graphics, be, graphics.packedLight());
            } else {
                new TrafficLightTextureManager.TrafficLightTextureKey(TrafficLightIcon.NONE, TrafficLightColor.NONE)
                        .render(graphics, be, graphics.packedLight());
            }
            graphics.poseStack().popPose();
        }
    }
}
