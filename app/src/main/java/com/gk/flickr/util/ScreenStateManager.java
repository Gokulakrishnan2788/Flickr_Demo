package com.gk.flickr.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gk.flickr.R;





/**
 * Created by gturedi on 16.02.2017.
 */
public class ScreenStateManager {

    private final ViewGroup root;
    private final View stateRoot;

    ProgressBar stateProgress;
    ImageView stateImage;
    TextView stateMessage;

    public ScreenStateManager(ViewGroup root) {
        this.root = root;
        stateRoot = LayoutInflater.from(root.getContext()).inflate(R.layout.template_state, root, false);
         stateProgress = (ProgressBar) stateRoot.findViewById(R.id.stateProgress);
         stateImage = (ImageView) stateRoot.findViewById(R.id.stateImage);
         stateMessage = (TextView) stateRoot.findViewById(R.id.stateMessage);
        root.addView(stateRoot);

    }

    public void hideAll() {
        setChildrenVisibility(View.VISIBLE);
        stateRoot.setVisibility(View.GONE);
    }

    public void showLoading() {
        setChildrenVisibility(View.GONE);
        stateRoot.setVisibility(View.VISIBLE);
        stateProgress.setVisibility(View.VISIBLE);
        stateImage.setVisibility(View.GONE);
        stateMessage.setVisibility(View.GONE);
    }

    public void showConnectionError() {
        showMessage(R.drawable.ic_signal_wifi_off_24dp, R.string.connectionErrorMessage);
    }

    public void showError( int message) {
        showMessage(R.drawable.ic_error_24dp, message);
    }

    public void showEmpty( int message) {
        showMessage(R.drawable.ic_inbox_24dp, message);
    }

    public void showMessage(int image,  int message) {
        setChildrenVisibility(View.GONE);
        stateRoot.setVisibility(View.VISIBLE);
        stateProgress.setVisibility(View.GONE);
        stateImage.setVisibility(View.VISIBLE);
        stateImage.setImageResource(image);
        stateMessage.setVisibility(View.VISIBLE);
        stateMessage.setText(message);
    }

    private void setChildrenVisibility(int visibility) {
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setVisibility(visibility);
        }
    }

}
