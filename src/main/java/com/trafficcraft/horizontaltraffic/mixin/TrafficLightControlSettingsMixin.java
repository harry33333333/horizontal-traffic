package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.client.gui.events.DLGuiStandardEvents;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLNumberPicker;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLPanel;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLRichTextEditBox;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightConfig;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightControlSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TrafficLightControlSettings.class, remap = false)
public abstract class TrafficLightControlSettingsMixin extends DLGuiComponent {

    @Shadow
    private TrafficLightConfig config;

    @Shadow
    private DLPanel settingsPanel;

    public TrafficLightControlSettingsMixin(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(int x, int y, int w, int h, TrafficLightConfig config, CallbackInfo ci) {
        this.addEventListener(DLGuiStandardEvents.TickEvent.class, (s, e) -> {
            syncPhaseIdFromComponents();
            return false;
        });
    }

    @Inject(method = "initRemoteSettings", at = @At("TAIL"), remap = false)
    private void onInitRemoteSettings(CallbackInfo ci) {
        List<DLNumberPicker> pickers = new ArrayList<>();
        findPickers(settingsPanel != null ? settingsPanel : this, pickers);
        for (DLNumberPicker picker : pickers) {
            try {
                DLRichTextEditBox textBox = ((DLNumberPickerAccessor) picker).getTextBox();
                if (textBox != null) {
                    textBox.addEventListener(DLGuiStandardEvents.KeyReleaseEvent.class, (s, e) -> {
                        syncFromTextBox(textBox);
                        return false;
                    });
                    textBox.addEventListener(DLGuiStandardEvents.CharTypeEvent.class, (s, e) -> {
                        syncFromTextBox(textBox);
                        return false;
                    });
                    textBox.addEventListener(DLGuiStandardEvents.FocusChangedEvent.class, (s, e) -> {
                        syncFromTextBox(textBox);
                        return false;
                    });
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void syncPhaseIdFromComponents() {
        if (this.config == null) return;
        List<DLNumberPicker> pickers = new ArrayList<>();
        findPickers(settingsPanel != null ? settingsPanel : this, pickers);
        for (DLNumberPicker picker : pickers) {
            try {
                DLRichTextEditBox textBox = ((DLNumberPickerAccessor) picker).getTextBox();
                syncFromTextBox(textBox);
            } catch (Exception ignored) {
            }
        }
    }

    private void syncFromTextBox(DLRichTextEditBox textBox) {
        if (config == null || textBox == null || textBox.text.get() == null) {
            return;
        }
        String plain = textBox.text.get().getPlainText().trim();
        if (!plain.isEmpty() && !plain.equals("-")) {
            try {
                int id = (int) Double.parseDouble(plain);
                config.phaseId = id;
            } catch (Exception ignored) {
            }
        }
    }

    private static void findPickers(DLGuiComponent comp, List<DLNumberPicker> list) {
        if (comp == null) return;
        if (comp instanceof DLNumberPicker picker) {
            list.add(picker);
        }
        try {
            List<DLGuiComponent> children = comp.getComponents();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    findPickers(children.get(i), list);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
