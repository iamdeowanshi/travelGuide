package com.ithakatales.android.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;
import com.ithakatales.android.ui.adapter.UserOnBoardPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author farhanali
 */
public class UserOnBoardActivity extends BaseActivity implements UserOnBoardPagerAdapter.OnBoardClickListener {

    @Bind(R.id.view_pager) ViewPager viewPager;

    private UserOnBoardPagerAdapter onBoardPagerAdapter;

    private List<Integer> slideResources = new ArrayList<Integer>() {{
        add(R.drawable.user_onboard_slide_1);
        add(R.drawable.user_onboard_slide_2);
        add(R.drawable.user_onboard_slide_3);
        add(R.drawable.user_onboard_slide_4);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_on_board);

        onBoardPagerAdapter = new UserOnBoardPagerAdapter(slideResources);
        onBoardPagerAdapter.setOnBoardClickListener(this);
        viewPager.setAdapter(onBoardPagerAdapter);
    }

    @Override
    public void onCloseClicked(int position) {
        startActivityClearTop(LaunchActivity.class, null);
    }

    @Override
    public void onNextClicked(int position) {
        // final slide
        if (position == slideResources.size() -1) {
            startActivityClearTop(LaunchActivity.class, null);
            return;
        }

        viewPager.setCurrentItem(position + 1);
    }

}
