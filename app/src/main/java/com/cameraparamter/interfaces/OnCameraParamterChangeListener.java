package com.cameraparamter.interfaces;


import com.cameraparamter.configuration.CameraParamter;
import com.cameraparamter.params.ColorEffect;
import com.cameraparamter.params.FocusMode;
import com.cameraparamter.params.Quality;
import com.cameraparamter.params.Ratio;
import com.cameraparamter.params.SceneMode;
import com.cameraparamter.params.WhiteBalance;

/**
 * Created by Administrator on 2016/11/30.
 */

public interface OnCameraParamterChangeListener {
    void onFaceDetectChange(CameraParamter cameraParamter, boolean isOpen);

    void onFocusModeChange(CameraParamter cameraParamter, FocusMode focusMode);

    void onPreviewRatioChange(CameraParamter cameraParamter, Ratio ratio);

    void onPictureRatioChange(CameraParamter cameraParamter, Ratio ratio);

    void onPictureQualityChange(CameraParamter cameraParamter, Quality quality);

    void onColorEffectChange(CameraParamter cameraParamter, ColorEffect colorEffect);

    void onSceneModeChange(CameraParamter cameraParamter, SceneMode sceneMode);

    void onWhiteBalance(CameraParamter cameraParamter, WhiteBalance whiteBalance);

    void onAntiBindingChange(CameraParamter cameraParamter, String antiBinding);

    void onVideoQualityChange(CameraParamter cameraParamter, Quality quality);
}
