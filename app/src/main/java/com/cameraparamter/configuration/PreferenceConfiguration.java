package com.cameraparamter.configuration;


import com.cameraparamter.params.FlashMode;
import com.cameraparamter.params.FocusMode;
import com.cameraparamter.params.HDRMode;
import com.cameraparamter.params.PictureFormat;
import com.cameraparamter.params.Quality;
import com.cameraparamter.params.Ratio;

/**
 * Created by Administrator on 2016/11/29.
 */

public interface PreferenceConfiguration {
    // 拍照的配置
    String CAMERA_PREFERENCE = "CAMERA_PREFERENCE";

    String PREVIEW_RATIO = "PREVIEW_RATIO";
    String PREVIEW_RATIO_DEFAULT = Ratio.R_16x9.toString();

    String FLASH_MODE = "FLASH_MODE";
    String FLASH_MODE_DEFAULT = FlashMode.ON.toString();

    String PICTURE_QUALITY = "PICTURE_QUALITY";
    String PICTURE_QUALITY_DEFAULT = Quality.HIGH.toString();

    String PICTURE_FORMAT = "picture_format";
    String PICTURE_FORMAT_DEFAULT = PictureFormat.JPEG.toString();

    String VIDEO_RATIO = "videoSize";
    String VIDEO_RATIO_DEFAULT = Ratio.R_16x9.toString();

    String FOCUS_MODE = "FOCUS_MODE";
    String FOCUS_MODE_DEFAULT = FocusMode.AUTO.toString();

    String HDR = "HDR";
    String HDR_DEFAULT = HDRMode.OFF.toString();

    // 录像的配置
    String RECORD_PREFERENCE = "RECORD_PREFERENCE";

    String AUDIO_MODE = "AUDIO_MODE";
    String AUDIO_MODE_DEFAULT = "AUDIO_MODE";

    String VIDEO_QUALITY = "VIDEO_QUALITY";
    String VIDEO_QUALITY_DEFAULT = "VIDEO_QUALITY";

}
