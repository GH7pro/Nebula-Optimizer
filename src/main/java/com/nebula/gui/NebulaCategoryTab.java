package com.nebula.gui;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class NebulaCategoryTab extends ButtonWidget {
    private String icon;
    private boolean selected = false;
    
    public NebulaCategoryTab(int x, int y, int width, int height, String icon, String text, PressAction onPress) {
        super(x, y, width, height, Text.literal(icon + " " + text), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.icon = icon;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }
}
