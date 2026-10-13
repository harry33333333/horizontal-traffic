package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.GreenFlashHelper;
import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.data.TrafficLightControlType;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.data.TrafficLightSchedule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(TrafficLightBlockEntity.class)
public abstract class TrafficLightBlockEntityMixin implements IGreenFlashConfigurable {

    @Shadow(remap = false)
    private boolean running;

    @Shadow(remap = false)
    private int ticker;

    @Shadow(remap = false)
    private int phaseId;

    @Shadow(remap = false)
    private TrafficLightSchedule schedule;

    @Shadow(remap = false)
    public abstract TrafficLightControlType getControlType();

    private boolean horizontal_traffic$greenFlashEnabled = true;
    private boolean horizontal_traffic$yellowFlashingEnabled = false;
    private boolean horizontal_traffic$countdownEnabled = true;
    private int horizontal_traffic$countdownNumber = 0;
    private boolean horizontal_traffic$countdownIsGreen = false;

    @Override
    public boolean isGreenFlashEnabled() {
        return this.horizontal_traffic$greenFlashEnabled;
    }

    @Override
    public void setGreenFlashEnabled(boolean enabled) {
        this.horizontal_traffic$greenFlashEnabled = enabled;
    }

    @Override
    public boolean isYellowFlashingEnabled() {
        return this.horizontal_traffic$yellowFlashingEnabled;
    }

    @Override
    public void setYellowFlashingEnabled(boolean enabled) {
        boolean changed = (this.horizontal_traffic$yellowFlashingEnabled != enabled);
        this.horizontal_traffic$yellowFlashingEnabled = enabled;
        if (changed && !enabled) {
            try {
                GreenFlashHelper.isApplyingYellowFlash = true;
                ((TrafficLightBlockEntity) (Object) this).enableOnlyColors(java.util.List.of());
                syncBlock();
            } finally {
                GreenFlashHelper.isApplyingYellowFlash = false;
            }
        }
    }

    @Override
    public boolean isCountdownEnabled() {
        return this.horizontal_traffic$countdownEnabled;
    }

    @Override
    public void setCountdownEnabled(boolean enabled) {
        this.horizontal_traffic$countdownEnabled = enabled;
    }

    @Override
    public int getCountdownNumber() {
        return this.horizontal_traffic$countdownNumber;
    }

    @Override
    public void setCountdownNumber(int number) {
        this.horizontal_traffic$countdownNumber = number;
    }

    @Override
    public boolean isCountdownGreen() {
        return this.horizontal_traffic$countdownIsGreen;
    }

    @Override
    public void setCountdownGreen(boolean isGreen) {
        this.horizontal_traffic$countdownIsGreen = isGreen;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("HT_GreenFlash")) {
            this.horizontal_traffic$greenFlashEnabled = tag.getBoolean("HT_GreenFlash");
        } else {
            this.horizontal_traffic$greenFlashEnabled = true;
        }
        if (tag.contains("HT_YellowFlash")) {
            this.horizontal_traffic$yellowFlashingEnabled = tag.getBoolean("HT_YellowFlash");
        } else {
            this.horizontal_traffic$yellowFlashingEnabled = false;
        }
        if (tag.contains("HT_Countdown")) {
            this.horizontal_traffic$countdownEnabled = tag.getBoolean("HT_Countdown");
        } else {
            this.horizontal_traffic$countdownEnabled = true;
        }
        if (tag.contains("HT_CountdownNum")) {
            this.horizontal_traffic$countdownNumber = tag.getInt("HT_CountdownNum");
        } else {
            this.horizontal_traffic$countdownNumber = 0;
        }
        if (tag.contains("HT_CountdownIsGreen")) {
            this.horizontal_traffic$countdownIsGreen = tag.getBoolean("HT_CountdownIsGreen");
        } else {
            this.horizontal_traffic$countdownIsGreen = false;
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("HT_GreenFlash", this.horizontal_traffic$greenFlashEnabled);
        tag.putBoolean("HT_YellowFlash", this.horizontal_traffic$yellowFlashingEnabled);
        tag.putBoolean("HT_Countdown", this.horizontal_traffic$countdownEnabled);
        tag.putInt("HT_CountdownNum", this.horizontal_traffic$countdownNumber);
        tag.putBoolean("HT_CountdownIsGreen", this.horizontal_traffic$countdownIsGreen);
    }

    @Inject(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void onTick(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide()) return;
        TrafficLightBlockEntity self = (TrafficLightBlockEntity) (Object) this;
        if (this.horizontal_traffic$yellowFlashingEnabled) {
            GreenFlashHelper.handleYellowFlashing(self, level);
            GreenFlashHelper.resetCountdown(self);
            return;
        }
        if (this.running && this.getControlType() == TrafficLightControlType.OWN_SCHEDULE) {
            if (this.schedule != null) {
                int currentTick = Math.max(0, this.ticker - 1);
                if (this.horizontal_traffic$greenFlashEnabled) {
                    int rem = GreenFlashHelper.getRemainingGreenTicks(this.schedule, currentTick, this.phaseId, false);
                    GreenFlashHelper.handleFlashing(self, rem);
                }
                GreenFlashHelper.updateCountdown(self, this.schedule, currentTick, this.phaseId, false);
            } else {
                GreenFlashHelper.resetCountdown(self);
            }
        } else {
            GreenFlashHelper.resetCountdown(self);
        }
    }

    @Inject(method = "enableOnlyColors", at = @At("HEAD"), cancellable = true, remap = false)
    private void onEnableOnlyColorsHead(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        if (this.horizontal_traffic$yellowFlashingEnabled && !GreenFlashHelper.isApplyingYellowFlash) {
            ci.cancel();
        }
    }

    @Inject(method = "enableColors", at = @At("HEAD"), cancellable = true, remap = false)
    private void onEnableColorsHead(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        if (this.horizontal_traffic$yellowFlashingEnabled && !GreenFlashHelper.isApplyingYellowFlash) {
            ci.cancel();
        }
    }

    @Inject(method = "disableColors", at = @At("HEAD"), cancellable = true, remap = false)
    private void onDisableColorsHead(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        if (this.horizontal_traffic$yellowFlashingEnabled && !GreenFlashHelper.isApplyingYellowFlash) {
            ci.cancel();
        }
    }

    @Inject(method = "enableOnlyColors", at = @At("TAIL"), remap = false)
    private void onEnableOnlyColors(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        syncBlock();
    }

    @Inject(method = "enableColors", at = @At("TAIL"), remap = false)
    private void onEnableColors(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        syncBlock();
    }

    @Inject(method = "disableColors", at = @At("TAIL"), remap = false)
    private void onDisableColors(Collection<TrafficLightColor> colors, CallbackInfo ci) {
        syncBlock();
    }

    private void syncBlock() {
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level != null && !level.isClientSide()) {
            BlockPos pos = ((BlockEntity) (Object) this).getBlockPos();
            BlockState state = ((BlockEntity) (Object) this).getBlockState();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }
}
