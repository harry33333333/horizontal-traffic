package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.network.NetworkPacketContext;
import de.mrjulsen.trafficcraft.network.packets.cts.TrafficLightPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightPacket.class, remap = false)
public class TrafficLightPacketMixin {

    @Inject(method = "handle", at = @At("TAIL"), remap = false)
    private static void onHandle(TrafficLightPacket packet, NetworkPacketContext context, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (player != null) {
            Level level = player.level();
            BlockPos pos = ((TrafficLightPacketAccessor) packet).getPos();
            if (pos != null && level.isLoaded(pos)) {
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
    }
}
