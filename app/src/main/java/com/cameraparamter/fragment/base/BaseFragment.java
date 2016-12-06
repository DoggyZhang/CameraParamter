package com.cameraparamter.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/11/29.
 */

public abstract class BaseFragment extends Fragment {

    private int layoutID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutID = getLayoutID();
        return inflater.inflate(layoutID, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArguments(getArguments());
        initView(view);
        initEvent();
    }

    public abstract int getLayoutID();

    public abstract void initArguments(Bundle arguments);

    public abstract void initView(View root);

    public abstract void initEvent();
}
