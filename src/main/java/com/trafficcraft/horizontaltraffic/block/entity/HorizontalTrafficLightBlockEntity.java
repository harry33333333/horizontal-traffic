package com.trafficcraft.horizontaltraffic.block.entity;

import com.trafficcraft.horizontaltraffic.init.ModBlockEntities;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HorizontalTrafficLightBlockEntity extends TrafficLightBlockEntity {

    public HorizontalTrafficLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HORIZONTAL_TRAFFIC_LIGHT_BLOCK_ENTITY, pos, state);
    }
}
