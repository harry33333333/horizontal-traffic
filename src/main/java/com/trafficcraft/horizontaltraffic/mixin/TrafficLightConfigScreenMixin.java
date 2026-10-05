package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLGuiComponent;
import de.mrjulsen.mcdragonlib.client.gui.widgets.base.DLWindow;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLNumberPicker;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLRichTextEditBox;
import de.mrjulsen.trafficcraft.block.entity.TrafficLightBlockEntity;
import de.mrjulsen.trafficcraft.client.screen.TrafficLightConfigScreen;
import de.mrjulsen.trafficcraft.client.widgets.trafficlight.TrafficLightConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = TrafficLightConfigScreen.class, remap = false)
public abstract class TrafficLightConfigScreenMixin extends DLWindow {

    @Shadow
    private TrafficLightConfig config;

    public TrafficLightConfigScreenMixin() {
        super(null);
    }

    @Inject(method = "close", at = @At("HEAD"), remap = false)
    private void onClose(CallbackInfo ci) {
        try {
            List<DLNumberPicker> pickers = new ArrayList<>();
            findPickers(this, pickers);
            for (DLNumberPicker picker : pickers) {
                try {
                    DLRichTextEditBox textBox = ((DLNumberPickerAccessor) picker).getTextBox();
                    if (textBox != null && textBox.text.get() != null) {
                        String plain = textBox.text.get().getPlainText().trim();
                        if (!plain.isEmpty() && !plain.equals("-")) {
                            try {
                                if (config != null) {
                                    config.phaseId = (int) Double.parseDouble(plain);
                                }
                            } catch (Exception ignored) {
                            }
                        } else if (plain.isEmpty()) {
                            if (config != null) {
                                config.phaseId = 0;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }

                try {
                    ((DLNumberPickerAccessor) picker).callUpdateValueFromTextbox();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        if (config != null && config.level != null) {
            if (config.level.getBlockEntity(config.blockPos) instanceof TrafficLightBlockEntity blockEntity) {
                blockEntity.setRunning(config.scheduleEnabled);
                blockEntity.setPhaseId(config.phaseId);
                blockEntity.setControlType(config.controlType);
                blockEntity.setIcon(config.icon);
                blockEntity.setColorSlots(config.colors);
                blockEntity.enableOnlyColors(config.enabledColors);
                blockEntity.setType(config.type);
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
