package com.example.android.newsfeed;

import android.graphics.Bitmap;

/**
 * Created by Jayabrata Dhakai on 11/26/2016.
 */

public class News {
    private String mSectionName;
    private String mWebTitle;
    private String mWebPublicationDate;
    private String mWebUrl;
    private Bitmap mBitmapImage;

    /**
     * Create a new {@link News} object
     * @param sectionName
     * @param webPublicationDate
     * @param webTitle
     * @param webUrl
     */
    public News(String sectionName, String webPublicationDate, String webTitle, String webUrl, Bitmap bitmapImage) {
        this.mSectionName = sectionName;
        this.mWebTitle = webTitle;
        this.mWebPublicationDate = webPublicationDate;
        this.mWebUrl = webUrl;
        this.mBitmapImage = bitmapImage;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public Bitmap getBitmapImage() {
        return mBitmapImage;
    }

    @Override
    public String toString() {
        return "News{" +
                "mSectionName='" + mSectionName + '\'' +
                ", mWebTitle='" + mWebTitle + '\'' +
                ", mWebPublicationDate='" + mWebPublicationDate + '\'' +
                ", mWebUrl='" + mWebUrl + '\'' +
                '}';
    }
}
