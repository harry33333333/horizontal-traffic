package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.GreenFlashHelper;
import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.trafficcraft.block.data.TrafficLightControlType;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.data.TrafficLightSchedule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightBlockEntity.class, remap = false)
public abstract class TrafficLightBlockEntityMixin implements IGreenFlashConfigurable {

    @Shadow
    private boolean running;

    @Shadow
    private int ticker;

    @Shadow
    private int phaseId;

    @Shadow
    private TrafficLightSchedule schedule;

    @Shadow
    public abstract TrafficLightControlType getControlType();

    private boolean horizontal_traffic$greenFlashEnabled = true;

    @Override
    public boolean isGreenFlashEnabled() {
        return this.horizontal_traffic$greenFlashEnabled;
    }

    @Override
    public void setGreenFlashEnabled(boolean enabled) {
        this.horizontal_traffic$greenFlashEnabled = enabled;
    }

    @Inject(method = "load", at = @At("TAIL"), remap = false)
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("HT_GreenFlash")) {
            this.horizontal_traffic$greenFlashEnabled = tag.getBoolean("HT_GreenFlash");
        } else {
            this.horizontal_traffic$greenFlashEnabled = true;
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"), remap = false)
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("HT_GreenFlash", this.horizontal_traffic$greenFlashEnabled);
    }

    @Inject(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"), remap = false)
    private void onTick(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (this.running && this.getControlType() == TrafficLightControlType.OWN_SCHEDULE) {
            if (this.horizontal_traffic$greenFlashEnabled && this.schedule != null) {
                int rem = GreenFlashHelper.getRemainingGreenTicks(this.schedule, this.ticker, this.phaseId, false);
                GreenFlashHelper.handleFlashing((TrafficLightBlockEntity) (Object) this, rem);
            }
        }
    }
}
