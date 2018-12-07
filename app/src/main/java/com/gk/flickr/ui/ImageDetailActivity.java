package com.gk.flickr.ui;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gk.flickr.R;
import com.gk.flickr.adapter.ImageDetailAapter;
import com.gk.flickr.model.ImageSize;
import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.model.event.ClickEvent;
import com.gk.flickr.model.event.DetailEvent;
import com.gk.flickr.service.FlickrService;
import com.gk.flickr.util.AppUtil;
import com.gk.flickr.util.ParallaxPageTransformer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;




/**
 * Created by gturedi on 8.02.2017.
 */
public class ImageDetailActivity
        extends BaseActivity implements View.OnClickListener {

    private static final String EXTRA_INDEX = "EXTRA_INDEX";
    private static final String EXTRA_ITEMS = "EXTRA_ITEMS";
    private static final String ERROR_LITERAL = "-";

    private List<IamgeModel> items;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private DetailEvent detailEvent;


    protected ViewPager pager;

    protected TextView tvOwner;

    protected TextView tvTitle;

    protected TextView tvDate;

    protected TextView tvViewCount;

    protected View lnrFooter;

    protected View ivClose;

    protected View ivInfo;

    protected View ivShare;

    public static Intent createIntent(Context context, int index, List<IamgeModel> items) {
        return new Intent(context, ImageDetailActivity.class)
                .putExtra(EXTRA_INDEX, index)
                .putExtra(EXTRA_ITEMS, (Serializable) items);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        items = (List<IamgeModel>) getIntent().getSerializableExtra(EXTRA_ITEMS);
        if (index == -1) {
            finish();
        } else if (!AppUtil.isConnected()) {
            showConnectionError();
        } else {
            pager.setPageTransformer(false, new ParallaxPageTransformer(R.id.image));
            pager.setAdapter(new ImageDetailAapter(getSupportFragmentManager(), items));
            pager.setCurrentItem(index);
            onPageSelected(index);
        }

        AppUtil.setVectorBg(ivClose, R.drawable.ic_close_24dp, android.R.color.white, R.color.gray2);
        AppUtil.setVectorBg(ivInfo, R.drawable.ic_info_outline_24dp, android.R.color.white, R.color.gray2);
        AppUtil.setVectorBg(ivShare, R.drawable.ic_share_24dp, android.R.color.white, R.color.gray2);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //showLoadingDialog();
                tvOwner.setText(R.string.loading);
                tvTitle.setText(R.string.loading);
                tvDate.setText(R.string.loading);
                tvViewCount.setText(R.string.loading);
                flickrService.getDetailAsync(items.get(position).id);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        tvOwner = (TextView) findViewById(R.id.tvOwner);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvViewCount = (TextView) findViewById(R.id.tvViewCount);
        lnrFooter = (View) findViewById(R.id.lnrFooter);
        ivClose = (View) findViewById(R.id.ivClose);
        ivInfo = (View) findViewById(R.id.ivInfo);
        ivShare = (View) findViewById(R.id.ivShare);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceEvent(DetailEvent event) {
        //dismissLoadingDialog();
        detailEvent = event;
        if (event.exception == null) {
            tvOwner.setText(event.item.owner.toString());
            tvTitle.setText(event.item.title.toString());
            tvDate.setText(event.item.getFormattedDate());
            tvViewCount.setText(getResources().getQuantityString(R.plurals.views, event.item.views, event.item.views));
        } else {
            tvOwner.setText(ERROR_LITERAL);
            tvTitle.setText(ERROR_LITERAL);
            tvDate.setText(ERROR_LITERAL);
            tvViewCount.setText(ERROR_LITERAL);
            showSnack(R.string.errorMessage);
        }
    }

    // fired by child fragment
    // child's photoView absorbs touch event so parent's touch events not fired
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickEvent(ClickEvent event) {
        int value = lnrFooter.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        Log.i("pagerClick: " , value+"");
        lnrFooter.setVisibility(value);
        ivClose.setVisibility(value);
        tvOwner.setVisibility(value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivClose:
                onCloseClick();
                break;
            case R.id.ivInfo:
                onInfoClick();
                break;
            case R.id.ivShare:
                onShareClick();
                break;

        }
    }

    public void onCloseClick() {
        onBackPressed();
    }


    public void onInfoClick() {
        if (detailEvent == null || detailEvent.item == null) return;
        showInfoDialog(getString(R.string.description), detailEvent.item.description.toString());
    }


    public void onShareClick() {
        if (detailEvent == null || detailEvent.item == null) return;
        String subject = detailEvent.item.title._content;
        String text = items.get(pager.getCurrentItem()).getImageUrl(ImageSize.LARGE);
        startActivity(AppUtil.createShareIntent(subject, text));
    }


    protected void onPageSelected(int position) {
        //showLoadingDialog();
        tvOwner.setText(R.string.loading);
        tvTitle.setText(R.string.loading);
        tvDate.setText(R.string.loading);
        tvViewCount.setText(R.string.loading);
        flickrService.getDetailAsync(items.get(position).id);
    }
}