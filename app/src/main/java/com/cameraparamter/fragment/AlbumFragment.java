package com.cameraparamter.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cameraparamter.R;
import com.cameraparamter.adapter.FragmentPagerAdapter;
import com.cameraparamter.fragment.base.BaseFragment;
import com.cameraparamter.interfaces.OnAlbumInfoUpdateListener;


/**
 * Created by Administrator on 2016/11/29.
 */

public class AlbumFragment extends BaseFragment implements OnAlbumInfoUpdateListener {

    private TabLayout tab_album;
    private ViewPager vp_album;
    private FragmentPagerAdapter adapter;

    private PhotoFragment photoFragment;
    private RecordFragment recordFragment;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_album;
    }

    @Override
    public void initArguments(Bundle arguments) {

    }

    @Override
    public void initView(View root) {
        tab_album = (TabLayout) root.findViewById(R.id.tab_album);
        vp_album = (ViewPager) root.findViewById(R.id.vp_album);
        adapter = new FragmentPagerAdapter(getChildFragmentManager());
        vp_album.setAdapter(adapter);
        tab_album.setupWithViewPager(vp_album);

        photoFragment = new PhotoFragment();
        adapter.add(photoFragment, "Photo");
        recordFragment = new RecordFragment();
        adapter.add(recordFragment, "Record");
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onPhotoInfoUpdate() {
        photoFragment.onPhotoInfoUpdate();
    }

    @Override
    public void onRecordInfoUpdate() {
        recordFragment.onRecordInfoUpdate();
    }
}
