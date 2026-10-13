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
    public static boolean lastConfigCountdown = true;
    public static boolean isApplyingYellowFlash = false;

    public static int getRemainingGreenTicks(TrafficLightSchedule schedule, int currentTick, int targetPhase, boolean isRemote) {
        return getRemainingColorTicks(schedule, currentTick, targetPhase, isRemote, TrafficLightColor.GREEN);
    }

    public static int getRemainingColorTicks(TrafficLightSchedule schedule, int currentTick, int targetPhase, boolean isRemote, TrafficLightColor targetColor) {
        if (schedule == null || schedule.getEntries() == null || schedule.getEntries().isEmpty() || targetColor == null) {
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

        class Interval {
            final int start;
            final int end;
            final boolean hasColor;

            Interval(int start, int end, boolean hasColor) {
                this.start = start;
                this.end = end;
                this.hasColor = hasColor;
            }
        }

        List<Interval> intervals = new ArrayList<>();
        int cum = 0;
        for (TrafficLightScheduleEntryData e : entries) {
            int start = cum;
            cum += e.getDurationTicks();
            if (!isRemote || e.getPhaseId() == targetPhase) {
                boolean hasColor = false;
                List<TrafficLightColor> colors = e.getEnabledColors();
                if (colors != null) {
                    for (TrafficLightColor c : colors) {
                        if (c == targetColor || c.isSimilar(targetColor)) {
                            hasColor = true;
                            break;
                        }
                    }
                }
                intervals.add(new Interval(start, cum, hasColor));
            }
        }

        if (intervals.isEmpty()) {
            return -1;
        }

        int normalizedTick = currentTick % totalDuration;
        if (normalizedTick < 0) {
            normalizedTick += totalDuration;
        }

        int activeIdx = -1;
        for (int i = 0; i < intervals.size(); i++) {
            Interval it = intervals.get(i);
            if (normalizedTick >= it.start && normalizedTick < it.end) {
                activeIdx = i;
                break;
            }
        }

        if (activeIdx == -1 || !intervals.get(activeIdx).hasColor) {
            return -1;
        }

        int currRem = intervals.get(activeIdx).end - normalizedTick;
        int curr = activeIdx + 1;
        while (curr < intervals.size()) {
            Interval it = intervals.get(curr);
            if (!it.hasColor) {
                return currRem;
            }
            currRem += (it.end - it.start);
            curr++;
        }

        if (schedule.isLoop()) {
            curr = 0;
            while (curr < activeIdx) {
                Interval it = intervals.get(curr);
                if (!it.hasColor) {
                    return currRem;
                }
                currRem += (it.end - it.start);
                curr++;
            }
        }

        return -1;
    }

    public static boolean hasYellowSlot(TrafficLightBlockEntity light) {
        if (light == null) return false;
        for (int i = 0; i < light.getColorSlotCount(); i++) {
            TrafficLightColor c = light.getColorOfSlot(i);
            if (c == TrafficLightColor.YELLOW || (c != null && c.isSimilar(TrafficLightColor.YELLOW))) {
                return true;
            }
        }
        return false;
    }

    public static void updateCountdown(TrafficLightBlockEntity light, TrafficLightSchedule schedule, int currentTick, int phaseId, boolean isRemote) {
        if (light == null) return;
        IGreenFlashConfigurable config = (IGreenFlashConfigurable) light;
        if (!config.isCountdownEnabled() || config.isYellowFlashingEnabled() || !hasYellowSlot(light)) {
            resetCountdown(light);
            return;
        }

        if (schedule == null || schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            resetCountdown(light);
            return;
        }

        int greenRem = getRemainingColorTicks(schedule, currentTick, phaseId, isRemote, TrafficLightColor.GREEN);
        boolean isGreen = false;
        int remTicks = -1;

        if (greenRem > 0) {
            isGreen = true;
            remTicks = greenRem;
        } else {
            int redRem = getRemainingColorTicks(schedule, currentTick, phaseId, isRemote, TrafficLightColor.RED);
            if (redRem > 0) {
                isGreen = false;
                remTicks = redRem;
            }
        }

        int number = 0;
        if (remTicks >= 1 && remTicks <= 180) {
            number = (remTicks + 19) / 20;
        }

        setCountdownState(light, number, isGreen);
    }

    public static void resetCountdown(TrafficLightBlockEntity light) {
        if (light == null) return;
        IGreenFlashConfigurable config = (IGreenFlashConfigurable) light;
        if (config.getCountdownNumber() != 0) {
            setCountdownState(light, 0, false);
        }
    }

    public static void setCountdownState(TrafficLightBlockEntity light, int number, boolean isGreen) {
        IGreenFlashConfigurable config = (IGreenFlashConfigurable) light;
        if (config.getCountdownNumber() != number || config.isCountdownGreen() != isGreen) {
            config.setCountdownNumber(number);
            config.setCountdownGreen(isGreen);
            syncBlockEntity(light);
        }
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
            HorizontalTrafficMod.LOGGER.debug("[GreenFlash] Light at {} FLASH ON (rem={})", light.getBlockPos(), remainingTicks);
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
            HorizontalTrafficMod.LOGGER.debug("[GreenFlash] Light at {} FLASH OFF (rem={})", light.getBlockPos(), remainingTicks);
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
