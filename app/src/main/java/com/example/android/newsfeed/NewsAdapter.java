package com.example.android.newsfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.android.newsfeed.R.id.sectionName;

/**
 * Created by Jayabrata Dhakai on 11/26/2016.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        News currentNews = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_news2, parent, false);
        }

        // Lookup view for data population
        TextView sectionNameTextView = (TextView) convertView.findViewById(sectionName);
        TextView webTitleTextView = (TextView) convertView.findViewById(R.id.webTitle);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.time);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);

        // Extract individual fields from the object
        String sectionName = currentNews.getSectionName();
        String webTitle = currentNews.getWebTitle();
        String webPublicationDate = currentNews.getWebPublicationDate();
        Bitmap image = currentNews.getBitmapImage();
        String date = getDate(webPublicationDate);
        String time = getTime(webPublicationDate);


        // Populate the data into the template view using the data object
        sectionNameTextView.setText(sectionName);
        webTitleTextView.setText(webTitle);
        timeTextView.setText(time);
        dateTextView.setText(date);
        if(image != null)
            imageView.setImageBitmap(image);

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Helper method to convert the date to required time format
     * @param webPublicationDate contains the date as String
     * @return published date as String
     */
    private String getDate(String webPublicationDate) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpledateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = simpledateformat.parse(webPublicationDate);
        } catch (ParseException e) {
            Log.v(LOG_TAG, "Exception while parsing date in getDate()", e);
        }
        DateFormat requiredDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return requiredDateFormat.format(date);
    }

    /**
     * Helper method to convert the date to required time format
     * @param webPublicationDate contains the date as String
     * @return published time as String
     */
    private String getTime(String webPublicationDate) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpledateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = simpledateformat.parse(webPublicationDate);
        } catch (ParseException e) {
            Log.v(LOG_TAG, "Exception while parsing date in getDate()", e);
        }
        DateFormat requiredTimeFormat = new SimpleDateFormat("HH:mm");
        return requiredTimeFormat.format(date);
    }

}
