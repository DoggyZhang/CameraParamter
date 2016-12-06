package com.cameraparamter.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/11/29.
 */

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> titles;

    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    public void add(Fragment fragment) {
        this.fragments.add(fragment);
        notifyDataSetChanged();
    }

    public void add(Fragment fragment, @NonNull String title) {
        this.fragments.add(fragment);
        this.titles.add(title);
        notifyDataSetChanged();
    }

    public void addAll(Collection fragments) {
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }

    public void addAll(Collection fragments, @NonNull Collection titles) {
        this.fragments.addAll(fragments);
        this.titles.addAll(titles);
        notifyDataSetChanged();
    }

    public void clear() {
        this.fragments.clear();
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (fragments.size() == titles.size()) {
            return titles.get(position);
        }
        return null;
    }
}
