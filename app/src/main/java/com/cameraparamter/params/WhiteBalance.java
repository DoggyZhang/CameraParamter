package com.cameraparamter.params;

import static android.hardware.Camera.Parameters.WHITE_BALANCE_AUTO;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_DAYLIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_FLUORESCENT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_INCANDESCENT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_SHADE;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_TWILIGHT;
import static android.hardware.Camera.Parameters.WHITE_BALANCE_WARM_FLUORESCENT;

/**
 * Created by Administrator on 2016/11/30.
 */

public enum WhiteBalance {

    AUTO(
            1, WHITE_BALANCE_AUTO),
    INCANDESCENT(
            2, WHITE_BALANCE_INCANDESCENT),
    FLUORESCENT(
            1, WHITE_BALANCE_FLUORESCENT),
    WARM_FLUORESCENT(
            1, WHITE_BALANCE_WARM_FLUORESCENT),
    DAYLIGHT(
            1, WHITE_BALANCE_DAYLIGHT),
    CLOUDY_DAYLIGHT(
            1, WHITE_BALANCE_CLOUDY_DAYLIGHT),
    TWILIGHT(
            1, WHITE_BALANCE_TWILIGHT),
    SHADE(
            1, WHITE_BALANCE_SHADE);

    private int id;
    private String name;

    WhiteBalance(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static WhiteBalance getWhiteBalanceById(int id) {
        for (WhiteBalance whiteBalance : values()) {
            if (whiteBalance.id == id) {
                return whiteBalance;
            }
        }
        return AUTO;
    }

    public static WhiteBalance getWhiteBalanceByName(String name) {
        for (WhiteBalance whiteBalance : values()) {
            if (whiteBalance.name.compareTo(name) == 0) {
                return whiteBalance;
            }
        }
        return AUTO;
    }

    @Override
    public String toString() {
        return name;
    }
}
