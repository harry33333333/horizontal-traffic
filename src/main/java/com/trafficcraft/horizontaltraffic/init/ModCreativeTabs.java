package com.trafficcraft.horizontaltraffic.init;

import com.trafficcraft.horizontaltraffic.HorizontalTrafficMod;
import de.mrjulsen.trafficcraft.registry.ModCreativeModeTab;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final CreativeModeTab HORIZONTAL_TRAFFIC_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.HORIZONTAL_TRAFFIC_LIGHT))
            .title(Component.translatable("itemGroup.horizontal_traffic.tab"))
            .displayItems((params, output) -> {
                output.accept(ModItems.HORIZONTAL_TRAFFIC_LIGHT);
                output.accept(ModItems.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
                output.accept(ModItems.HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
            })
            .build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(HorizontalTrafficMod.MOD_ID, "main"),
                HORIZONTAL_TRAFFIC_TAB
        );

        if (ModCreativeModeTab.MOD_TAB != null && ModCreativeModeTab.MOD_TAB.getKey() != null) {
            ItemGroupEvents.modifyEntriesEvent(ModCreativeModeTab.MOD_TAB.getKey()).register(entries -> {
                entries.accept(ModItems.HORIZONTAL_TRAFFIC_LIGHT);
                entries.accept(ModItems.HORIZONTAL_DOUBLE_TRAFFIC_LIGHT);
                entries.accept(ModItems.HORIZONTAL_SINGLE_TRAFFIC_LIGHT);
            });
        }
    }
}
