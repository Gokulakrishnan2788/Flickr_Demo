package com.gk.flickr.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gk.flickr.R;
import com.gk.flickr.model.ImageSize;
import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.model.event.ClickEvent;
import com.gk.flickr.util.AppUtil;

import org.greenrobot.eventbus.EventBus;




/**
 * Created by gturedi on 8.02.2017.
 */
public class ImageDetailFragment
        extends BaseFragment {

    private static final String EXTRA_ITEM = "EXTRA_ITEM";

     ImageView image;

    public static Fragment newInstance(IamgeModel item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_ITEM, item);
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        image=(ImageView) root.findViewById(R.id.image);
        return root;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IamgeModel item = (IamgeModel) getArguments().getSerializable(EXTRA_ITEM);
        AppUtil.bindImage(item.getImageUrl(ImageSize.LARGE), image, false);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("","setOnViewTapListener");
                EventBus.getDefault().post(new ClickEvent());
            }
        });


    }

}