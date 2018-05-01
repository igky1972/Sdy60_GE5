package com.sdy60ge5.teamge5_a;

import java.util.HashMap;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
// import com.ippokratis.mapmaker2.R;
import com.sdy60ge5.teamge5_a.R;

public class GoogleAnalyticsApp extends Application{

    private static final String PROPERTY_ID = "UA-61538623-2";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker που χρησιμοποιείται μονο για αυτή την εφαρμογή
        GLOBAL_TRACKER, // Tracker που χρησιμοποιείται απο ολες τις εφαρμογές μιας εταιείας. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker που χρησιμοποιείται απο ολες τις συναλλαγές ηλεκτρονικου εμπορίου μιας εταιρείας.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public GoogleAnalyticsApp() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

}
