package com.bulsu.quickeyshare.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.adapter.MainPagerAdapter;
import com.bulsu.quickeyshare.sliding.SlidingTabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FileChooserActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    @Bind(R.id.pager)
    ViewPager pager;

    CharSequence title[] = {"Files", "Video", "Images", "Music"};
    MainPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        ButterKnife.bind(this);

        initToolbar();
        setupPager();

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupPager() {

        adapter = new MainPagerAdapter(getSupportFragmentManager(), title);
        pager.setAdapter(adapter);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.blue6);
            }
        });
        tabs.setViewPager(pager);

    }
}
