package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.trafficcraft.block.data.TrafficLightColor;
import de.mrjulsen.trafficcraft.block.data.TrafficLightIcon;
import de.mrjulsen.trafficcraft.block.data.TrafficLightType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = TrafficLightIcon.class, remap = false)
public class TrafficLightIconMixin {

    @Shadow
    @Final
    @Mutable
    private static TrafficLightIcon[] $VALUES;

    @Invoker("<init>")
    public static TrafficLightIcon createTrafficLightIcon(String internalName, int internalId, String name, int index, int u, int v, TrafficLightType[] allowedInTypes, TrafficLightColor[] applicableToColors) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void onClinit(CallbackInfo ci) {
        List<TrafficLightIcon> icons = new ArrayList<>(Arrays.asList($VALUES));
        TrafficLightType[] carType = new TrafficLightType[]{TrafficLightType.CAR};
        TrafficLightColor[] standardColors = new TrafficLightColor[]{
                TrafficLightColor.RED, TrafficLightColor.YELLOW, TrafficLightColor.GREEN
        };

        // 8: U_TURN (掉头)
        icons.add(createTrafficLightIcon("U_TURN", icons.size(), "u_turn", 8, 8, 1, carType, standardColors));
        // 9: LEFT_AND_U_TURN (左转 + 掉头)
        icons.add(createTrafficLightIcon("LEFT_AND_U_TURN", icons.size(), "left_u_turn", 9, 9, 1, carType, standardColors));
        // 10: LEFT_STRAIGHT_RIGHT (左转 + 直行 + 右转)
        icons.add(createTrafficLightIcon("LEFT_STRAIGHT_RIGHT", icons.size(), "left_straight_right", 10, 10, 1, carType, standardColors));
        // 11: RIGHT_AND_U_TURN (右转 + 掉头)
        icons.add(createTrafficLightIcon("RIGHT_AND_U_TURN", icons.size(), "right_u_turn", 11, 11, 1, carType, standardColors));
        // 12: LEFT_AND_RIGHT (左转 + 右转)
        icons.add(createTrafficLightIcon("LEFT_AND_RIGHT", icons.size(), "left_right", 12, 12, 1, carType, standardColors));
        // 13: SLANTED_LEFT (斜向左转 ↖)
        icons.add(createTrafficLightIcon("SLANTED_LEFT", icons.size(), "slanted_left", 13, 13, 1, carType, standardColors));
        // 14: SLANTED_RIGHT (斜向右转 ↗)
        icons.add(createTrafficLightIcon("SLANTED_RIGHT", icons.size(), "slanted_right", 14, 14, 1, carType, standardColors));
        // 15: SLANTED_LEFT_AND_RIGHT (双向斜箭头 ↖ ↗)
        icons.add(createTrafficLightIcon("SLANTED_LEFT_AND_RIGHT", icons.size(), "slanted_left_right", 15, 15, 1, carType, standardColors));

        $VALUES = icons.toArray(new TrafficLightIcon[0]);
    }
}
