package com.bulsu.quickeyshare.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bulsu.quickeyshare.fragment.BaseFileChooserFragment;

/**
 * Created by mykelneds on 17/02/2017.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];

    public MainPagerAdapter(FragmentManager fm, CharSequence[] titles) {
        super(fm);
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {


        Bundle b = new Bundle();
        b.putInt("category", position);

//        switch (position) {
//
//            case 0:
//                return new FileChooserFragment();
//            case 1:
//                return new VideoChooserFragment();
//            case 2:
//                return new ImageChooserFragment();
//            case 3:
//                return new MusicChooserFragment();
//        }

        BaseFileChooserFragment fragment = new BaseFileChooserFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
