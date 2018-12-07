package com.gk.flickr.model.event;

import com.gk.flickr.model.IamgeModel;

import java.util.List;

/**
 * Created by gturedi on 7.02.2017.
 */
public class SearchEvent
        extends BaseServiceEvent<List<IamgeModel>> {

    public SearchEvent(List<IamgeModel> item, Throwable exception) {
        super(item, exception);
    }

}