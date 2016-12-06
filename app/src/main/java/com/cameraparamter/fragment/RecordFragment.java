package com.cameraparamter.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cameraparamter.R;
import com.cameraparamter.adapter.RecordRecyclerAdapter;
import com.cameraparamter.fragment.base.BaseFragment;
import com.cameraparamter.interfaces.OnRecordInfoUpdateListener;
import com.cameraparamter.utils.FilePathUtils;
import com.cameraparamter.utils.FileUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */

public class RecordFragment extends BaseFragment implements OnRecordInfoUpdateListener {

    private static final String TAG = "RecordFragment";

    private TextView tv_fragment_record;
    private RecyclerView recycler_record;
    private RecordRecyclerAdapter mAdapter;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_record;
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
        tv_fragment_record = (TextView) root.findViewById(R.id.tv_fragment_record);

        recycler_record = (RecyclerView) root.findViewById(R.id.recycler_record);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_record.setLayoutManager(layoutManager);

        mAdapter = new RecordRecyclerAdapter(getContext());
        recycler_record.setAdapter(mAdapter);
    }

    @Override
    public void initEvent() {
        mAdapter.setOnItemRemoveListener(new RecordRecyclerAdapter.OnItemRemoveListener() {
            @Override
            public void onItemRemove() {
                updatePhotos();
            }
        });
    }

    private void updatePhotos() {
        if (mAdapter != null) {
            mAdapter.clear();
            List<String> recordPaths = FileUtils.loadFile(FilePathUtils.recordDir, "mp4");
            if (recordPaths.size() == 0) {
                recycler_record.setVisibility(View.INVISIBLE);
                tv_fragment_record.setVisibility(View.VISIBLE);
                tv_fragment_record.setText("No Record");
            } else {
                tv_fragment_record.setVisibility(View.INVISIBLE);
                recycler_record.setVisibility(View.VISIBLE);
                mAdapter.addAll(recordPaths);
            }
        }
    }


    @Override
    public void onRecordInfoUpdate() {
        updatePhotos();
    }
}
