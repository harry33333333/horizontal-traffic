package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.network.packets.cts.TrafficLightPacket;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TrafficLightPacket.class, remap = false)
public interface TrafficLightPacketAccessor {
    @Accessor("pos")
    BlockPos getPos();

    @Accessor("phaseId")
    int getPhaseId();

    @Accessor("phaseId")
    void setPhaseId(int phaseId);
}
