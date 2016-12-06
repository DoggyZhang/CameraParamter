package com.cameraparamter.params;

import android.hardware.Camera;

/**
 * Created by Administrator on 2016/11/30.
 */

public enum ColorEffect {

    NONE(
            0, Camera.Parameters.EFFECT_NONE),
    MONO(
            1, Camera.Parameters.EFFECT_MONO),
    SEPIA(
            2, Camera.Parameters.EFFECT_SEPIA),
    NEGATIVE(
            3, Camera.Parameters.EFFECT_NEGATIVE),
    AQUA(
            4, Camera.Parameters.EFFECT_AQUA),
    WHITEBOARD(
            5, Camera.Parameters.EFFECT_WHITEBOARD),
    BLACKBOARD(
            6, Camera.Parameters.EFFECT_BLACKBOARD),
    SOLARIZE(
            7, Camera.Parameters.EFFECT_SOLARIZE),
    POSTERIZE(
            8, Camera.Parameters.EFFECT_POSTERIZE);

    private int id;
    private String name;

    ColorEffect(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ColorEffect getColorEffectModeById(int id) {
        for (ColorEffect colorEffectMode : values()) {
            if (colorEffectMode.id == id) {
                return colorEffectMode;
            }
        }
        return ColorEffect.NONE;
    }

    public static ColorEffect getColorEffectModeByName(String name) {
        for (ColorEffect colorEffectMode : values()) {
            if (colorEffectMode.name.compareTo(name) == 0) {
                return colorEffectMode;
            }
        }
        return ColorEffect.NONE;
    }

    @Override
    public String toString() {
        return name;
    }
}
