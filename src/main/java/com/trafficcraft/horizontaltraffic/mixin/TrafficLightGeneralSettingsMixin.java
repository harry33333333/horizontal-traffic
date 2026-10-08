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

        de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLPanel bottomPanel =
                new de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLPanel(0, 0, 146, 16);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.flowDirection.set(FlowLayout.Direction.HORIZONTAL);
        flowLayout.padding.set(new de.mrjulsen.mcdragonlib.client.gui.widgets.richtext.Padding(0, 0, 0, 0));
        flowLayout.wrap.set(true);
        bottomPanel.layout.set(flowLayout);

        de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCheckBox greenBox =
                new de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCheckBox(0, 0, 73, 14);
        greenBox.text.set(net.minecraft.network.chat.Component.translatable("gui.horizontal_traffic.green_flash"));
        if (config instanceof com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable flashConfig) {
            greenBox.checked.set(flashConfig.isGreenFlashEnabled());
            greenBox.addEventListener(de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLToggleButton.CheckedChangedEvent.class, (comp, evt) -> {
                flashConfig.setGreenFlashEnabled(evt.checked());
                return false;
            });
        }
        bottomPanel.addComponent(greenBox);

        de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCheckBox yellowBox =
                new de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLCheckBox(0, 0, 73, 14);
        yellowBox.text.set(net.minecraft.network.chat.Component.translatable("gui.horizontal_traffic.yellow_flash"));
        if (config instanceof com.trafficcraft.horizontaltraffic.util.IGreenFlashConfigurable flashConfig) {
            yellowBox.checked.set(flashConfig.isYellowFlashingEnabled());
            yellowBox.addEventListener(de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLToggleButton.CheckedChangedEvent.class, (comp, evt) -> {
                flashConfig.setYellowFlashingEnabled(evt.checked());
                return false;
            });
        }
        bottomPanel.addComponent(yellowBox);

        ((de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent) (Object) this).addComponent(bottomPanel);
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
