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

    @Override
    public boolean isGreenFlashEnabled() {
        return this.horizontal_traffic$greenFlashEnabled;
    }

    @Override
    public void setGreenFlashEnabled(boolean enabled) {
        this.horizontal_traffic$greenFlashEnabled = enabled;
    }

    @Inject(method = "load", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("HT_GreenFlash")) {
            this.horizontal_traffic$greenFlashEnabled = tag.getBoolean("HT_GreenFlash");
        } else {
            this.horizontal_traffic$greenFlashEnabled = true;
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void onSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("HT_GreenFlash", this.horizontal_traffic$greenFlashEnabled);
    }

    @Inject(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void onTick(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (level.isClientSide()) return;
        if (this.running && this.getControlType() == TrafficLightControlType.OWN_SCHEDULE) {
            if (this.horizontal_traffic$greenFlashEnabled && this.schedule != null) {
                int currentTick = Math.max(0, this.ticker - 1);
                int rem = GreenFlashHelper.getRemainingGreenTicks(this.schedule, currentTick, this.phaseId, false);
                GreenFlashHelper.handleFlashing((TrafficLightBlockEntity) (Object) this, rem);
            }
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
