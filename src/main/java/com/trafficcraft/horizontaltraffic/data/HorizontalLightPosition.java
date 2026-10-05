package com.trafficcraft.horizontaltraffic.data;

import net.minecraft.util.StringRepresentable;

public enum HorizontalLightPosition implements StringRepresentable {
    TOP("top", 8.0f, 16.0f, 6.0f),
    CENTER("center", 4.0f, 12.0f, 10.0f),
    BOTTOM("bottom", 0.0f, 8.0f, 14.0f);

    private final String name;
    private final float hitboxMinY;
    private final float hitboxMaxY;
    private final float renderY;

    HorizontalLightPosition(String name, float hitboxMinY, float hitboxMaxY, float renderY) {
        this.name = name;
        this.hitboxMinY = hitboxMinY;
        this.hitboxMaxY = hitboxMaxY;
        this.renderY = renderY;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public float getHitboxMinY() {
        return hitboxMinY;
    }

    public float getHitboxMaxY() {
        return hitboxMaxY;
    }

    public float getRenderY() {
        return renderY;
    }

    public HorizontalLightPosition next() {
        return switch (this) {
            case CENTER -> TOP;
            case TOP -> BOTTOM;
            case BOTTOM -> CENTER;
        };
    }
}
