package com.gk.flickr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.ui.ImageDetailFragment;

import java.util.List;

/**
 * Created by gturedi on 8.02.2017.
 */
public class ImageDetailAapter
        extends FragmentStatePagerAdapter {

    private final List<IamgeModel> items;

    public ImageDetailAapter(FragmentManager fm, List<IamgeModel> items) {
        super(fm);
        this.items = items;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageDetailFragment.newInstance(items.get(position));
    }

    @Override
    public int getCount() {
        return items.size();
    }

}