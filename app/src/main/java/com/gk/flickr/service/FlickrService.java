package com.gk.flickr.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gk.flickr.App;
import com.gk.flickr.model.event.DetailEvent;
import com.gk.flickr.model.ImageInfoModel;
import com.gk.flickr.model.IamgeModel;
import com.gk.flickr.model.event.SearchEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Key;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by gturedi on 7.02.2017.
 * <p>
 * used greenRobot's eventBus for asynchronous operations because it is android optimized for android so take care
 * of activity/fragment lifecycle. when activity destroyed it does not receive events thus prevents npe.
 */
public final class FlickrService {

    public static final FlickrService INSTANCE = new FlickrService();
    public static final int PAGE_SIZE = 15;
    private static final long CACHE_SIZE_IN_MB = 10 * 1024 * 1024;
    private static final String API_KEY = "04a42d236e746206fbbf64245342dd2d";
    private static final String URL_BASE = "https://api.flickr.com/services/rest/"
            + "?format=json&nojsoncallback=1&api_key=" + API_KEY;
    private static String SEARCH_KEY = "";
    private static String URL_SEARCH = "&method=flickr.photos.search&tags=mode&text="+SEARCH_KEY+"&per_page=" + PAGE_SIZE + "&page=";
    private static final String URL_DETAIL = "&method=flickr.photos.getInfo&photo_id=";
    private static final String CACHE_PATH = App.getContext().getCacheDir().getAbsolutePath();
    private static final String COLUMN_PHOTO = "photo";
    private static final String COLUMN_PHOTOS = "photos";


    private FlickrService() {
        // no instances
    }

    public void searchAsync(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<IamgeModel> items = search(page);
                    EventBus.getDefault().post(new SearchEvent(items, null));
                } catch (IOException | JSONException e) {
                    Log.e("",e.toString());
                    EventBus.getDefault().post(new SearchEvent(null, e));
                }
            }
        }).start();
    }

    public void searchAsync(final int page,String key) {
        SEARCH_KEY= key;
        URL_SEARCH = "&method=flickr.photos.search&tags=mode&text="+SEARCH_KEY+"&per_page=" + PAGE_SIZE + "&page=";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<IamgeModel> items = search(page);
                    EventBus.getDefault().post(new SearchEvent(items, null));
                } catch (IOException | JSONException e) {
                    Log.e("",e.toString());
                    EventBus.getDefault().post(new SearchEvent(null, e));
                }
            }
        }).start();
    }
    public void getDetailAsync(final long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ImageInfoModel item = getDetail(id);
                    EventBus.getDefault().post(new DetailEvent(item, null));
                } catch (IOException | JSONException e) {
                    Log.e("",e.toString());
                    EventBus.getDefault().post(new DetailEvent(null, e));
                }
            }
        }).start();
    }

    private ImageInfoModel getDetail(long id) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(URL_BASE + URL_DETAIL + id)
                .build();

        Response response = getClient().newCall(request).execute();
        String json = response.body().string();
        JSONObject jsonObject = new JSONObject(json).getJSONObject(COLUMN_PHOTO);
        return getGson().fromJson(jsonObject.toString(), ImageInfoModel.class);
    }

    private List<IamgeModel> search(int page) throws IOException, JSONException {


        Request request = new Request.Builder()
                .url(URL_BASE + URL_SEARCH + page)
                .build();

        Response response = getClient().newCall(request).execute();
        String json = response.body().string();
        JSONArray jsonArray = new JSONObject(json).getJSONObject(COLUMN_PHOTOS).getJSONArray(COLUMN_PHOTO);

        Type listType = new TypeToken<List<IamgeModel>>() {
        }.getType();
        return getGson().fromJson(jsonArray.toString(), listType);
    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(new Cache(new File(CACHE_PATH), CACHE_SIZE_IN_MB))
                .build();
    }

    private Gson getGson() {
        return new GsonBuilder().create();
    }

}