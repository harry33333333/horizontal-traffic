package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.network.packets.cts.TrafficLightPacket;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrafficLightPacket.class)
public interface TrafficLightPacketAccessor {
    @Accessor("pos")
    BlockPos getPos();

    @Accessor(value = "phaseId", remap = false)
    int getPhaseId();

    @Accessor(value = "phaseId", remap = false)
    void setPhaseId(int phaseId);
}
