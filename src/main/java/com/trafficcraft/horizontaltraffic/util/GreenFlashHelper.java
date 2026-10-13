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

    public static class ScheduleTimelineResult {
        public final int remainingTicks;
        public final boolean isGreen;

        public ScheduleTimelineResult(int remainingTicks, boolean isGreen) {
            this.remainingTicks = remainingTicks;
            this.isGreen = isGreen;
        }
    }

    public static ScheduleTimelineResult calculateRemainingTicks(TrafficLightSchedule schedule, int currentTick, int targetPhase, boolean isRemote) {
        if (schedule == null || schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            return new ScheduleTimelineResult(-1, false);
        }

        List<TrafficLightScheduleEntryData> entries = schedule.getEntries();
        int totalDuration = 0;
        for (TrafficLightScheduleEntryData e : entries) {
            totalDuration += e.getDurationTicks();
        }
        if (totalDuration <= 0) {
            return new ScheduleTimelineResult(-1, false);
        }

        class ChangeEvent {
            final int tick;
            final boolean hasGreen;
            final boolean hasRed;
            final boolean hasYellow;

            ChangeEvent(int tick, boolean hasGreen, boolean hasRed, boolean hasYellow) {
                this.tick = tick;
                this.hasGreen = hasGreen;
                this.hasRed = hasRed;
                this.hasYellow = hasYellow;
            }
        }

        List<ChangeEvent> events = new ArrayList<>();
        int cum = 0;
        for (TrafficLightScheduleEntryData e : entries) {
            cum += e.getDurationTicks();
            if (!isRemote || e.getPhaseId() == targetPhase) {
                boolean hasGreen = false;
                boolean hasRed = false;
                boolean hasYellow = false;
                List<TrafficLightColor> colors = e.getEnabledColors();
                if (colors != null) {
                    for (TrafficLightColor c : colors) {
                        if (c == TrafficLightColor.GREEN || (c != null && c.isSimilar(TrafficLightColor.GREEN))) {
                            hasGreen = true;
                        } else if (c == TrafficLightColor.RED || (c != null && c.isSimilar(TrafficLightColor.RED))) {
                            hasRed = true;
                        } else if (c == TrafficLightColor.YELLOW || (c != null && c.isSimilar(TrafficLightColor.YELLOW))) {
                            hasYellow = true;
                        }
                    }
                }
                events.add(new ChangeEvent(cum % totalDuration, hasGreen, hasRed, hasYellow));
            }
        }

        if (events.isEmpty()) {
            return new ScheduleTimelineResult(-1, false);
        }

        // Stable sort by trigger tick
        events.sort((a, b) -> Integer.compare(a.tick, b.tick));

        // Deduplicate events occurring at the exact same tick: later entry in schedule order wins
        List<ChangeEvent> dedup = new ArrayList<>();
        for (ChangeEvent ev : events) {
            if (!dedup.isEmpty() && dedup.get(dedup.size() - 1).tick == ev.tick) {
                dedup.set(dedup.size() - 1, ev);
            } else {
                dedup.add(ev);
            }
        }
        events = dedup;

        int normT = currentTick % totalDuration;
        if (normT < 0) normT += totalDuration;

        // Active event is the last event with ev.tick <= normT
        // If none, it wraps around from the previous cycle, so it is events.get(last)
        int activeIdx = -1;
        for (int i = events.size() - 1; i >= 0; i--) {
            if (events.get(i).tick <= normT) {
                activeIdx = i;
                break;
            }
        }
        if (activeIdx == -1) {
            activeIdx = events.size() - 1;
        }

        ChangeEvent active = events.get(activeIdx);
        // Yellow phase does not count down!
        if (active.hasYellow) {
            return new ScheduleTimelineResult(-1, false);
        }

        boolean isGreen = active.hasGreen;
        if (!isGreen && !active.hasRed) {
            return new ScheduleTimelineResult(-1, false);
        }

        // Find the next event that changes this color
        int n = events.size();
        int rem = 0;
        boolean foundEnd = false;
        for (int step = 1; step <= n; step++) {
            int nextIdx = (activeIdx + step) % n;
            ChangeEvent nextEv = events.get(nextIdx);
            boolean hasSameColor = isGreen ? nextEv.hasGreen : nextEv.hasRed;
            if (!hasSameColor || nextEv.hasYellow) {
                int nextT = nextEv.tick;
                if (nextT > normT) {
                    rem = nextT - normT;
                } else {
                    if (!schedule.isLoop()) {
                        return new ScheduleTimelineResult(-1, false);
                    }
                    rem = (totalDuration - normT) + nextT;
                }
                foundEnd = true;
                break;
            }
        }

        if (!foundEnd) {
            return new ScheduleTimelineResult(-1, false);
        }

        return new ScheduleTimelineResult(rem, isGreen);
    }

    public static int getRemainingGreenTicks(TrafficLightSchedule schedule, int currentTick, int targetPhase, boolean isRemote) {
        ScheduleTimelineResult res = calculateRemainingTicks(schedule, currentTick, targetPhase, isRemote);
        return res.isGreen ? res.remainingTicks : -1;
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

    public static void updateLightState(TrafficLightBlockEntity light, TrafficLightSchedule schedule, int currentTick, int phaseId, boolean isRemote) {
        if (light == null) return;
        IGreenFlashConfigurable config = (IGreenFlashConfigurable) light;

        if (config.isYellowFlashingEnabled()) {
            handleYellowFlashing(light, light.getLevel());
            resetCountdown(light);
            return;
        }

        // User requirement: When yellow light is lit, countdown does NOT display!
        if (light.isColorEnabled(TrafficLightColor.YELLOW, true)) {
            resetCountdown(light);
            return;
        }

        if (schedule == null || schedule.getEntries() == null || schedule.getEntries().isEmpty()) {
            resetCountdown(light);
            return;
        }

        ScheduleTimelineResult res = calculateRemainingTicks(schedule, currentTick, phaseId, isRemote);

        // Handle green flashing
        if (res.isGreen) {
            if (config.isGreenFlashEnabled() && res.remainingTicks > 0 && res.remainingTicks <= 60) {
                handleFlashing(light, res.remainingTicks);
            }
        }

        // Countdown: only on lights with a yellow slot
        if (!config.isCountdownEnabled() || !hasYellowSlot(light)) {
            resetCountdown(light);
            return;
        }

        int number = 0;
        if (res.remainingTicks >= 1 && res.remainingTicks <= 180) {
            number = (res.remainingTicks + 19) / 20;
        }

        setCountdownState(light, number, res.isGreen);
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

        int halfSecIndex = (remainingTicks - 1) / 10;
        boolean shouldBeOn = (halfSecIndex % 2 == 0);

        boolean isCurrentlyOn = light.isColorEnabled(TrafficLightColor.GREEN, true);
        if (shouldBeOn && !isCurrentlyOn) {
            light.enableOnlyColors(List.of(TrafficLightColor.GREEN));
            syncBlockEntity(light);
            HorizontalTrafficMod.LOGGER.debug("[GreenFlash] Light at {} FLASH ON (rem={})", light.getBlockPos(), remainingTicks);
        } else if (!shouldBeOn && isCurrentlyOn) {
            light.enableOnlyColors(List.of());
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
