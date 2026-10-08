package com.trafficcraft.horizontaltraffic.mixin;

import com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable;
import de.mrjulsen.mcdragonlib.network.NetworkPacketContext;
import de.mrjulsen.trafficcraft.network.packets.cts.TrafficLightPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrafficLightPacket.class)
public class TrafficLightPacketMixin implements IGreenFlashConfigurable {

    private boolean horizontal_traffic$greenFlashEnabled = true;
    private boolean horizontal_traffic$yellowFlashingEnabled = false;

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
        this.horizontal_traffic$yellowFlashingEnabled = enabled;
    }

    @Inject(method = "<init>(Lnet/minecraft/core/BlockPos;Ljava/util/Collection;Lde/mrjulsen/trafficcraft/block/data/TrafficLightType;Lde/mrjulsen/trafficcraft/block/data/TrafficLightModel;Lde/mrjulsen/trafficcraft/block/data/TrafficLightIcon;Lde/mrjulsen/trafficcraft/block/data/TrafficLightControlType;[Lde/mrjulsen/trafficcraft/block/data/TrafficLightColor;IZ)V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.horizontal_traffic$greenFlashEnabled = com.trafficcraft.horizontaltraffic.util.GreenFlashHelper.lastConfigGreenFlash;
        this.horizontal_traffic$yellowFlashingEnabled = com.trafficcraft.horizontaltraffic.util.GreenFlashHelper.lastConfigYellowFlash;
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void onWrite(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("HT_GreenFlash", this.horizontal_traffic$greenFlashEnabled);
        tag.putBoolean("HT_YellowFlash", this.horizontal_traffic$yellowFlashingEnabled);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void onRead(CompoundTag tag, CallbackInfo ci) {
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
    }

    @Inject(method = "handle", at = @At("TAIL"), remap = false)
    private static void onHandle(TrafficLightPacket packet, NetworkPacketContext context, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        if (player != null) {
            Level level = player.level();
            BlockPos pos = ((TrafficLightPacketAccessor) packet).getPos();
            if (pos != null && level.isLoaded(pos)) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IGreenFlashConfigurable flashBe && packet instanceof IGreenFlashConfigurable flashPacket) {
                    flashBe.setGreenFlashEnabled(flashPacket.isGreenFlashEnabled());
                    flashBe.setYellowFlashingEnabled(flashPacket.isYellowFlashingEnabled());
                    be.setChanged();
                }

                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            }
        }
    }
}
