package com.cameraparamter.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cameraparamter.MainActivity;
import com.cameraparamter.R;
import com.cameraparamter.app.App;
import com.cameraparamter.configuration.CameraParamter;
import com.cameraparamter.dialog.SettingDialog;
import com.cameraparamter.fragment.base.BaseFragment;
import com.cameraparamter.interfaces.OnCameraParamterChangeListener;
import com.cameraparamter.params.ColorEffect;
import com.cameraparamter.params.FlashMode;
import com.cameraparamter.params.FocusMode;
import com.cameraparamter.params.HDRMode;
import com.cameraparamter.params.Quality;
import com.cameraparamter.params.Ratio;
import com.cameraparamter.params.SceneMode;
import com.cameraparamter.params.WhiteBalance;
import com.cameraparamter.task.SavePhotoTask;
import com.cameraparamter.utils.BitmapUtils;
import com.cameraparamter.utils.FilePathUtils;
import com.cameraparamter.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener, OnCameraParamterChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private DrawerLayout drawer_main;
    private ImageView iv_photo;
    private ImageView iv_album;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private ImageView iv_focus;
    private static final int FOCUS_ANIMATION_DURATION = 500;
    private static final int FOCUS_STATE_NORMAL = 0x1;
    private static final int FOCUS_STATE_SUCCESS = 0x2;
    private static final int FOCUS_STATE_FAIL = 0x3;
    private static final int FOCUS_STATE_INVISIBLE = 0x4;

    private ImageView iv_record_time;
    private TextView tv_record_time;
    private ImageView iv_hdr;
    private ImageView iv_shutter_sound;
    boolean isShutterSoundEnable = false;
    private ImageView iv_flash;
    private ImageView iv_change_camera;
    private ImageView iv_setting;
    private ImageView iv_record;
    private ImageView iv_take;
    private View progress_save;

    private Camera mCamera;
    private boolean mSupportedFrontCamera;
    private int mCameraCount;
    private int mCurrentCameraID = CAMERA_ID_BACK;
    private static final int CAMERA_ID_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int CAMERA_ID_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private static final int CAMERA_DISPLAY_ORIENTATION = 90;
    private CameraParamter mCameraParamter;

    private SettingDialog mSettingDialog;

    private MediaRecorder mRecorder;
    private File outputRecordFile;
    private boolean isPrepared = false;
    private boolean isRecording = false;
    private OnRecordPreparedListener mOnRecordPreparedListener;

    private static final int MODE_PHOTO = 0x1;
    private static final int MODE_RECORD = 0x2;
    private int MODE = MODE_PHOTO;

    private static final int HANDLER_UPDATE_VIEW = 0x00;

    private static final int HANDLER_FOCUS_STATE_START = 0x01;
    private static final int HANDLER_FOCUS_STATE_UPDATE = 0x02;
    private static final int HANDLER_FOCUS_STATE_OVER = 0x03;

    private static final int HANDLER_RECORD_TIME_START = 0x04;
    private static final int HANDLER_RECORD_TIME_UPDATE = 0x05;
    private static final int HANDLER_RECORD_TIME_STOP = 0x06;
    private static final int HANDLER_RECORD_TIME_RESET = 0x07;
    private Handler mHandler = new Handler() {
        private int recordTime = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_VIEW:
                    updateView();
                    break;

                case HANDLER_FOCUS_STATE_START:
                    int x = msg.arg1;
                    int y = msg.arg2;
                    updateFocusState(FOCUS_STATE_NORMAL, x, y);
                    break;
                case HANDLER_FOCUS_STATE_UPDATE:
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        updateFocusState(FOCUS_STATE_SUCCESS, 0, 0);
                    } else {
                        updateFocusState(FOCUS_STATE_FAIL, 0, 0);
                    }
                    sendEmptyMessageDelayed(HANDLER_FOCUS_STATE_OVER, FOCUS_ANIMATION_DURATION);
                    break;
                case HANDLER_FOCUS_STATE_OVER:
                    updateFocusState(FOCUS_STATE_INVISIBLE, 0, 0);
                    break;

                case HANDLER_RECORD_TIME_START:
                    mHandler.sendEmptyMessage(HANDLER_RECORD_TIME_UPDATE);
                    break;
                case HANDLER_RECORD_TIME_UPDATE:
                    recordTime++;
                    int minute = recordTime / 60;
                    int second = recordTime % 60;
                    tv_record_time.setText(String.format("%02d:%02d", minute, second));
                    mHandler.sendEmptyMessageDelayed(HANDLER_RECORD_TIME_UPDATE, 1000);
                    break;
                case HANDLER_RECORD_TIME_STOP:
                    mHandler.removeMessages(HANDLER_RECORD_TIME_UPDATE);
                    break;
                case HANDLER_RECORD_TIME_RESET:
                    mHandler.removeMessages(HANDLER_RECORD_TIME_START);
                    mHandler.removeMessages(HANDLER_RECORD_TIME_UPDATE);
                    mHandler.removeMessages(HANDLER_RECORD_TIME_STOP);
                    recordTime = 0;
                    tv_record_time.setText("00:00");
                    break;
            }
        }
    };

    @Override
    public int getLayoutID() {
        return R.layout.fragment_main;
    }

    @Override
    public void initArguments(Bundle arguments) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseRecord();
        releaseCamera();
    }

    @Override
    public void initView(View root) {
        drawer_main = (DrawerLayout) root.findViewById(R.id.drawer_main);
        iv_record = (ImageView) root.findViewById(R.id.iv_record);
        iv_photo = (ImageView) root.findViewById(R.id.iv_photo);

        surfaceView = (SurfaceView) root.findViewById(R.id.surfaceView);

        iv_focus = (ImageView) root.findViewById(R.id.iv_focus);
        iv_record_time = (ImageView) root.findViewById(R.id.iv_record_time);
        tv_record_time = (TextView) root.findViewById(R.id.tv_record_time);

        iv_hdr = (ImageView) root.findViewById(R.id.iv_hdr);
        iv_shutter_sound = ((ImageView) root.findViewById(R.id.iv_shutter_sound));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            iv_shutter_sound.setVisibility(View.GONE);
        }
        iv_flash = (ImageView) root.findViewById(R.id.iv_flash);
        iv_change_camera = (ImageView) root.findViewById(R.id.iv_change_camera);
        iv_setting = (ImageView) root.findViewById(R.id.iv_setting);
        iv_take = (ImageView) root.findViewById(R.id.iv_take);
        progress_save = root.findViewById(R.id.progress_save);
        iv_album = (ImageView) root.findViewById(R.id.iv_album);

        mSettingDialog = new SettingDialog(getContext(), this);
    }

    @Override
    public void initEvent() {
        // 处理 DrawerLayout 冲突问题
        drawer_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getX() < (App.getApp().getScreenWidth() / 4)) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        surfaceView.setOnTouchListener(new CameraTouchListener(getContext()));
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceHolder = holder;
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                mCurrentCameraID = CAMERA_ID_BACK;
                initCamera(mCurrentCameraID);
                startCameraPreview();
                updateView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                surfaceHolder = holder;
                if (surfaceHolder.getSurface() == null) {
                    return;
                }
                try {
                    mCamera.stopPreview();
                } catch (Exception e) {
                }

                startCameraPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseRecord();
                releaseCamera();
            }
        });

        // 控制按钮的事件
        iv_photo.setOnClickListener(this);
        iv_record.setOnClickListener(this);

        iv_hdr.setOnClickListener(this);
        iv_shutter_sound.setOnClickListener(this);
        iv_flash.setOnClickListener(this);
        iv_change_camera.setOnClickListener(this);
        iv_setting.setOnClickListener(this);
        iv_take.setOnClickListener(this);
        iv_album.setOnClickListener(this);
    }

    /**
     * 更新当前模式
     */
    public void setMode(int mode) {
        switch (mode) {
            case MODE_PHOTO:
                MODE = MODE_PHOTO;
                break;
            case MODE_RECORD:
                MODE = MODE_RECORD;
                break;
        }
    }

    /**
     * 更新当前所有 view 的状态
     */
    private void updateView() {
        switch (MODE) {
            case MODE_PHOTO:
                iv_photo.setImageResource(R.mipmap.ic_photo_pressed);
                iv_record.setImageResource(R.drawable.bg_record_selector);
                iv_record_time.setVisibility(View.INVISIBLE);
                tv_record_time.setVisibility(View.INVISIBLE);

                progress_save.setVisibility(View.VISIBLE);

                iv_hdr.setVisibility(View.VISIBLE);
                iv_shutter_sound.setVisibility(View.VISIBLE);
                iv_flash.setVisibility(View.VISIBLE);
                iv_change_camera.setVisibility(View.VISIBLE);
                break;
            case MODE_RECORD:
                iv_photo.setImageResource(R.drawable.bg_photo_selector);
                iv_record.setImageResource(R.mipmap.ic_record_pressed);
                iv_record_time.setVisibility(View.VISIBLE);
                tv_record_time.setVisibility(View.VISIBLE);
                if (!isPrepared) {
                    iv_take.setImageResource(R.drawable.bg_take_selector);
                    iv_record_time.setImageResource(R.mipmap.ic_record_time_normal);
                } else {
                    if (isRecording) {
                        iv_take.setImageResource(R.drawable.bg_take_stop_selector);
                        iv_record_time.setImageResource(R.mipmap.ic_record_time_recording);
                    } else {
                        iv_take.setImageResource(R.drawable.bg_take_selector);
                        iv_record_time.setImageResource(R.mipmap.ic_record_time_normal);
                    }
                }
                progress_save.setVisibility(View.INVISIBLE);

                iv_hdr.setVisibility(View.INVISIBLE);
                iv_shutter_sound.setVisibility(View.INVISIBLE);
                iv_flash.setVisibility(View.INVISIBLE);
                iv_change_camera.setVisibility(View.INVISIBLE);
                break;
        }
        if (mCamera != null) {
            if (mCameraParamter.isSupportedHDR()) {
                iv_hdr.setEnabled(true);
                HDRMode hdrMode = mCameraParamter.getHdrMode();
                switch (hdrMode) {
                    case ON:
                        iv_hdr.setImageResource(R.drawable.bg_hdr_on_selector);
                        break;
                    case OFF:
                    case NONE:
                        iv_hdr.setImageResource(R.drawable.bg_hdr_off_selector);
                        break;
                }
            } else {
                iv_hdr.setEnabled(false);
                iv_hdr.setImageResource(R.drawable.bg_hdr_off_selector);
            }

            if (mCameraParamter.isSupportedFlash()) {
                iv_flash.setEnabled(true);
                FlashMode flashMode = mCameraParamter.getFlashMode();
                switch (flashMode) {
                    case ON:
                        iv_flash.setImageResource(R.drawable.bg_flash_on_selector);
                        break;
                    case OFF:
                        iv_flash.setImageResource(R.drawable.bg_flash_off_selector);
                        break;
                    case AUTO:
                        iv_flash.setImageResource(R.drawable.bg_flash_auto_selector);
                        break;
                }
            } else {
                iv_flash.setEnabled(false);
                iv_flash.setImageResource(R.drawable.bg_flash_off_selector);
            }

            if (mSupportedFrontCamera) {
                iv_change_camera.setEnabled(true);
            } else {
                iv_change_camera.setEnabled(false);
            }
        }

        List<String> photoPaths = FileUtils.loadFile(FilePathUtils.photoDir, "jpeg");
        if (photoPaths != null
                && photoPaths.size() > 0) {
            String photoPath = photoPaths.get(0);
            Bitmap imageThumbnail = BitmapUtils.createImageThumbnail(photoPath);
            if (imageThumbnail != null) {
                iv_album.setImageBitmap(imageThumbnail);
            } else {
                iv_album.setImageResource(R.drawable.bg_rect);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mCamera != null) {
            switch (v.getId()) {
                case R.id.iv_photo:
                    setMode(MODE_PHOTO);
                    break;
                case R.id.iv_record:
                    setMode(MODE_RECORD);
                    break;
                case R.id.iv_album:
                    ((MainActivity) getActivity()).nextFragment();
                    break;
                case R.id.iv_hdr:
                    if (mCameraParamter.isSupportedHDR()) {
                        HDRMode setHDRMode = mCameraParamter.getHdrMode() == HDRMode.ON ? HDRMode.OFF : HDRMode.ON;
                        mCameraParamter.setHDRMode(setHDRMode);
                        mCamera.setParameters(mCameraParamter.getParameters());
                    } else {
                        Toast.makeText(getContext(), "设备不支持HDR模式", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.iv_shutter_sound:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (isShutterSoundEnable) {
                            mCamera.enableShutterSound(false);
                            isShutterSoundEnable = false;
                            iv_shutter_sound.setImageResource(R.drawable.bg_shutter_sound_disable_selector);
                        } else {
                            mCamera.enableShutterSound(true);
                            isShutterSoundEnable = true;
                            iv_shutter_sound.setImageResource(R.drawable.bg_shutter_sound_enable_selector);
                        }
                    }
                    break;
                case R.id.iv_flash:
                    if (mCameraParamter.isSupportedFlash()) {
                        FlashMode setFlashMode = null;
                        switch (mCameraParamter.getFlashMode()) {
                            case ON:
                                setFlashMode = FlashMode.AUTO;
                                break;
                            case AUTO:
                                setFlashMode = FlashMode.OFF;
                                break;
                            case OFF:
                                setFlashMode = FlashMode.ON;
                                break;
                            default:
                                setFlashMode = FlashMode.ON;
                                break;
                        }
                        mCameraParamter.setFlashMode(setFlashMode);
                        mCamera.setParameters(mCameraParamter.getParameters());
                    } else {
                        Toast.makeText(getContext(), "设备没有闪光灯设备", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.iv_change_camera:
                    if (mSupportedFrontCamera) {
                        releaseCamera();
                        mCurrentCameraID = mCurrentCameraID == CAMERA_ID_BACK ? CAMERA_ID_FRONT : CAMERA_ID_BACK;
                        initCamera(mCurrentCameraID);
                        startCameraPreview();
                        updateView();
                    } else {
                        Toast.makeText(getContext(), "设备没有前置摄像头", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.iv_setting:
                    mSettingDialog.show();
                    mSettingDialog.updateCameraParamter(mCameraParamter);
                    break;
                case R.id.iv_take:
                    switch (MODE) {
                        case MODE_PHOTO:
                            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] data, Camera camera) {
                                    new SavePhotoTask(new SavePhotoTask.OnSavePhotoTaskListener() {
                                        @Override
                                        public void result(boolean success) {
                                            if (success) {
                                                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
                                            }
                                            ((MainActivity) getActivity()).updateAlbum();
                                            iv_take.setVisibility(View.VISIBLE);
                                            startCameraPreview();
                                            updateView();
                                            // TODO: 发送消息通知相册页面更新信息
                                        }
                                    }).execute(data);
                                }
                            });
                            iv_take.setVisibility(View.INVISIBLE);
                            break;

                        case MODE_RECORD:
                            if (isPrepared && isRecording) {
                                stopRecord();
                                ((MainActivity) getActivity()).updateAlbum();
                            } else if (!isPrepared) {
                                initRecord(false);
                            }
                            break;
                    }
                    break;
            }
            updateView();
        }
    }

    /**
     * 初始化相机
     *
     * @param cameraID CAMERA_ID_BACK , CAMERA_ID_FRONT
     *                 默认是 back
     */
    private void initCamera(int cameraID) {
        mCameraCount = Camera.getNumberOfCameras();
        mSupportedFrontCamera = false;
        if (mCameraCount > 1) {
            mSupportedFrontCamera = true;
        }

        // 默认打开后置摄像头
        switch (cameraID) {
            case CAMERA_ID_BACK:
                mCurrentCameraID = CAMERA_ID_BACK;
                break;
            case CAMERA_ID_FRONT:
                if (mSupportedFrontCamera) {
                    mCurrentCameraID = CAMERA_ID_FRONT;
                } else {
                    Toast.makeText(getContext(), "不支持前置摄像头", Toast.LENGTH_SHORT).show();
                    mCurrentCameraID = CAMERA_ID_BACK;
                }
                break;
            default:
                mCurrentCameraID = CAMERA_ID_BACK;
                break;
        }
        mCamera = Camera.open(mCurrentCameraID);
        if (mCamera == null) {
            Toast.makeText(getContext(), "打开相机失败", Toast.LENGTH_SHORT).show();
            throw new RuntimeException("fail to open camera !!!");
        }
        mCameraParamter = CameraParamter.getInstance(getContext(), mCamera.getParameters());
        mCameraParamter.initParams();
        mCamera.setParameters(mCameraParamter.getParameters());
        mCamera.setDisplayOrientation(CAMERA_DISPLAY_ORIENTATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCamera.enableShutterSound(true);
            isShutterSoundEnable = true;
        }

        mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                Toast.makeText(getContext(), "检测到了 " + faces.length + " 张脸", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 相机预览 (已经包含初始化相机)
     */
    private void startCameraPreview() {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (IOException e) {
            e.printStackTrace();
            releaseCamera();
        }
    }

    private void updateFocusState(int focusState, int x, int y) {
        iv_focus.setVisibility(View.VISIBLE);
        switch (focusState) {
            case FOCUS_STATE_INVISIBLE:
                iv_focus.setVisibility(View.INVISIBLE);
                break;
            case FOCUS_STATE_NORMAL:
                int width = iv_focus.getWidth();
                int height = iv_focus.getHeight();
                int screenWidth = App.getApp().getScreenWidth();
                int screenHeight = App.getApp().getScreenHeight();
                int positionX;
                int positionY;
                if (x + width > screenWidth) {
                    positionX = screenHeight - width;
                } else if (x - width < 0) {
                    positionX = 0;
                } else {
                    positionX = x - width / 2;
                }

                if (y + height > screenHeight) {
                    positionY = screenHeight - height;
                } else if (y - width < 0) {
                    positionY = 0;
                } else {
                    positionY = y - height / 2;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(iv_focus.getLayoutParams());
                layoutParams.setMargins(positionX, positionY, 0, 0);
                iv_focus.setLayoutParams(layoutParams);
                iv_focus.setImageResource(R.mipmap.ic_focus_normal);
                break;
            case FOCUS_STATE_SUCCESS:
                iv_focus.setImageResource(R.mipmap.ic_focus_success);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 0.33f, 1.2f, 0.33f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(FOCUS_ANIMATION_DURATION);
                iv_focus.startAnimation(scaleAnimation);
                break;
            case FOCUS_STATE_FAIL:
                iv_focus.setImageResource(R.mipmap.ic_focus_fail);
                ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.2f, 0.33f, 1.2f, 0.33f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation2.setDuration(FOCUS_ANIMATION_DURATION);
                iv_focus.startAnimation(scaleAnimation2);
                break;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCameraParamter.release();
            try {
                mCamera.stopFaceDetection();
                mCamera.stopPreview();
            } catch (Exception e) {

            }
            mCamera.release();
            mCamera = null;
        }
    }

    // TODO 增加 Camera 属性
    private void setCameraAttribute() {

    }

    @Override
    public void onFaceDetectChange(CameraParamter cameraParamter, boolean isOpen) {
        if (isOpen) {
            mCamera.startFaceDetection();
        } else {
            mCamera.stopFaceDetection();
        }
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onFocusModeChange(CameraParamter cameraParamter, FocusMode focusMode) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onPreviewRatioChange(CameraParamter cameraParamter, Ratio ratio) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
        matchSurfaceView(ratio);
    }

    public void matchSurfaceView(Ratio ratio) {
        int screenWidth = App.getApp().getScreenWidth();
        int screenHeight = App.getApp().getScreenHeight();
        int viewHeight = 0;
        switch (ratio) {
            case R_4x3:
                viewHeight = (int) ((screenWidth * 1.0f) / 3 * 4);
                break;
            case R_16x9:
                viewHeight = (int) ((screenWidth * 1.0f) / 9 * 16);
                break;
        }
        int marginTop = (screenHeight - viewHeight) / 2;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, viewHeight);
        layoutParams.topMargin = marginTop;
        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPictureRatioChange(CameraParamter cameraParamter, Ratio ratio) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onPictureQualityChange(CameraParamter cameraParamter, Quality quality) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onColorEffectChange(CameraParamter cameraParamter, ColorEffect colorEffect) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onSceneModeChange(CameraParamter cameraParamter, SceneMode sceneMode) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onWhiteBalance(CameraParamter cameraParamter, WhiteBalance whiteBalance) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onAntiBindingChange(CameraParamter cameraParamter, String antiBinding) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    @Override
    public void onVideoQualityChange(CameraParamter cameraParamter, Quality quality) {
        mCameraParamter = cameraParamter;
        mCamera.setParameters(mCameraParamter.getParameters());
    }

    /**
     * 初始化录制
     */
    private void initRecord(boolean isAsync) {
        mOnRecordPreparedListener = new OnRecordPreparedListener() {
            @Override
            public void onPrepared() {
                isPrepared = true;
            }

            @Override
            public void onFail(Exception e) {
                isPrepared = false;
                Toast.makeText(getContext(), "录制准备失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSizeChange(int width, int height) {
                Log.e(TAG, "onSizeChange: \n"
                        + "width : " + width + "\n"
                        + "height : " + height + "\n");
                Ratio ratio = Ratio.pickRatio(width, height);
                matchSurfaceView(ratio);
            }
        };
        if (isAsync) {
            new RecordPrepareTask(mOnRecordPreparedListener).execute();
        } else {
            isPrepared = prepareVideoRecorder(mOnRecordPreparedListener);
            if (isPrepared) {
                startRecord();
            } else {
                releaseRecord();
            }
        }
    }

    private boolean prepareVideoRecorder(OnRecordPreparedListener onRecordPreparedListener) {
        if (mCamera == null) {
            initCamera(CAMERA_ID_BACK);
            startCameraPreview();
            mHandler.sendEmptyMessage(HANDLER_UPDATE_VIEW);
        }

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraParamter.getOptimalVideoSize(
                mSupportedVideoSizes,
                mSupportedPreviewSizes,
                surfaceView.getWidth(),
                surfaceView.getHeight());

        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;


        Camera.Size videoSize = mCameraParamter.getVideoSize();
        if( videoSize == null ){

        }
        if (videoSize != null) {
            if (videoSize.width > profile.videoFrameWidth) {
                profile.videoFrameWidth = videoSize.width;
                profile.videoFrameHeight = videoSize.height;
            }
        }

        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);

        mCamera.setParameters(parameters);
        if (onRecordPreparedListener != null) {
            onRecordPreparedListener.onSizeChange(profile.videoFrameWidth, profile.videoFrameHeight);
        }

        mRecorder = new MediaRecorder();
        mCamera.unlock();
        mRecorder.setCamera(mCamera);

        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mRecorder.setProfile(profile);

        outputRecordFile = FilePathUtils.createFile(FilePathUtils.FILENAME_RECORD);

        if (outputRecordFile == null) {
            if (onRecordPreparedListener != null) {
                onRecordPreparedListener.onFail(new FileNotFoundException("Create record file fail !!!"));
            }
            return false;
        }
        mRecorder.setOutputFile(outputRecordFile.getPath());
        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseRecord();
            if (onRecordPreparedListener != null) {
                onRecordPreparedListener.onFail(e);
            }
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseRecord();
            if (onRecordPreparedListener != null) {
                onRecordPreparedListener.onFail(e);
            }
            return false;
        }
        if (onRecordPreparedListener != null) {
            onRecordPreparedListener.onPrepared();
        }
        return true;
    }

    private void startRecord() {
        if (isPrepared && isRecording == false) {
            try {
                mRecorder.start();
                isRecording = true;
                mHandler.sendEmptyMessage(HANDLER_RECORD_TIME_START);
            } catch (Exception e) {
                e.printStackTrace();
                releaseRecord();
            }
        }
    }

    private void stopRecord() {
        if (isPrepared && isRecording) {
            try {
                mRecorder.stop();
                mHandler.sendEmptyMessage(HANDLER_RECORD_TIME_STOP);
            } catch (Exception e) {
                outputRecordFile.delete();
            } finally {
                releaseRecord();
            }
        }
    }

    private void releaseRecord() {
        if (mRecorder != null) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            isPrepared = false;
            isRecording = false;
        }
        if (mCamera != null) {
            mCameraParamter.setPreviewSize(mCameraParamter.getPreviewRatio(), 0);
            mCamera.setParameters(mCameraParamter.getParameters());
            matchSurfaceView(mCameraParamter.getPreviewRatio());
        }
        mHandler.sendEmptyMessage(HANDLER_RECORD_TIME_RESET);
        mHandler.sendEmptyMessage(HANDLER_UPDATE_VIEW);
    }

    class RecordPrepareTask extends AsyncTask<Void, Void, Boolean> {

        private OnRecordPreparedListener onRecordPreparedListener;

        public RecordPrepareTask(OnRecordPreparedListener onRecordPreparedListener) {
            this.onRecordPreparedListener = onRecordPreparedListener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (prepareVideoRecorder(mOnRecordPreparedListener)) {
                startRecord();
            } else {
                releaseRecord();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mHandler.sendEmptyMessage(HANDLER_UPDATE_VIEW);
        }
    }

    public interface OnRecordPreparedListener {
        void onPrepared();

        void onFail(Exception e);

        void onSizeChange(int width, int height);
    }

    public class CameraTouchListener implements View.OnTouchListener {
        private Context mContext;

        public CameraTouchListener(Context context) {
            this.mContext = context;
            mScaleDetector = new ScaleGestureDetector(context, new CameraTouchListener.ScaleListener());
            mTapDetector = new GestureDetector(context, new CameraTouchListener.TapListener());
        }

        private ScaleGestureDetector mScaleDetector;
        private GestureDetector mTapDetector;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getPointerCount() > 1) {
                mScaleDetector.onTouchEvent(event);
                return true;
            } else {
                mTapDetector.onTouchEvent(event);
                return true;
            }
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

            float prevScaleFactor;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                scaleFactor = BigDecimal.valueOf(scaleFactor).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
                if (Float.compare(scaleFactor, 1.0f) == 0 || Float.compare(scaleFactor, prevScaleFactor) == 0) {
                    return true;
                }
                if (scaleFactor > 1f) {
                    mCameraParamter.zoomIn();
                    mCamera.setParameters(mCameraParamter.getParameters());
                }
                if (scaleFactor < 1f) {
                    mCameraParamter.zoomOut();
                    mCamera.setParameters(mCameraParamter.getParameters());
                }
                prevScaleFactor = scaleFactor;
                return true;
            }

        }

        private class TapListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                if (mCameraParamter.getFocusMode() != FocusMode.AUTO
                        && mCameraParamter.getFocusMode() != FocusMode.MACRO) {
                    float x = event.getX();
                    float y = event.getY();
                    Message message = new Message();
                    message.what = HANDLER_FOCUS_STATE_START;
                    message.arg1 = (int) x;
                    message.arg2 = (int) y;
                    mHandler.sendMessage(message);
                    mCamera.cancelAutoFocus();
                    showPoint(mCameraParamter.getParameters(), (int) x, (int) y);
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            Message focusMessage = new Message();
                            focusMessage.what = HANDLER_FOCUS_STATE_UPDATE;
                            focusMessage.obj = success;
                            mHandler.sendMessage(focusMessage);
                        }
                    });
                }
                return true;
            }

            /**
             * @param parameters 相机配置参数
             * @param x          当前点击的 x 坐标
             * @param y          当前点击的 y 坐标
             */
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            private void showPoint(Camera.Parameters parameters, int x, int y) {
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    List<Camera.Area> areas = new ArrayList<Camera.Area>();
                    //xy变换了
                    int rectY = -x * 2000 / App.getApp().getScreenWidth() + 1000;
                    int rectX = y * 2000 / App.getApp().getScreenHeight() - 1000;

                    int left = rectX < -900 ? -1000 : rectX - 100;
                    int top = rectY < -900 ? -1000 : rectY - 100;
                    int right = rectX > 900 ? 1000 : rectX + 100;
                    int bottom = rectY > 900 ? 1000 : rectY + 100;
                    Rect area1 = new Rect(left, top, right, bottom);
                    areas.add(new Camera.Area(area1, 800));
                    parameters.setMeteringAreas(areas);
                }
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                mCamera.setParameters(parameters);
            }
        }
    }


}
