package com.trafficcraft.horizontaltraffic.mixin;

import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLNumberPicker;
import de.mrjulsen.mcdragonlib.client.gui.widgets.components.DLRichTextEditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = DLNumberPicker.class, remap = false)
public interface DLNumberPickerAccessor {
    @Invoker("updateValueFromTextbox")
    void callUpdateValueFromTextbox();

    @Accessor("textBox")
    DLRichTextEditBox getTextBox();
}
