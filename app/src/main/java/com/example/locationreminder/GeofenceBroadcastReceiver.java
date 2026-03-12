package com.example.locationreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        boolean isTimeBased = intent.getBooleanExtra("isTimeBased", false);

        // 1. Handle TIME-BASED Trigger
        if ("com.example.locationreminder.ACTION_TIME_ALARM".equals(action) || isTimeBased) {
            String title = intent.getStringExtra("title");
            if (title == null) title = "Scheduled Reminder";

            Log.d(TAG, "Time Trigger: " + title);
            NotificationHelper.showNotification(context, "Time Reminder!", "It's time for: " + title);
            return;
        }

        // 2. Handle LOCATION-BASED Trigger
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            return;
        }

        if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null) {
                for (Geofence geofence : triggeringGeofences) {
                    String title = geofence.getRequestId();
                    Log.d(TAG, "Location Trigger: " + title);
                    NotificationHelper.showNotification(context, "Location Reached!", "You are near: " + title);
                }
            }
        }
    }
}