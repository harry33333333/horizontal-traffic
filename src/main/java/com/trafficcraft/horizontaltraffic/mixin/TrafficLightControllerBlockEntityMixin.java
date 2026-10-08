package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.GreenFlashHelper;
import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.mcdragonlib.data.WorldLocation;
import de.mrjulsen.trafficcraft.block.data.TrafficLightControlType;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightControllerBlockEntity;
import de.mrjulsen.trafficcraft.data.TrafficLightSchedule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TrafficLightControllerBlockEntity.class)
public abstract class TrafficLightControllerBlockEntityMixin {

    @Shadow(remap = false)
    private boolean running;

    @Shadow(remap = false)
    private int ticks;

    @Shadow(remap = false)
    private List<WorldLocation> trafficLightLocations;

    @Shadow(remap = false)
    public abstract TrafficLightSchedule getFirstOrMainSchedule();

    @Inject(method = "instanceTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void onInstanceTick(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide() || !this.running || this.trafficLightLocations == null) {
            return;
        }
        TrafficLightSchedule schedule = this.getFirstOrMainSchedule();
        if (schedule == null) return;

        int currentTick = Math.max(0, this.ticks - 1);
        for (WorldLocation loc : this.trafficLightLocations) {
            if (loc == null) continue;
            BlockPos lightPos = loc.getLocationBlockPos();
            if (lightPos != null && level.isLoaded(lightPos)) {
                BlockEntity be = level.getBlockEntity(lightPos);
                if (be instanceof TrafficLightBlockEntity light) {
                    if (((IGreenFlashConfigurable) light).isYellowFlashingEnabled()) {
                        GreenFlashHelper.handleYellowFlashing(light, level);
                        continue;
                    }
                    if (((IGreenFlashConfigurable) light).isGreenFlashEnabled()) {
                        if (light.getControlType() == TrafficLightControlType.REMOTE) {
                            int rem = GreenFlashHelper.getRemainingGreenTicks(schedule, currentTick, light.getPhaseId(), true);
                            GreenFlashHelper.handleFlashing(light, rem);
                        }
                    }
                }
            }
        }
    }
}
