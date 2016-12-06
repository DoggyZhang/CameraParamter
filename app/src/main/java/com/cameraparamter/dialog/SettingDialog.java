package com.cameraparamter.dialog;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.cameraparamter.R;
import com.cameraparamter.configuration.CameraParamter;
import com.cameraparamter.fragment.MainFragment;
import com.cameraparamter.params.ColorEffect;
import com.cameraparamter.params.FaceDetect;
import com.cameraparamter.params.FocusMode;
import com.cameraparamter.params.Quality;
import com.cameraparamter.params.Ratio;
import com.cameraparamter.params.SceneMode;
import com.cameraparamter.params.WhiteBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/30.
 */

public class SettingDialog extends AlertDialog {

    private static final String TAG = "SettingDialog";

    private MainFragment mainFragment;

    private CameraParamter mCameraParamter;

    private View layout_sw_face_detect;
    private Switch sw_face_detect;

    private View layout_sp_focus_mode;
    private Spinner sp_focus_mode;
    private ArrayAdapter<String> adapter_focus_mode;

    private View layout_sp_preview_ratio;
    private Spinner sp_preview_ratio;
    private ArrayAdapter<String> adapter_preview_ratio;

    private View layout_sp_picture_ratio;
    private Spinner sp_picture_ratio;
    private ArrayAdapter<String> adapter_picture_ratio;

    private View layout_sp_picture_quality;
    private Spinner sp_picture_quality;
    private ArrayAdapter<String> adapter_picture_quality;

    private View layout_sp_color_effects;
    private Spinner sp_color_effects;
    private ArrayAdapter<String> adapter_color_effect;

    private View layout_sp_scene_mode;
    private Spinner sp_scene_mode;
    private ArrayAdapter<String> adapter_scene_mode;

    private View layout_sp_white_balance;
    private Spinner sp_white_balance;
    private ArrayAdapter<String> adapter_white_balance;

    private View layout_sp_antibinding;
    private Spinner sp_antibinding;
    private ArrayAdapter<String> adapter_antibinding;

    private View layout_sp_video_quality;
    private Spinner sp_video_quality;
    private ArrayAdapter<String> adapter_video_quality;

