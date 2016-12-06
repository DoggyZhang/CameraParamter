package com.cameraparamter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.cameraparamter.adapter.FragmentPagerAdapter;
import com.cameraparamter.fragment.AlbumFragment;
import com.cameraparamter.fragment.MainFragment;


public class MainActivity extends AppCompatActivity {

    private ViewPager vp_main;
    private FragmentPagerAdapter adapter;

    private MainFragment mainFragment;
    private AlbumFragment albumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();
    }

    private void initView() {
        vp_main = (ViewPager) findViewById(R.id.vp_main);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager());
        vp_main.setAdapter(adapter);

        mainFragment = new MainFragment();
        adapter.add(mainFragment);
        albumFragment = new AlbumFragment();
        adapter.add(albumFragment);
    }

    private void initEvent() {

    }

    public void nextFragment() {
        if (vp_main != null
                && vp_main.getAdapter() != null
                && vp_main.getAdapter().getCount() > 1) {
            vp_main.setCurrentItem(1, true);
        }
    }

    public void updateAlbum() {
        albumFragment.onPhotoInfoUpdate();
        albumFragment.onRecordInfoUpdate();
    }
}
