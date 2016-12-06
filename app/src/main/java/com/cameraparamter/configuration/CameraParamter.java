package com.cameraparamter.configuration;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.cameraparamter.params.ColorEffect;
import com.cameraparamter.params.FaceDetect;
import com.cameraparamter.params.FlashMode;
import com.cameraparamter.params.FocusMode;
import com.cameraparamter.params.HDRMode;
import com.cameraparamter.params.PictureFormat;
import com.cameraparamter.params.Quality;
import com.cameraparamter.params.Ratio;
import com.cameraparamter.params.SceneMode;
import com.cameraparamter.params.WhiteBalance;
import com.cameraparamter.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class CameraParamter {
    private static final String TAG = "CameraParamter";

    private static CameraParamter mCameraParamter = null;

    private Context mContext;
    private Camera.Parameters mParameters;

    private Ratio previewRatio;
    private boolean supportPreviewSize;
    private Map<Ratio, Size> previewSizes;

    private Ratio pictureRatio;
    private Quality pictureQuality;
    private boolean supportPictureSize;
    private Map<Ratio, Map<Quality, Size>> pictureSizes;

    private PictureFormat pictureFormat;
    private boolean supportPictureFormat = false;
    private List<PictureFormat> pictureFormats;

    private Ratio videoRatio;
    private Quality videoQuality;
    private boolean supportVideoSize = false;
    private Map<Ratio, Map<Quality, Size>> videoQualitySizes;
    private Map<Ratio, Size> videoSizes;

    private List<Integer> zoomRatios;
    private int zoomIndex;
    private int minZoomIndex;
    private int maxZoomIndex;

    private FocusMode focusMode;
    private boolean supportFocusMode = false;
    private List<FocusMode> focusModes;

    private HDRMode hdrMode;
    private boolean supportedHDR = false;

    private FlashMode flashMode;
    private boolean supportedFlash = false;
    private final List<FlashMode> flashModes;

    private ColorEffect colorEffect;
    private boolean supportedColorEffect = false;
    private List<ColorEffect> colorEffects;

    private SceneMode sceneMode;
    private boolean supportedSceneMode = false;
    private List<SceneMode> sceneModes;

    private WhiteBalance whiteBalance;
    private boolean supportedWhiteBalance = false;
    private List<WhiteBalance> whiteBalances;

    private String antiBinding;
    private boolean supportedAntiBinding = false;
    private List<String> antiBindings;

    private FaceDetect faceDetect;
    private boolean supportFaceDetect = false;

    private boolean isInit = false;

    public static CameraParamter getInstance(Context context, Camera.Parameters parameters) {
        if (mCameraParamter == null) {
            mCameraParamter = new CameraParamter(context, parameters);
            return mCameraParamter;
        } else {
            return mCameraParamter;
        }
    }

    private CameraParamter(Context context, Camera.Parameters parameters) {
        if (context == null) {
            throw new RuntimeException("CameraParamter(Camera camera) argumens exception : activity is null !!!");
        }
        if (parameters == null) {
            throw new RuntimeException("CameraParamter(Camera camera) argumens exception : camera is null !!!");
        }
        this.mContext = context;
        this.mParameters = parameters;
        Log.e(TAG, "CameraParamter: \n " + mParameters.flatten());

        zoomRatios = mParameters.getZoomRatios();
        zoomIndex = minZoomIndex = 0;
        maxZoomIndex = mParameters.getMaxZoom();

        this.previewSizes = buildPreviewSizesRatioMap(mParameters.getSupportedPreviewSizes());
        if (this.previewSizes == null || this.previewSizes.size() <= 1) {
            supportPreviewSize = false;
        } else {
            supportPreviewSize = true;
        }

        this.pictureSizes = buildPictureSizesRatioMap(mParameters.getSupportedPictureSizes());
        if (this.pictureSizes == null || this.pictureSizes.size() <= 1) {
            supportPictureSize = false;
        } else {
            supportPictureSize = true;
        }

        this.pictureFormats = buildPictureFormatList(mParameters.getSupportedPictureFormats());
        if (this.pictureFormats == null || this.pictureFormats.size() <= 1) {
            supportPictureFormat = false;
        } else {
            supportPictureFormat = true;
        }

        videoQualitySizes = buildVideoSizesRatioMap(mParameters.getSupportedVideoSizes());
        this.videoSizes = buildVideoSizesRatio(mParameters.getSupportedVideoSizes());
        if (this.videoSizes == null || this.videoSizes.size() <= 1) {
            supportVideoSize = false;
        } else {
            supportVideoSize = true;
        }

        this.focusModes = buildFocusModeList(mParameters.getSupportedFocusModes());
        if (this.focusModes == null || this.focusModes.size() <= 1) {
            supportFocusMode = false;
        } else {
            supportFocusMode = true;
        }

        List<String> sceneModes = mParameters.getSupportedSceneModes();
        if (sceneModes != null) {
            for (String mode : sceneModes) {
                if (mode.equals(Camera.Parameters.SCENE_MODE_HDR)) {
                    supportedHDR = true;
                    break;
                }
            }
        }

        this.flashModes = buildFlashModeList(mParameters.getSupportedFlashModes());
        if (this.flashModes == null || this.flashModes.size() <= 1) { /* Device has no flash */
            supportedFlash = false;
        } else {
            supportedFlash = true;
        }

        this.colorEffects = buildColorEffectList(mParameters.getSupportedColorEffects());
        if (this.colorEffects == null || this.colorEffects.size() <= 1) {
            supportedColorEffect = false;
        } else {
            supportedColorEffect = true;
        }

        this.sceneModes = buildSceneModeList(mParameters.getSupportedSceneModes());
        if (this.sceneModes == null || this.sceneModes.size() <= 1) {
            supportedSceneMode = false;
        } else {
            supportedSceneMode = true;
        }

        this.whiteBalances = buildWhiteBalance(mParameters.getSupportedWhiteBalance());
        if (this.whiteBalances == null || this.whiteBalances.size() <= 1) {
            supportedWhiteBalance = false;
        } else {
            supportedWhiteBalance = true;
        }

        this.antiBindings = mParameters.getSupportedAntibanding();
        if (this.antiBindings == null || this.antiBindings.size() <= 1) {
            supportedAntiBinding = false;
        } else {
            supportedAntiBinding = true;
        }

        // API 14
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            supportFaceDetect = true;
        } else {
            supportFaceDetect = false;
        }

    }

    public Camera.Parameters getParameters() {
        if (mParameters != null) {
            return mParameters;
        } else {
            Log.e(TAG, "CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
            return null;
        }
    }

    /**
     * 初始化参数
     */
    public void initParams() {
        isInit = true;
        /**
         * 从 SharePreference 中读取相机配置信息
         */
        if (supportPreviewSize) {
//            String pre_previewRatio = PreferenceUtils.get(mContext, PreferenceConfiguration.PREVIEW_RATIO, PreferenceConfiguration.PREVIEW_RATIO_DEFAULT);
//            Ratio ratioByName = Ratio.getRatioByName(pre_previewRatio);
//            setPreviewSize(ratioByName, 0);
            setPreviewSize(Ratio.R_16x9, 0);
        }

        if (supportPictureSize) {
            String pre_pictureQuality = PreferenceUtils.get(mContext, PreferenceConfiguration.PICTURE_QUALITY, PreferenceConfiguration.PICTURE_QUALITY_DEFAULT);
            Quality qualityByName = Quality.getQualityByName(pre_pictureQuality);
            String pre_previewRatio = PreferenceUtils.get(mContext, PreferenceConfiguration.PREVIEW_RATIO, PreferenceConfiguration.PREVIEW_RATIO_DEFAULT);
            Ratio ratioByName = Ratio.getRatioByName(pre_previewRatio);
            setPictureSize(ratioByName, qualityByName);
        }

        if (supportPictureFormat) {
            String pre_pictureFormat = PreferenceUtils.get(mContext, PreferenceConfiguration.PICTURE_FORMAT, PreferenceConfiguration.PICTURE_FORMAT_DEFAULT);
            PictureFormat formatByName = PictureFormat.getPictureFormatByName(pre_pictureFormat);
            setPictureFormat(formatByName);
        }

        if (supportVideoSize) {
            String pre_videoRatio = PreferenceUtils.get(mContext, PreferenceConfiguration.VIDEO_RATIO, PreferenceConfiguration.VIDEO_RATIO_DEFAULT);
            Ratio ratioByName = Ratio.getRatioByName(pre_videoRatio);
            setVideoSizeRatio(ratioByName);
        }

        if (supportFocusMode) {
            String pre_focusMdoe = PreferenceUtils.get(mContext, PreferenceConfiguration.FOCUS_MODE, PreferenceConfiguration.FOCUS_MODE_DEFAULT);
            FocusMode focusModeByName = FocusMode.getFocusModeByName(pre_focusMdoe);
            setFocusMode(focusModeByName);
        }

        if (supportedHDR) {
            String pre_HDRMode = PreferenceUtils.get(mContext, PreferenceConfiguration.HDR, PreferenceConfiguration.HDR_DEFAULT);
            HDRMode hdrModeByName = HDRMode.getHDRModeByName(pre_HDRMode);
            setHDRMode(hdrModeByName);
        }

        if (supportedFlash) {
            String pre_flashMode = PreferenceUtils.get(mContext, PreferenceConfiguration.FLASH_MODE, PreferenceConfiguration.FLASH_MODE_DEFAULT);
            FlashMode flashModeByName = FlashMode.getFlashModeByName(pre_flashMode);
            setFlashMode(flashModeByName);
        }

        if (supportFaceDetect) {
            faceDetect = FaceDetect.OFF;
        }
    }

    /**
     * PreViewSize
     */
    private Map<Ratio, Size> buildPreviewSizesRatioMap(List<Size> sizes) {
        Map<Ratio, Size> map = new HashMap<>();

        for (Size size : sizes) {
            Ratio ratio = Ratio.pickRatio(size.width, size.height);
            if (ratio != null) {
                Size oldSize = map.get(ratio);
                if (oldSize == null || (oldSize.width < size.width || oldSize.height < size.height)) {
                    map.put(ratio, size);
                }
            }
        }
        return map;
    }

    public boolean isSupportPreviewSize() {
        return supportPreviewSize;
    }

    public Map<Ratio, Size> getPreviewSizes() {
        return previewSizes;
    }

    public void setPreviewSize(@NonNull Ratio ratio, int orientation) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportPreviewSize) {
            return;
        }
        Size size = previewSizes.get(ratio);
        if (size != null) {
            mParameters.setPreviewSize(size.width, size.height);
            mParameters.setRotation(orientation);
            this.previewRatio = ratio;
//            PreferenceUtils.put(mContext , PreferenceConfiguration.PREVIEW_RATIO , this.previewRatio.toString());
        }
    }

    public Size getPreviewSize(Ratio ratio) {
        return previewSizes.get(ratio);
    }

    public Ratio getPreviewRatio() {
        return this.previewRatio;
    }

    /**
     * PictureSize
     */
    private Map<Ratio, Map<Quality, Size>> buildPictureSizesRatioMap(List<Size> sizes) {
        Map<Ratio, Map<Quality, Size>> map = new HashMap<>();

        Map<Ratio, List<Size>> ratioListMap = new HashMap<>();
        for (Size size : sizes) {
            Ratio ratio = Ratio.pickRatio(size.width, size.height);
            if (ratio != null) {
                List<Size> sizeList = ratioListMap.get(ratio);
                if (sizeList == null) {
                    sizeList = new ArrayList<>();
                    ratioListMap.put(ratio, sizeList);
                }
                sizeList.add(size);
            }
        }
        for (Ratio r : ratioListMap.keySet()) {
            List<Size> list = ratioListMap.get(r);
            ratioListMap.put(r, sortSizes(list));
            Map<Quality, Size> sizeMap = new HashMap<>();
            int i = 0;
            for (Quality q : Quality.values()) {
                Size size = null;
                if (i < list.size()) {
                    size = list.get(i++);
                }
                sizeMap.put(q, size);
            }
            map.put(r, sizeMap);
        }

        return map;
    }

    public boolean isSupportPictureSize() {
        return supportPictureSize;
    }

    public Map<Ratio, Map<Quality, Size>> getPictureSizes() {
        return pictureSizes;
    }

    public void setPictureSize(@NonNull Ratio ratio, Quality quality) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportPictureSize) {
            return;
        }

        if (this.pictureRatio != ratio) {
            if (ratio == null) {
                ratio = Ratio.R_16x9;
            }
            this.pictureRatio = ratio;
        }

        if (this.pictureQuality != quality) {
            if (quality == null) {
                quality = Quality.HIGH;
            }
            this.pictureQuality = quality;
            mParameters.setJpegQuality(quality.getQualityValue());
            PreferenceUtils.put(mContext, PreferenceConfiguration.PICTURE_QUALITY, this.pictureQuality.toString());
        }

        Size size = pictureSizes.get(ratio).get(quality);
        if (size != null) {
            mParameters.setPictureSize(size.width, size.height);
        }
    }

    public Ratio getPictureRatio() {
        return this.pictureRatio;
    }

    public Quality getPictureQuality() {
        return this.pictureQuality;
    }

    public Size getPictureSize(Ratio ratio, Quality quality) {
        return pictureSizes.get(ratio).get(quality);
    }

    /**
     * Picture Format
     */
    private List<PictureFormat> buildPictureFormatList(List<Integer> items) {
        List<PictureFormat> pictureFormats = new ArrayList<>();
        for (Integer item : items) {
            PictureFormat pictureFormat = PictureFormat.getPictureFormatByID(item);
            pictureFormats.add(pictureFormat);
        }
        return pictureFormats;
    }

    public boolean isSupportPictureFormat() {
        return supportPictureFormat;
    }

    public void setPictureFormat(PictureFormat pictureFormat) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportPictureFormat) {
            return;
        }
        if (pictureFormat == null) {
            pictureFormat = PictureFormat.JPEG;
        }
        for (PictureFormat format : this.pictureFormats) {
            if (format == pictureFormat) {
                mParameters.setPictureFormat(pictureFormat.getID());
                this.pictureFormat = pictureFormat;
                PreferenceUtils.put(mContext, PreferenceConfiguration.PICTURE_FORMAT, this.pictureFormat.toString());
            }
        }
    }

    public List<PictureFormat> getPictureFormats() {
        return pictureFormats;
    }

    /**
     * VideoSizeRatio
     */
    private Map<Ratio, Map<Quality, Size>> buildVideoSizesRatioMap(List<Size> sizes) {
        Map<Ratio, Map<Quality, Size>> map = new HashMap<>();

        Map<Ratio, List<Size>> ratioListMap = new HashMap<>();
        for (Size size : sizes) {
            Ratio ratio = Ratio.pickRatio(size.width, size.height);
            if (ratio != null) {
                List<Size> sizeList = ratioListMap.get(ratio);
                if (sizeList == null) {
                    sizeList = new ArrayList<>();
                    ratioListMap.put(ratio, sizeList);
                }
                sizeList.add(size);
            }
        }
        for (Ratio r : ratioListMap.keySet()) {
            List<Size> list = ratioListMap.get(r);
            ratioListMap.put(r, sortSizes(list));
            Map<Quality, Size> sizeMap = new HashMap<>();
            int i = 0;
            for (Quality q : Quality.values()) {
                Size size = null;
                if (i < list.size()) {
                    size = list.get(i++);
                }
                sizeMap.put(q, size);
            }
            map.put(r, sizeMap);
        }

        return map;
    }

    private Map<Ratio, Size> buildVideoSizesRatio(List<Size> sizes) {
        Map<Ratio, Size> map = new HashMap<>();

        for (Size size : sizes) {
            Ratio ratio = Ratio.pickRatio(size.width, size.height);
            if (ratio != null) {
                Size oldSize = map.get(ratio);
                if (oldSize == null || (oldSize.width < size.width || oldSize.height < size.height)) {
                    map.put(ratio, size);
                }
            }
        }
        return map;
    }

    public boolean isSupportVideoSize() {
        return supportVideoSize;
    }

    public void setVideoSizeRatio(Ratio ratio) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportVideoSize) {
            return;
        }
        if (ratio == null) {
            ratio = Ratio.R_16x9;
        }
        if (this.videoSizes.keySet().contains(ratio)) {
            this.videoRatio = ratio;
            PreferenceUtils.put(mContext, PreferenceConfiguration.VIDEO_RATIO, this.videoRatio.toString());
        }
    }

    public void setVideoSize(Quality quality) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportVideoSize) {
            return;
        }
        if (quality == null) {
            quality = Quality.HIGH;
        }
        if (videoQualitySizes.get(getVideoRatio()).keySet().contains(quality)) {
            this.videoQuality = quality;
        }
    }

    public Ratio getVideoRatio() {
        if (this.videoRatio == null) {
            this.videoRatio = this.previewRatio;
        }
        if (this.videoRatio == null) {
            this.previewRatio = Ratio.R_16x9;
            this.videoRatio = this.previewRatio;
        }
        return this.videoRatio;
    }

    public Quality getVideoQuality() {
        if (this.videoQuality == null) {
            this.videoQuality = Quality.HIGH;
        }
        return this.videoQuality;
    }

    public Size getVideoSize() {
        return getVideoQualitySizes().get(getVideoRatio()).get(getVideoQuality());
    }

    public Map<Ratio, Size> getVideoSizes() {
        return videoSizes;
    }

    public Map<Ratio, Map<Quality, Size>> getVideoQualitySizes() {
        return videoQualitySizes;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Camera.Size getOptimalVideoSize(List<Size> supportedVideoSizes,
                                                  List<Size> previewSizes,
                                                  int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;

        // Supported video sizes list might be null, it means that we are allowed to use the preview
        // sizes
        List<Size> videoSizes;
        if (supportedVideoSizes != null) {
            videoSizes = supportedVideoSizes;
        } else {
            videoSizes = previewSizes;
        }
        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available video sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetWidth = w;

        // Try to find a video size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : videoSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.width - targetWidth) < minDiff && previewSizes.contains(size)) {
                optimalSize = size;
                minDiff = Math.abs(size.width - targetWidth);
            }
        }

        // Cannot find video size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : videoSizes) {
                if (Math.abs(size.width - targetWidth) < minDiff && previewSizes.contains(size)) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetWidth);
                }
            }
        }
        return optimalSize;
    }

    private List<Size> sortSizes(List<Size> sizes) {
        int count = sizes.size();

        while (count > 2) {
            for (int i = 0; i < count - 1; i++) {
                Size current = sizes.get(i);
                Size next = sizes.get(i + 1);

                if (current.width < next.width || current.height < next.height) {
                    sizes.set(i, next);
                    sizes.set(i + 1, current);
                }
            }
            count--;
        }

        return sizes;
    }

    /**
     * Zoom
     */
    private void setZoom(int index) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (mParameters != null) {
            mParameters.setZoom(index);
        }
    }

    public void zoomIn() {
        if (++zoomIndex > maxZoomIndex) {
            zoomIndex = maxZoomIndex;
        }
        setZoom(zoomIndex);
    }

    public void zoomOut() {
        if (--zoomIndex < minZoomIndex) {
            zoomIndex = minZoomIndex;
        }
        setZoom(zoomIndex);
    }

    /**
     * FocusMode
     */
    public List<FocusMode> buildFocusModeList(List<String> items) {
        List<FocusMode> focusModes = new ArrayList<>();
        for (String item : items) {
            FocusMode focusMode = FocusMode.getFocusModeByName(item);
            if (focusMode != null) {
                focusModes.add(focusMode);
            }
        }
        return focusModes;
    }

    public boolean isSupportFocusMode() {
        return supportFocusMode;
    }

    public void setFocusMode(FocusMode focusMode) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (!supportFocusMode) {
            return;
        }
        if (focusMode == null) {
            focusMode = FocusMode.AUTO;
        }
        for (FocusMode mode : this.focusModes) {
            if (mode == focusMode) {
                mParameters.setFocusMode(focusMode.toString());
                this.focusMode = focusMode;
                PreferenceUtils.put(mContext, PreferenceConfiguration.FOCUS_MODE, this.focusMode.toString());
            }
        }
    }

    public FocusMode getFocusMode() {
        return focusMode;
    }

    /**
     * FlashMode
     */
    public boolean isSupportedFlash() {
        return supportedFlash;
    }

    public List<FlashMode> buildFlashModeList(List<String> items) {
        List<FlashMode> flashModes = new ArrayList<>();
        for (String item : items) {
            FlashMode flashMode = FlashMode.getFlashModeByName(item);
            flashModes.add(flashMode);
        }
        return flashModes;
    }

    public List<FlashMode> getFlashModes() {
        return flashModes;
    }

    public void setFlashMode(FlashMode flashMode) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedFlash) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (flashMode == null) {
            flashMode = FlashMode.AUTO;
        }
        for (FlashMode mode : this.flashModes) {
            if (mode == flashMode) {
                mParameters.setFlashMode(flashMode.toString());
                this.flashMode = flashMode;
                PreferenceUtils.put(mContext, PreferenceConfiguration.FLASH_MODE, this.flashMode.toString());
            }
        }
    }

    public FlashMode getFlashMode() {
        return flashMode;
    }

    /**
     * HDR
     */
    public boolean isSupportedHDR() {
        return supportedHDR;
    }

    public void setHDRMode(HDRMode hdrMode) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedHDR) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (hdrMode == null) {
            return;
        }
        if (hdrMode == HDRMode.NONE) {
            hdrMode = HDRMode.OFF;
        }
        switch (hdrMode) {
            case ON:
                mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
                break;
            case NONE:
            case OFF:
            default:
                mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
                break;
        }
        this.hdrMode = hdrMode;
        PreferenceUtils.put(mContext, PreferenceConfiguration.HDR, this.hdrMode.toString());
    }

    public HDRMode getHdrMode() {
        return hdrMode;
    }

    /**
     * Color Effect
     */
    public List<ColorEffect> buildColorEffectList(List<String> items) {
        List<ColorEffect> colorEffects = new ArrayList<>();
        for (String item : items) {
            ColorEffect colorEffect = ColorEffect.getColorEffectModeByName(item);
            colorEffects.add(colorEffect);
        }
        return colorEffects;
    }

    public boolean isSupportedColorEffect() {
        return supportedColorEffect;
    }

    public void setColorEffect(ColorEffect colorEffect) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedColorEffect) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (colorEffect == null) {
            return;
        }
        for (ColorEffect effect : colorEffects) {
            if (effect == colorEffect) {
                mParameters.setColorEffect(colorEffect.toString());
                this.colorEffect = colorEffect;
            }
        }
    }

    public ColorEffect getColorEffect() {
        return colorEffect;
    }

    public List<ColorEffect> getColorEffects() {
        return colorEffects;
    }

    /**
     * Scene Mode
     */
    public boolean isSupportedSceneMode() {
        return supportedSceneMode;
    }

    public List<SceneMode> buildSceneModeList(List<String> items) {
        List<SceneMode> sceneModes = new ArrayList<>();
        for (String item : items) {
            SceneMode sceneMode = SceneMode.getSceneModeByName(item);
            sceneModes.add(sceneMode);
        }
        return sceneModes;
    }

    public void setSceneMode(SceneMode sceneMode) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedSceneMode) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (sceneMode == null) {
            return;
        }
        for (SceneMode mode : this.sceneModes) {
            if (mode == sceneMode) {
                mParameters.setSceneMode(sceneMode.toString());
                this.sceneMode = sceneMode;
            }
        }
    }

    public SceneMode getSceneMode() {
        return sceneMode;
    }

    public List<SceneMode> getSceneModes() {
        return sceneModes;
    }

    /**
     * White Balance
     */
    public boolean isSupportedWhiteBalance() {
        return supportedWhiteBalance;
    }

    public List<WhiteBalance> buildWhiteBalance(List<String> items) {
        List<WhiteBalance> whiteBalances = new ArrayList<>();
        for (String item : items) {
            WhiteBalance whiteBalance = WhiteBalance.getWhiteBalanceByName(item);
            whiteBalances.add(whiteBalance);
        }
        return whiteBalances;
    }

    public List<WhiteBalance> getWhiteBalances() {
        return whiteBalances;
    }

    public void setWhiteBalance(WhiteBalance whiteBalance) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedWhiteBalance) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (whiteBalance == null) {
            return;
        }
        for (WhiteBalance balance : this.whiteBalances) {
            if (balance == whiteBalance) {
                mParameters.setWhiteBalance(whiteBalance.toString());
                this.whiteBalance = whiteBalance;
            }
        }
    }

    public WhiteBalance getWhiteBalance() {
        return whiteBalance;
    }

    /**
     * AntiBinding
     */
    public boolean isSupportedAntiBinding() {
        return supportedAntiBinding;
    }

    public List<String> getAntiBindings() {
        return this.antiBindings;
    }

    public String getAntiBinding() {
        if (this.antiBinding == null) {
            antiBinding = antiBindings.get(0);
        }
        return this.antiBinding;
    }

    public void setAntiBinding(String antiBinding) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportedAntiBinding) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (TextUtils.isEmpty(antiBinding)) {
            return;
        }
        if (antiBindings.contains(antiBinding)) {
            mParameters.setAntibanding(antiBinding);
            this.antiBinding = antiBinding;
        }
    }

    /**
     * Face Detect
     */
    public void setFaceDetect(FaceDetect faceDetect) {
        if (mCameraParamter == null) {
            throw new RuntimeException("CameraParamter is null , please inject MyCameraParamter.getInstance() !!!");
        }
        if (!supportFaceDetect) {
            return;
        }
        if (!isInit) {
            throw new RuntimeException("CameraParamter is not init , please inject MyCameraParamter.initParams !!!");
        }
        if (faceDetect == null) {
            faceDetect = FaceDetect.OFF;
        }
        this.faceDetect = faceDetect;
    }

    public boolean isSupportFaceDetect() {
        return supportFaceDetect;
    }

    public FaceDetect getFaceDetect() {
        return this.faceDetect;
    }

    /**
     * 释放对象
     */
    public void release() {
        if (mCameraParamter != null) {
            mParameters = null;
            mCameraParamter = null;
            isInit = false;
        }
    }
}