    public SettingDialog(@NonNull Context context, MainFragment mainFragment) {
        super(context);
        this.mainFragment = mainFragment;
        initAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);
        initView();
        initEvent();
        initParamterValue();
    }

    private void initView() {
        layout_sw_face_detect = findViewById(R.id.layout_sw_face_detect);
        sw_face_detect = (Switch) findViewById(R.id.sw_face_detect);

        layout_sp_focus_mode = findViewById(R.id.layout_sp_focus_mode);
        sp_focus_mode = (Spinner) findViewById(R.id.sp_focus_mode);

        layout_sp_preview_ratio = findViewById(R.id.layout_sp_preview_ratio);
        sp_preview_ratio = ((Spinner) findViewById(R.id.sp_preview_ratio));

        layout_sp_picture_ratio = findViewById(R.id.layout_sp_picture_ratio);
        sp_picture_ratio = ((Spinner) findViewById(R.id.sp_picture_ratio));

        layout_sp_picture_quality = findViewById(R.id.layout_sp_picture_quality);
        sp_picture_quality = ((Spinner) findViewById(R.id.sp_picture_quality));

        layout_sp_color_effects = findViewById(R.id.layout_sp_color_effects);
        sp_color_effects = (Spinner) findViewById(R.id.sp_color_effects);

        layout_sp_scene_mode = findViewById(R.id.layout_sp_scene_mode);
        sp_scene_mode = (Spinner) findViewById(R.id.sp_scene_mode);

        layout_sp_white_balance = findViewById(R.id.layout_sp_white_balance);
        sp_white_balance = (Spinner) findViewById(R.id.sp_white_balance);

        layout_sp_antibinding = findViewById(R.id.layout_sp_antibinding);
        sp_antibinding = (Spinner) findViewById(R.id.sp_antibinding);

        layout_sp_video_quality = findViewById(R.id.layout_sp_video_quality);
        sp_video_quality = (Spinner) findViewById(R.id.sp_video_quality);

    }

    private void initEvent() {
        sw_face_detect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCameraParamter.setFaceDetect(FaceDetect.ON);
                } else {
                    mCameraParamter.setFaceDetect(FaceDetect.OFF);
                }
                mainFragment.onFaceDetectChange(mCameraParamter, isChecked);
            }
        });

        sp_focus_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_focus_mode.getItem(position);
                FocusMode focusMode = FocusMode.getFocusModeByName(item);
                mCameraParamter.setFocusMode(focusMode);
                mainFragment.onFocusModeChange(mCameraParamter, focusMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_preview_ratio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_preview_ratio.getItem(position);
                Ratio ratio = Ratio.getRatioByName(item);
                mCameraParamter.setPreviewSize(ratio, 0);
                mainFragment.onPreviewRatioChange(mCameraParamter, ratio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_picture_ratio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_picture_ratio.getItem(position);
                Ratio ratio = Ratio.getRatioByName(item);
                mCameraParamter.setPictureSize(ratio, mCameraParamter.getPictureQuality());
                mainFragment.onPictureRatioChange(mCameraParamter, ratio);

                /**
                 * Update Picture Quality
                 */
                Set<Quality> pictureQualities = mCameraParamter.getPictureSizes().get(mCameraParamter.getPictureRatio()).keySet();
                List<String> s_pictureQuality = new ArrayList<>();
                for (Quality pictureQuality : pictureQualities) {
                    if (pictureQuality != null) {
                        s_pictureQuality.add(pictureQuality.toString());
                    }
                }
                adapter_picture_quality.clear();
                adapter_picture_quality.addAll(s_pictureQuality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_picture_quality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_picture_quality.getItem(position);
                Quality quality = Quality.getQualityByName(item);
                mCameraParamter.setPictureSize(mCameraParamter.getPictureRatio(), quality);
                mainFragment.onPictureQualityChange(mCameraParamter, quality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_color_effects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s_color_effect = adapter_color_effect.getItem(position);
                ColorEffect colorEffect = ColorEffect.getColorEffectModeByName(s_color_effect);
                mCameraParamter.setColorEffect(colorEffect);
                mainFragment.onColorEffectChange(mCameraParamter, colorEffect);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_scene_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s_scene_mode = adapter_scene_mode.getItem(position);
                SceneMode sceneMode = SceneMode.getSceneModeByName(s_scene_mode);
                mCameraParamter.setSceneMode(sceneMode);
                mainFragment.onSceneModeChange(mCameraParamter, sceneMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_white_balance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s_white_balance = adapter_white_balance.getItem(position);
                WhiteBalance whiteBalance = WhiteBalance.getWhiteBalanceByName(s_white_balance);
                mCameraParamter.setWhiteBalance(whiteBalance);
                mainFragment.onWhiteBalance(mCameraParamter, whiteBalance);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_antibinding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_antibinding.getItem(position);
                mCameraParamter.setAntiBinding(item);
                mainFragment.onAntiBindingChange(mCameraParamter, item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_video_quality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = adapter_video_quality.getItem(position);
                Quality qualityByName = Quality.getQualityByName(item);
                mCameraParamter.setVideoSize(qualityByName);
                mainFragment.onVideoQualityChange(mCameraParamter, qualityByName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initAdapter() {
        adapter_focus_mode = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_preview_ratio = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_picture_ratio = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_picture_quality = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_color_effect = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_scene_mode = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_white_balance = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
        adapter_antibinding = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );

        adapter_video_quality = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item
        );
    }

    private void initParamterValue() {
        sp_focus_mode.setAdapter(adapter_focus_mode);
        sp_preview_ratio.setAdapter(adapter_preview_ratio);
        sp_picture_ratio.setAdapter(adapter_picture_ratio);
        sp_picture_quality.setAdapter(adapter_picture_quality);
        sp_color_effects.setAdapter(adapter_color_effect);
        sp_scene_mode.setAdapter(adapter_scene_mode);
        sp_white_balance.setAdapter(adapter_white_balance);
        sp_antibinding.setAdapter(adapter_antibinding);

        sp_video_quality.setAdapter(adapter_video_quality);
    }

    /**
     * 当调用 show() 的之前要更新 CameraParamter
     *
     * @param cameraParamter
     */
    public void updateCameraParamter(CameraParamter cameraParamter) {
        mCameraParamter = cameraParamter;
        if (mCameraParamter != null) {
            if (mCameraParamter.isSupportFaceDetect()) {
                FaceDetect faceDetect = mCameraParamter.getFaceDetect();
                switch (faceDetect) {
                    case ON:
                        sw_face_detect.setChecked(true);
                        break;
                    case OFF:
                        sw_face_detect.setChecked(false);
                        break;
                }
            } else {
                layout_sw_face_detect.setVisibility(View.GONE);
            }

            /**
             * Focus Mode
             */
            if (mCameraParamter.isSupportFocusMode()) {
                List<String> s_focusMode = new ArrayList<>();
                for (FocusMode focusMode : FocusMode.values()) {
                    if (focusMode != null) {
                        s_focusMode.add(focusMode.toString());
                    }
                }
                adapter_focus_mode.clear();
                adapter_focus_mode.addAll(s_focusMode);
                FocusMode focusMode_default = mCameraParamter.getFocusMode();
                if (focusMode_default != null) {
                    sp_focus_mode.setSelection(adapter_focus_mode.getPosition(focusMode_default.toString()));
                }
            } else {
                layout_sp_focus_mode.setVisibility(View.GONE);
            }

            /**
             * Preview Size
             */
            if (mCameraParamter.isSupportPreviewSize()) {
                Set<Ratio> previewSizeRatios = mCameraParamter.getPreviewSizes().keySet();
                List<String> s_previewSizes = new ArrayList<>();
                for (Ratio previewSizeRatio : previewSizeRatios) {
                    if (previewSizeRatio != null) {
                        s_previewSizes.add(previewSizeRatio.toString());
                    }
                }
                adapter_preview_ratio.clear();
                adapter_preview_ratio.addAll(s_previewSizes);
                Ratio previewRatio_default = mCameraParamter.getPreviewRatio();
                if (previewRatio_default != null) {
                    sp_preview_ratio.setSelection(adapter_preview_ratio.getPosition(previewRatio_default.toString()));
                }
            } else {
                layout_sp_preview_ratio.setVisibility(View.GONE);
            }

            /**
             * Picture Ratio
             */
            if (mCameraParamter.isSupportPictureSize()) {
                Set<Ratio> pictureRatios = mCameraParamter.getPictureSizes().keySet();
                List<String> s_pictureSizes = new ArrayList<>();
                for (Ratio pictureRatio : pictureRatios) {
                    if (pictureRatio != null) {
                        s_pictureSizes.add(pictureRatio.toString());
                    }
                }
                adapter_picture_ratio.clear();
                adapter_picture_ratio.addAll(s_pictureSizes);
                Ratio pictureRatio_default = mCameraParamter.getPictureRatio();
                if (pictureRatio_default != null) {
                    sp_picture_ratio.setSelection(adapter_picture_ratio.getPosition(pictureRatio_default.toString()));
                }
            } else {
                layout_sp_picture_ratio.setVisibility(View.GONE);
            }


            /**
             * Picture Quality
             */
            if (mCameraParamter.isSupportPictureSize()) {
                Set<Quality> pictureQualities = mCameraParamter.getPictureSizes().get(mCameraParamter.getPictureRatio()).keySet();
                List<String> s_pictureQuality = new ArrayList<>();
                for (Quality pictureQuality : pictureQualities) {
                    if (pictureQuality != null) {
                        s_pictureQuality.add(pictureQuality.toString());
                    }
                }
                adapter_picture_quality.clear();
                adapter_picture_quality.addAll(s_pictureQuality);
                Quality pictureQuality_default = mCameraParamter.getPictureQuality();
                if (pictureQuality_default != null) {
                    sp_picture_quality.setSelection(adapter_picture_quality.getPosition(pictureQuality_default.toString()));
                }
            } else {
                layout_sp_picture_quality.setVisibility(View.GONE);
            }

            /**
             * Color Mode
             */
            if (mCameraParamter.isSupportedColorEffect()) {
                List<ColorEffect> colorEffects = mCameraParamter.getColorEffects();
                List<String> s_colorEffects = new ArrayList<>();
                for (ColorEffect colorEffect : colorEffects) {
                    if (colorEffect != null) {
                        s_colorEffects.add(colorEffect.toString());
                    }
                }
                adapter_color_effect.clear();
                adapter_color_effect.addAll(s_colorEffects);
                ColorEffect colorEffect_default = mCameraParamter.getColorEffect();
                if (colorEffect_default != null) {
                    sp_color_effects.setSelection(adapter_color_effect.getPosition(colorEffect_default.toString()));
                }
            } else {
                layout_sp_color_effects.setVisibility(View.GONE);
            }

            /**
             * Scene Mode
             */
            if (mCameraParamter.isSupportedSceneMode()) {
                List<SceneMode> sceneModes = mCameraParamter.getSceneModes();
                List<String> s_sceneModes = new ArrayList<>();
                for (SceneMode sceneMode : sceneModes) {
                    if (sceneMode != null) {
                        s_sceneModes.add(sceneMode.toString());
                    }
                }
                adapter_scene_mode.clear();
                adapter_scene_mode.addAll(s_sceneModes);
                SceneMode sceneMode_default = mCameraParamter.getSceneMode();
                if (sceneMode_default != null) {
                    sp_scene_mode.setSelection(adapter_scene_mode.getPosition(sceneMode_default.toString()));
                }
            } else {
                layout_sp_scene_mode.setVisibility(View.GONE);
            }

            /**
             * White Balance
             */
            if (mCameraParamter.isSupportedWhiteBalance()) {
                List<WhiteBalance> whiteBalances = mCameraParamter.getWhiteBalances();
                List<String> s_whiteBalances = new ArrayList<>();
                for (WhiteBalance whiteBalance : whiteBalances) {
                    if (whiteBalance != null) {
                        s_whiteBalances.add(whiteBalance.toString());
                    }
                }
                adapter_white_balance.clear();
                adapter_white_balance.addAll(s_whiteBalances);
                WhiteBalance whiteBalance_default = mCameraParamter.getWhiteBalance();
                if (whiteBalance_default != null) {
                    sp_white_balance.setSelection(adapter_white_balance.getPosition(whiteBalance_default.toString()));
                }

            } else {
                layout_sp_white_balance.setVisibility(View.GONE);
            }

            /**
             * AntiBinding
             */
            if (mCameraParamter.isSupportedAntiBinding()) {
                List<String> antiBindings = mCameraParamter.getAntiBindings();
                adapter_antibinding.clear();
                adapter_antibinding.addAll(antiBindings);
                String antiBinding_default = mCameraParamter.getAntiBinding();
                if (!TextUtils.isEmpty(antiBinding_default)) {
                    sp_antibinding.setSelection(adapter_antibinding.getPosition(antiBinding_default));
                }

            } else {
                layout_sp_antibinding.setVisibility(View.GONE);
            }

            /**
             * Video Quality
             */
            if (mCameraParamter.isSupportVideoSize()) {
                Map<Quality, Camera.Size> videoQualitySizes = mCameraParamter.getVideoQualitySizes().get(mCameraParamter.getVideoRatio());
                Set<Quality> qualities = videoQualitySizes.keySet();
                List<String> s_videoQualitySizes = new ArrayList<>();
                for (Quality quality : qualities) {
                    s_videoQualitySizes.add(quality.toString());
                }
                adapter_video_quality.clear();
                adapter_video_quality.addAll(s_videoQualitySizes);
                Quality videoQuality_default = mCameraParamter.getVideoQuality();
                if (videoQuality_default != null) {
                    sp_video_quality.setSelection(adapter_video_quality.getPosition(videoQuality_default.toString()));
                }
            }

        }
    }


}
