package com.trafficcraft.horizontaltraffic;

import com.trafficcraft.horizontaltraffic.client.HorizontalTrafficLightBlockEntityRenderer;
import com.trafficcraft.horizontaltraffic.init.ModBlockEntities;
import com.trafficcraft.horizontaltraffic.init.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public class HorizontalTrafficClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(
                ModBlockEntities.HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY,
                HorizontalTrafficLightBlockEntityRenderer::new
        );

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HORIZONTAL_TRAFFIC_LIGHT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HORIZONTAL_SINGLE_TRAFFIC_LIGHT, RenderType.cutout());
    }
}
