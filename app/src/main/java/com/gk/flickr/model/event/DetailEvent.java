package com.gk.flickr.model.event;

import com.gk.flickr.model.ImageInfoModel;

/**
 * Created by gturedi on 7.02.2017.
 */
public class DetailEvent
        extends BaseServiceEvent<ImageInfoModel> {

    public DetailEvent(ImageInfoModel item, Throwable exception) {
        super(item, exception);
    }

}