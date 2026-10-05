package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.client.gui.widgets.layout.FlowLayout;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.OptionsPanel;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightConfig;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightGeneralSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrafficLightGeneralSettings.class, remap = false)
public class TrafficLightGeneralSettingsMixin {

    @Shadow
    private OptionsPanel iconPanel;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(int x, int y, int width, int height, TrafficLightConfig config, CallbackInfo ci) {
        adjustIconPanel();
    }

    @Inject(method = "reloadIcons", at = @At("TAIL"), remap = false)
    private void onReloadIcons(CallbackInfo ci) {
        adjustIconPanel();
    }

    private void adjustIconPanel() {
        if (this.iconPanel != null) {
            this.iconPanel.setWidth(146);
            if (this.iconPanel.layout.get() instanceof FlowLayout flowLayout) {
                flowLayout.wrap.set(true);
            }
        }
    }
}
