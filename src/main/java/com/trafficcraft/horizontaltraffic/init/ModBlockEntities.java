package com.trafficcraft.horizontaltraffic.init;

import com.trafficcraft.horizontaltraffic.HorizontalTrafficMod;
import com.trafficcraft.horizontaltraffic.block.entity.HorizontalTrafficLightBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static BlockEntityType<HorizontalTrafficLightBlockEntity> HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY;

    public static void register() {
        HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation(HorizontalTrafficMod.MOD_ID, "horizontal_traffic_light"),
                FabricBlockEntityTypeBuilder.create(
                        HorizontalTrafficLightBlockEntity::new,
                        ModBlocks.HORIZONTAL_TRAFFIC_LIGHT,
                        ModBlocks.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT,
                        ModBlocks.HORIZONTAL_SINGLE_TRAFFIC_LIGHT
                ).build(null)
        );
    }
}
