package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static android.net.Uri.parse;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    private NewsAdapter mNewsAdapter;

    private final int NEWS_LOADER_ID = 1;

    private final String GUARDIAN_QUERY = "http://content.guardianapis.com/search";

    private final String API_KEY = "51c1cf10-488a-43e6-8d46-374cc0280c47";

    private final String LOG_TAG = NewsActivity.class.getSimpleName();

    private TextView mEmptyView;

    private ListView mListView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "In onCreate()");

        setContentView(R.layout.activity_news);

        // get reference to the empty view
        mEmptyView = (TextView) findViewById(R.id.emptyView);

        // get reference to the list view
        mListView = (ListView) findViewById(R.id.listView);

        // get reference to the list view
        mProgressBar = (ProgressBar) findViewById(R.id.progressSpinner);

        // set the empty view
        mListView.setEmptyView(mEmptyView);

        List<News> newsList = new ArrayList<>();

        // Create the adapter to convert the array to views
        mNewsAdapter = new NewsAdapter(NewsActivity.this, newsList);

        // Attach the adapter to a ListView
        mListView.setAdapter(mNewsAdapter);

        // if the device is not connected to internet change the text of the empty view
        if(!isConnectedToInternet()) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setText(getString(R.string.no_internet));
        } else{
            // Initialise the custom loader
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        }

        // Set up listener on list item
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get current clicked item
                News currentNews = mNewsAdapter.getItem(position);

                // Create a browser intent
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(parse(currentNews.getWebUrl()));

                // Give option to choose an app
                String title = getResources().getString(R.string.chooser_title);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(browserIntent, title);

                // Verify the intent will resolve to at least one activity
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(NewsActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int loaderId, Bundle args) {
        Log.i(LOG_TAG, "In onCreateLoader()");

        String requestURL = getURL();

        // Should return if the requested URL is Null
        if(requestURL == null) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setText(getString(R.string.no_data_fetched));
            Toast.makeText(getApplicationContext(), "Select topics from settings menu", Toast.LENGTH_LONG).show();
            return null;
        }
        else
            return new NewsLoader(NewsActivity.this, requestURL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        Log.i(LOG_TAG, "In onLoadFinished()");
        //hide the progress bar
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.no_data_fetched);
        mNewsAdapter.clear();
        if (newsList != null && !newsList.isEmpty())
            mNewsAdapter.addAll(newsList);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.i(LOG_TAG, "In onLoaderReset()");
        mNewsAdapter.clear();
    }

    /*
     * Helper method to check if the device is connected to the internet
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Helper method which will get all the choices from SharedPreferences and form the requestURL, will return null if all choices are false else return the requestURL by prepending the GUARDIAN_QUERY and appending the API_KEY
     * @return null OR String
     */
    public String getURL(){

        // Get reference to shared preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String newsCount = sharedPreferences.getString(getString(R.string.settings_news_count_key), getString(R.string.settings_news_count_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        //the page-size count should be 0-200
        if(Integer.parseInt(newsCount) > 200 || Integer.parseInt(newsCount) < 0){
            Toast.makeText(getApplicationContext(), "Cannot fetch more than 200, fetching 150", Toast.LENGTH_SHORT).show();
            newsCount = "150";
        }

        // Get all the preferences at once
        Map<String,?> keys = sharedPreferences.getAll();
        StringBuilder queryParameter = new StringBuilder();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            if(entry.getValue().toString().equals("true")) {
                queryParameter.append(entry.getKey());
                queryParameter.append(",");
            }
        }
        switch (queryParameter.length()){
            case 0:
                return null;
            default:
                queryParameter.deleteCharAt(queryParameter.length() - 1);
        }

        Uri baseUri = Uri.parse(GUARDIAN_QUERY);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", newsCount);
        uriBuilder.appendQueryParameter("q", queryParameter.toString());
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        Log.v(LOG_TAG, uriBuilder.toString());
        return uriBuilder.toString();
    }
}
