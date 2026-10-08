package com.trafficcraft.horizontaltraffic.util;

import com.trafficcraft.horizontaltraffic.HorizontalTrafficMod;
import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.data.TrafficLightSchedule;
import de.mrjulsen.trafficcraft.data.TrafficLightScheduleEntryData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class GreenFlashHelper {

    public static boolean lastConfigGreenFlash = true;
    public static boolean lastConfigYellowFlash = false;
    public static boolean isApplyingYellowFlash = false;

    public static int getRemainingGreenTicks(TrafficLightSchedule schedule, int currentTick, int targetPhase, boolean isRemote) {
        if (schedule == null || schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            return -1;
        }

        List<TrafficLightScheduleEntryData> entries = schedule.getEntries();
        int totalDuration = 0;
        for (TrafficLightScheduleEntryData e : entries) {
            totalDuration += e.getDurationTicks();
        }
        if (totalDuration <= 0) {
            return -1;
        }

        class PhaseEvent {
            final int cumTick;
            final boolean hasGreen;

            PhaseEvent(int cumTick, boolean hasGreen) {
                this.cumTick = cumTick;
                this.hasGreen = hasGreen;
            }
        }

        List<PhaseEvent> events = new ArrayList<>();
        int cum = 0;
        for (TrafficLightScheduleEntryData e : entries) {
            cum += e.getDurationTicks();
            if (!isRemote || e.getPhaseId() == targetPhase) {
                boolean hasGreen = false;
                List<TrafficLightColor> colors = e.getEnabledColors();
                if (colors != null) {
                    for (TrafficLightColor c : colors) {
                        if (c == TrafficLightColor.GREEN || c.isSimilar(TrafficLightColor.GREEN)) {
                            hasGreen = true;
                            break;
                        }
                    }
                }
                events.add(new PhaseEvent(cum, hasGreen));
            }
        }

        if (events.isEmpty()) {
            return -1;
        }

        int normalizedTick = currentTick % totalDuration;
        if (normalizedTick < 0) {
            normalizedTick += totalDuration;
        }

        // Active event for currentTick
        PhaseEvent activeEvent = null;
        for (PhaseEvent ev : events) {
            if (normalizedTick >= ev.cumTick) {
                activeEvent = ev;
            }
        }
        if (activeEvent == null) {
            activeEvent = events.get(events.size() - 1);
        }

        if (!activeEvent.hasGreen) {
            return -1;
        }

        // Find next event without green
        for (PhaseEvent ev : events) {
            if (ev.cumTick > normalizedTick) {
                if (!ev.hasGreen) {
                    return ev.cumTick - normalizedTick;
                }
            }
        }

        if (schedule.isLoop()) {
            for (PhaseEvent ev : events) {
                if (!ev.hasGreen) {
                    return (totalDuration - normalizedTick) + ev.cumTick;
                }
            }
        }

        return -1;
    }

    public static void handleFlashing(TrafficLightBlockEntity light, int remainingTicks) {
        if (light == null || remainingTicks <= 0 || remainingTicks > 60) {
            return;
        }

        // In the final 60 ticks (3 seconds):
        // 60..51: OFF (0.5s)
        // 50..41: ON  (0.5s, flash 1)
        // 40..31: OFF (0.5s)
        // 30..21: ON  (0.5s, flash 2)
        // 20..11: OFF (0.5s)
        // 10..1:  ON  (0.5s, flash 3)
        // 0: naturally switches to Yellow!
        int halfSecIndex = (remainingTicks - 1) / 10;
        boolean shouldBeOn = (halfSecIndex % 2 == 0);

        boolean isCurrentlyOn = light.isColorEnabled(TrafficLightColor.GREEN, true);
        if (shouldBeOn && !isCurrentlyOn) {
            light.enableColors(List.of(TrafficLightColor.GREEN));
            syncBlockEntity(light);
            HorizontalTrafficMod.LOGGER.info("[GreenFlash] Light at {} FLASH ON (rem={})", light.getBlockPos(), remainingTicks);
        } else if (!shouldBeOn && isCurrentlyOn) {
            List<TrafficLightColor> toDisable = new ArrayList<>();
            for (TrafficLightColor c : light.getEnabledColors()) {
                if (c == TrafficLightColor.GREEN || c.isSimilar(TrafficLightColor.GREEN)) {
                    toDisable.add(c);
                }
            }
            if (!toDisable.isEmpty()) {
                light.disableColors(toDisable);
            } else {
                light.disableColors(List.of(TrafficLightColor.GREEN));
            }
            syncBlockEntity(light);
            HorizontalTrafficMod.LOGGER.info("[GreenFlash] Light at {} FLASH OFF (rem={})", light.getBlockPos(), remainingTicks);
        }
    }

    public static void handleYellowFlashing(TrafficLightBlockEntity light, Level level) {
        if (light == null || level == null || level.isClientSide()) {
            return;
        }

        // 1s flash cycle (20 ticks): 0..9 (10 ticks / 0.5s) ON, 10..19 (10 ticks / 0.5s) OFF
        long gameTime = level.getGameTime();
        boolean shouldBeOn = (gameTime % 20) < 10;

        boolean isYellowOn = light.isColorEnabled(TrafficLightColor.YELLOW, true);
        boolean hasNonYellow = false;
        for (TrafficLightColor c : light.getEnabledColors()) {
            if (c != TrafficLightColor.YELLOW && !c.isSimilar(TrafficLightColor.YELLOW)) {
                hasNonYellow = true;
                break;
            }
        }

        try {
            isApplyingYellowFlash = true;
            if (shouldBeOn) {
                if (!isYellowOn || hasNonYellow) {
                    light.enableOnlyColors(List.of(TrafficLightColor.YELLOW));
                    syncBlockEntity(light);
                }
            } else {
                if (isYellowOn || hasNonYellow) {
                    light.enableOnlyColors(List.of());
                    syncBlockEntity(light);
                }
            }
        } finally {
            isApplyingYellowFlash = false;
        }
    }

    public static void syncBlockEntity(TrafficLightBlockEntity light) {
        Level level = light.getLevel();
        if (level != null && !level.isClientSide()) {
            BlockPos pos = light.getBlockPos();
            BlockState state = light.getBlockState();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }
}
