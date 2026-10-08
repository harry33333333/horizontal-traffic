package com.trafficcraft.horizontaltraffic.util;

import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.data.TrafficLightSchedule;
import de.mrjulsen.trafficcraft.data.TrafficLightScheduleEntryData;

import java.util.ArrayList;
import java.util.List;

public class GreenFlashHelper {

    public static boolean lastConfigGreenFlash = true;

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

        // Active event for currentTick
        PhaseEvent activeEvent = null;
        for (PhaseEvent ev : events) {
            if (currentTick >= ev.cumTick) {
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
            if (ev.cumTick > currentTick) {
                if (!ev.hasGreen) {
                    return ev.cumTick - currentTick;
                }
            }
        }

        for (PhaseEvent ev : events) {
            if (!ev.hasGreen) {
                return (totalDuration - currentTick) + ev.cumTick;
            }
        }

        return -1;
    }

    public static void handleFlashing(TrafficLightBlockEntity light, int remainingTicks) {
        if (light == null || remainingTicks <= 0 || remainingTicks > 60) {
            return;
        }

        // In the final 60 ticks (3 seconds):
        // 60..51: ON  (half-sec 5)
        // 50..41: OFF (half-sec 4)
        // 40..31: ON  (half-sec 3)
        // 30..21: OFF (half-sec 2)
        // 20..11: ON  (half-sec 1)
        // 10..1:  OFF (half-sec 0)
        // At 0: naturally switches to next phase (Yellow)
        int halfSecIndex = (remainingTicks - 1) / 10;
        boolean shouldBeOn = (halfSecIndex % 2 == 1);

        boolean isCurrentlyOn = light.isColorEnabled(TrafficLightColor.GREEN, true);
        if (shouldBeOn && !isCurrentlyOn) {
            light.enableColors(List.of(TrafficLightColor.GREEN));
        } else if (!shouldBeOn && isCurrentlyOn) {
            light.disableColors(List.of(TrafficLightColor.GREEN));
        }
    }
}
