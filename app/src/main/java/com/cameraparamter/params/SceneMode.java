package com.cameraparamter.params;

import static android.hardware.Camera.Parameters.SCENE_MODE_ACTION;
import static android.hardware.Camera.Parameters.SCENE_MODE_AUTO;
import static android.hardware.Camera.Parameters.SCENE_MODE_BARCODE;
import static android.hardware.Camera.Parameters.SCENE_MODE_BEACH;
import static android.hardware.Camera.Parameters.SCENE_MODE_CANDLELIGHT;
import static android.hardware.Camera.Parameters.SCENE_MODE_FIREWORKS;
import static android.hardware.Camera.Parameters.SCENE_MODE_LANDSCAPE;
import static android.hardware.Camera.Parameters.SCENE_MODE_NIGHT;
import static android.hardware.Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT;
import static android.hardware.Camera.Parameters.SCENE_MODE_PARTY;
import static android.hardware.Camera.Parameters.SCENE_MODE_PORTRAIT;
import static android.hardware.Camera.Parameters.SCENE_MODE_SNOW;
import static android.hardware.Camera.Parameters.SCENE_MODE_SPORTS;
import static android.hardware.Camera.Parameters.SCENE_MODE_STEADYPHOTO;
import static android.hardware.Camera.Parameters.SCENE_MODE_SUNSET;
import static android.hardware.Camera.Parameters.SCENE_MODE_THEATRE;

/**
 * Created by Administrator on 2016/11/30.
 */

public enum SceneMode {

    AUTO(
            1, SCENE_MODE_AUTO),
    ACTION(
            2, SCENE_MODE_ACTION),
    PORTRAIT(
            3, SCENE_MODE_PORTRAIT),
    LANDSCAPE(
            4, SCENE_MODE_LANDSCAPE),
    NIGHT(
            5, SCENE_MODE_NIGHT),
    NIGHT_PORTRAIT(
            6, SCENE_MODE_NIGHT_PORTRAIT),
    THEATRE(
            7, SCENE_MODE_THEATRE),
    BEACH(
            8, SCENE_MODE_BEACH),
    SNOW(
            9, SCENE_MODE_SNOW
    ),
    SUNSET(
            10, SCENE_MODE_SUNSET
    ),
    STEADYPHOTO(
            11, SCENE_MODE_STEADYPHOTO
    ),
    FIREWORKS(
            12, SCENE_MODE_FIREWORKS
    ),
    SPORTS(
            13, SCENE_MODE_SPORTS
    ),
    PARTY(
            14, SCENE_MODE_PARTY
    ),
    CANDLELIGHT(
            15, SCENE_MODE_CANDLELIGHT
    ),
    BARCODE(
            16, SCENE_MODE_BARCODE
    );

    private int id;
    private String name;

    SceneMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SceneMode getSceneModeById(int id) {
        for (SceneMode sceneMode : values()) {
            if (sceneMode.id == id) {
                return sceneMode;
            }
        }
        return AUTO;
    }

    public static SceneMode getSceneModeByName(String name) {
        for (SceneMode sceneMode : values()) {
            if (sceneMode.name.compareTo(name) == 0) {
                return sceneMode;
            }
        }
        return AUTO;
    }

    @Override
    public String toString() {
        return name;
    }
}
