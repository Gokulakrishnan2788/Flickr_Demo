package com.gk.flickr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.gk.flickr.R;




/**
 * Created by gturedi on 8.02.2017.
 */
public class PhotoViewHolder
        extends RecyclerView.ViewHolder {


    protected ImageView image;

    public PhotoViewHolder(View itemView) {
        super(itemView);
        image=(ImageView)itemView.findViewById(R.id.image);
    }

}