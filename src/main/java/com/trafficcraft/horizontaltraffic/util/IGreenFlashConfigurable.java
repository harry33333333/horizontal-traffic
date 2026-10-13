package com.trafficcraft.horizontaltraffic.util;

public interface IGreenFlashConfigurable {
    boolean isGreenFlashEnabled();
    void setGreenFlashEnabled(boolean enabled);

    boolean isYellowFlashingEnabled();
    void setYellowFlashingEnabled(boolean enabled);

    boolean isCountdownEnabled();
    void setCountdownEnabled(boolean enabled);

    int getCountdownNumber();
    void setCountdownNumber(int number);

    boolean isCountdownGreen();
    void setCountdownGreen(boolean isGreen);
}
