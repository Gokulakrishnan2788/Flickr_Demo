package com.gk.flickr.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gk.flickr.R;
import com.gk.flickr.model.ImageSize;
import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.util.AppUtil;
import com.gk.flickr.util.RowClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gturedi on 8.02.2017.
 */
public class ImageListAdapter
        extends RecyclerView.Adapter {

    private static final int VIEW_ITEM = 1;
    private static final int VIEW_PROG = 0;

    private final List<IamgeModel> items = new ArrayList<>();
    private RowClickListener<IamgeModel> rowClickListener;

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photo, parent, false);
            return new PhotoViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_progress, parent, false);
            return new ProgressViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder vh = (PhotoViewHolder) holder;
            IamgeModel item = items.get(position);
            AppUtil.bindImage(item.getImageUrl(ImageSize.MEDIUM), vh.image, true);
            if (rowClickListener != null) {
                vh.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rowClickListener.onRowClicked(holder.getAdapterPosition(),
                                items.get(holder.getAdapterPosition()));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setRowClickListener(RowClickListener<IamgeModel> rowClickListener) {
        this.rowClickListener = rowClickListener;
    }

    public void addAll(List<IamgeModel> newItems) {
        if (newItems == null) {
            items.add(null);
            notifyItemInserted(getItemCount() - 1);
        } else {
            items.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    public List<IamgeModel> getAll() {
        return items;
    }

    public void remove(int index) {
        if (index == -1) return;
        items.remove(index);
        notifyItemRemoved(index);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

}