package com.cameraparamter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.cameraparamter.R;
import com.cameraparamter.adapter.PhotoPagerAdapter;
import com.cameraparamter.utils.FilePathUtils;
import com.cameraparamter.utils.FileUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/12/2.
 */

public class PhotoDetailActivity extends AppCompatActivity {

    private static final String TAG = "PhotoDetailActivity";
    public static final String SELECTPATH = "selectpath";
    private String selectPath;

    private ViewPager vp_detail;
    private PhotoPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initArguments();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            updatePhotos();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    private void initArguments() {
        Intent intent = getIntent();
        selectPath = intent.getStringExtra(SELECTPATH);
    }

    private void initView() {
        vp_detail = (ViewPager) findViewById(R.id.vp_main);

        mAdapter = new PhotoPagerAdapter(this);
        vp_detail.setAdapter(mAdapter);
    }

    private void initEvent() {
        vp_detail.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                ViewCompat.setScaleX(page, (float) (0.6 + 0.4 * (1 - Math.abs(position))));
                ViewCompat.setScaleY(page, (float) (0.6 + 0.4 * (1 - Math.abs(position))));
            }
        });
        vp_detail.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updatePhotos() {
        if (mAdapter != null) {
            mAdapter.clear();
            List<String> photoPaths = FileUtils.loadFile(FilePathUtils.photoDir, "jpeg");
            if (photoPaths.size() == 0) {
                Toast.makeText(this, "没有图片", Toast.LENGTH_SHORT).show();
            } else {
                mAdapter.addItems(photoPaths);
                if (!TextUtils.isEmpty(selectPath)) {
                    mAdapter.getItemPosition(selectPath);
                }
            }
        }
    }

}
