package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import java.util.List;

/**
 * Created by Jayabrata Dhakai on 11/26/2016.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private final String LOG_TAG = NewsLoader.class.getSimpleName();

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "In onStartLoading()");
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, "In loadInBackground()");
        return QueryUtils.fetchNewsData(mUrl);
    }
}
