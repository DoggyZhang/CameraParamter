package com.cameraparamter.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cameraparamter.R;
import com.cameraparamter.adapter.PhotoRecyclerAdapter;
import com.cameraparamter.fragment.base.BaseFragment;
import com.cameraparamter.interfaces.OnPhotoInfoUpdateListener;
import com.cameraparamter.utils.FilePathUtils;
import com.cameraparamter.utils.FileUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */

public class PhotoFragment extends BaseFragment implements OnPhotoInfoUpdateListener {
    private static final String TAG = "PhotoFragment";

    private TextView tv_fragment_photo;
    private RecyclerView recycler_photo;
    private PhotoRecyclerAdapter mAdapter;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_photo;
    }

    @Override
    public void initArguments(Bundle arguments) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            updatePhotos();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }

    @Override
    public void initView(View root) {
        tv_fragment_photo = (TextView) root.findViewById(R.id.tv_fragment_photo);

        recycler_photo = (RecyclerView) root.findViewById(R.id.recycler_photo);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        recycler_photo.setLayoutManager(layoutManager);

        mAdapter = new PhotoRecyclerAdapter(getContext());
        recycler_photo.setAdapter(mAdapter);
    }

    @Override
    public void initEvent() {
        mAdapter.setOnItemRemoveListener(new PhotoRecyclerAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemove() {
                updatePhotos();
            }
        });
    }

    private void updatePhotos() {
        if (mAdapter != null) {
            mAdapter.clear();
            List<String> photoPaths = FileUtils.loadFile(FilePathUtils.photoDir, "jpeg");
            if (photoPaths.size() == 0) {
                recycler_photo.setVisibility(View.INVISIBLE);
                tv_fragment_photo.setVisibility(View.VISIBLE);
                tv_fragment_photo.setText("No Photo");
            } else {
                recycler_photo.setVisibility(View.VISIBLE);
                tv_fragment_photo.setVisibility(View.INVISIBLE);
                mAdapter.addAll(photoPaths);
            }
        }
    }

    @Override
    public void onPhotoInfoUpdate() {
        updatePhotos();
    }
}
