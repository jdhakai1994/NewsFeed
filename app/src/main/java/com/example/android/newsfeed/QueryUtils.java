package com.example.android.newsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

import static android.R.attr.thumbnail;

/**
 * Created by Jayabrata Dhakai on 11/26/2016.
 */

/**
 * Utility Class containing helper methods to fetch data from the internet
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Private constructor to make sure that an object cannot be created
     */
    private QueryUtils() {
    }

    /**
     * Creates an URL object, make HTTPRequest, parses the response to a list of {@link News} objects
     * @param requestURL the URL in String format
     * @return the list of {@link News} objects
     */
    public static List<News> fetchNewsData(String requestURL){

        URL url = createURL(requestURL);
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.v(LOG_TAG, "IO Exception occurred in fetchNewsData()", e);
        }
        return extractResultsFromJson(jsonResponse);
    }


    /**
     * Helper method to convert requestURL from String to URL
     * @param requestURL the url in String format
     * @return the url as {@link URL}
     */
    private static URL createURL(String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Exception occurred while casting String to URL in createURL()", e);
        }
        return url;
    }

    /**
     * Helper method to make the HTTP request and returns the jsonResponse obtained in String format
     * @param url is the url as {@link URL} object
     * @return the response of the url
     */
    private static String makeHTTPRequest(URL url) throws IOException {
        // If the url object is null no point further executing
        if(url == null)
            return null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
                Log.v(LOG_TAG, "The response code is not 200, it is " + String.valueOf(urlConnection.getResponseCode()));
        } catch (IOException e) {
            Log.v(LOG_TAG, "Problem retrieving the jsonResponse from the server", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Helper method to convert the InputStream to String format
     * @param inputStream the response obtained as {@link InputStream} object
     * @return the string format of the response
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        // If the inputStream object is null no point further executing
        if(inputStream == null)
            return null;
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }

        return output.toString();
    }

    private static List<News> extractResultsFromJson(String jsonResponse) {

        List<News> newsList = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(jsonResponse);
            JSONObject responseObject = rootJsonObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            for(int i=0; i<resultsArray.length(); i++){
                JSONObject currentResult = (JSONObject) resultsArray.get(i);

                String sectionName = currentResult.getString("sectionName");
                String webPublicationDate = currentResult.getString("webPublicationDate");
                String webTitle = currentResult.getString("webTitle");
                String webUrl = currentResult.getString("webUrl");
                Bitmap bitmap = null;

                if(currentResult.has("fields")){
                    JSONObject fields = currentResult.getJSONObject("fields");
                    String thumbnail = fields.getString("thumbnail");
                    URL thumbnailUrl = getThumbnailUrl(thumbnail);
                    bitmap = getBitmapFromURL(thumbnailUrl);
                }

                News newNews = new News(sectionName, webPublicationDate, webTitle, webUrl, bitmap);
                newsList.add(newNews);
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Error parsing the JSON Response", e);
        }

        return newsList;
    }

    /**
     *
     * @param thumbnailUrl
     * @return
     */
    private static Bitmap getBitmapFromURL(URL thumbnailUrl) {
        Bitmap imageBitmap = null;
        try {
            imageBitmap = BitmapFactory.decodeStream(thumbnailUrl.openConnection().getInputStream());
        } catch (IOException e) {
            Log.v(LOG_TAG, "Problem retrieving the image from the server", e);
        }
        return imageBitmap;
    }

    /**
     *
     * @param thumbnail
     * @return
     */
    private static URL getThumbnailUrl(String thumbnail) {

        // change the URL to get the smaller image
        URL newUrl = null;
        try {
            newUrl = new URL(thumbnail.replace("/500.jpg", "/140.jpg"));
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Exception occurred while casting String to URL in getThumbnailUrl()", e);
        }
        return newUrl;
    }
}
