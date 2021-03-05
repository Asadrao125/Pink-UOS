package com.gexton.pink_uos.adapters;

import com.gexton.pink_uos.fragments.ReadNotificationFragment;
import com.gexton.pink_uos.fragments.UnreadNotificationFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new UnreadNotificationFragment();
        } else if (position == 1) {
            fragment = new ReadNotificationFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = "Unread Notification";
        } else if (position == 1) {
            title = "Read Notification";
        }
        return title;
    }
}