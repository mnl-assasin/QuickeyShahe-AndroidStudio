package com.bulsu.quickeyshare.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bulsu.quickeyshare.fragment.BaseLockChooserFragment;

/**
 * Created by mykelneds on 17/02/2017.
 */

public class LockPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence titles[];

    public LockPagerAdapter(FragmentManager fm, CharSequence[] titles) {
        super(fm);
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {


        Bundle b = new Bundle();
        b.putInt("category", position+1);

        BaseLockChooserFragment fragment = new BaseLockChooserFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
