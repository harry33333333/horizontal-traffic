package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightConfig.class, remap = false)
public abstract class TrafficLightConfigMixin implements IGreenFlashConfigurable {

    private boolean horizontal_traffic$greenFlashEnabled = true;

    @Override
    public boolean isGreenFlashEnabled() {
        return this.horizontal_traffic$greenFlashEnabled;
    }

    @Override
    public void setGreenFlashEnabled(boolean enabled) {
        this.horizontal_traffic$greenFlashEnabled = enabled;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"), remap = false)
    private void onInit(Level level, BlockPos blockPos, CallbackInfo ci) {
        if (level != null && blockPos != null) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (be instanceof IGreenFlashConfigurable flashConfig) {
                this.horizontal_traffic$greenFlashEnabled = flashConfig.isGreenFlashEnabled();
                return;
            }
        }
        this.horizontal_traffic$greenFlashEnabled = true;
    }
}
