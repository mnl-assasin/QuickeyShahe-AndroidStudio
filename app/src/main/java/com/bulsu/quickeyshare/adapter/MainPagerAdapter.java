package com.bulsu.quickeyshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bulsu.quickeyshare.fragment.FileChooserFragment;
import com.bulsu.quickeyshare.fragment.ImageChooserFragment;
import com.bulsu.quickeyshare.fragment.MusicChooserFragment;
import com.bulsu.quickeyshare.fragment.VideoChooserFragment;

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

        switch (position) {
            case 0:
                return new FileChooserFragment();
            case 1:
                return new VideoChooserFragment();
            case 2:
                return new ImageChooserFragment();
            case 3:
                return new MusicChooserFragment();
        }

        return null;
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
