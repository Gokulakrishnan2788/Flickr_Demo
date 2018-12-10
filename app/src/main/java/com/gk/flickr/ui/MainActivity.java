package com.gk.flickr.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gk.flickr.R;
import com.gk.flickr.adapter.ImageListAdapter;
import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.model.event.SearchEvent;
import com.gk.flickr.service.FlickrService;
import com.gk.flickr.util.AppUtil;
import com.gk.flickr.util.RowClickListener;
import com.gk.flickr.util.ScreenStateManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class MainActivity
        extends BaseActivity
        implements RowClickListener<IamgeModel>,
        SwipeRefreshLayout.OnRefreshListener {

    private int page = 1;
    private boolean isLoading;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private ImageListAdapter adapter;
    private ScreenStateManager screenStateManager;
    private MenuItem searchMenu;
    private String mSearchString = "";
    private String defaultKey="Flower";


    protected Toolbar toolbar;

    protected SwipeRefreshLayout swipe;

    protected LinearLayout linear;

    protected RecyclerView recycler;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        linear = (LinearLayout) findViewById(R.id.linear);
        recycler = (RecyclerView) findViewById(R.id.recycler);


        swipe.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipe.setOnRefreshListener(this);

        adapter = new ImageListAdapter();
        adapter.setRowClickListener(this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        setScrollListener();

        screenStateManager = new ScreenStateManager(linear);

        sendRequest(defaultKey);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRowClicked(int row, IamgeModel item) {
        startActivity(ImageDetailActivity.createIntent(this, row, adapter.getAll()));
    }

    @Override
    public void onRefresh() {
        page = 1;
        sendRequest(defaultKey);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        isLoading = false;

        // fired by pull to refresh
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
            adapter.clear();
        }

        if (isScreenEmpty()) {
            if (event.exception != null) {
                screenStateManager.showError(R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                screenStateManager.showEmpty(R.string.emptyText);
            } else {
                screenStateManager.hideAll();
                adapter.addAll(event.item);
            }
        } else {
            adapter.remove(adapter.getItemCount() - 1); //remove progress item
            if (event.exception != null) {
                showSnack(R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                showSnack(R.string.emptyText);
            } else {
                adapter.addAll(event.item);
            }
        }
    }

    private void sendRequest(String searchKey) {
        Log.i("sendRequest: " , page+"");
        if (AppUtil.isConnected()) {
            isLoading = true;
            flickrService.searchAsync(page++,searchKey);
            if (isScreenEmpty())
                screenStateManager.showLoading();
            else
                adapter.addAll(null); // add null , so the adapter will check view_type and show progress bar at bottom
        } else {
            swipe.setRefreshing(false);
            if (isScreenEmpty()) screenStateManager.showConnectionError();
            else showConnectionError();
        }
    }

    private void setScrollListener() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= FlickrService.PAGE_SIZE) {
                    sendRequest(defaultKey);
                }
            }
        });
    }

    private boolean isScreenEmpty() {
        return adapter == null || adapter.getItemCount() == 0;
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.image_search_menu, menu);
      final  MenuItem mSearch = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search..");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                page = 1;
                if(query!=null)
                {
                    if(!query.isEmpty())
                    {
                        defaultKey=query;
                        adapter.clear();
                        mSearchView.setIconified(true);
                        mSearchView.clearFocus();

                        // call the request here

                        // call collapse action view on 'MenuItem'
                        mSearch.collapseActionView();
                        sendRequest(query);
                        return false;
                    }else
                    {
                         Toast.makeText(getApplicationContext(), "Please Enter the Valid Key", Toast.LENGTH_LONG).show();
                    }
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}