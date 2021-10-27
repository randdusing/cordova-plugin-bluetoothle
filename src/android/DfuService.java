package com.randdusing.bluetoothle;

import no.nordicsemi.android.dfu.DfuBaseService;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        /*
         * As a target activity the NotificationActivity is returned, not the MainActivity. This is because
         * the notification must create a new task:
         *
         * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         *
         * when you press it. You can use NotificationActivity to check whether the new activity
         * is a root activity (that means no other activity was open earlier) or that some
         * other activity is already open. In the latter case the NotificationActivity will just be
         * closed. The system will restore the previous activity. However, if the application has been
         * closed during upload and you click the notification, a NotificationActivity will
         * be launched as a root activity. It will create and start the main activity and
         * terminate itself.
         *
         * This method may be used to restore the target activity in case the application
         * was closed or is open. It may also be used to recreate an activity history using
         * startActivities(...).
         */
        return NotificationActivity.class;
    }

    @Override
    protected boolean isDebug() {
        // Here return true if you want the service to print more logs in LogCat.
        // Library's BuildConfig in current version of Android Studio is always set to DEBUG=false, so
        // make sure you return true or your.app.BuildConfig.DEBUG here.
        // return BuildConfig.DEBUG;
        return true;
    }

    @Override
    protected void updateForegroundNotification(@NonNull final NotificationCompat.Builder builder) {
        // Customize the foreground service notification here.
        Log.d("BLE", "update foreground notification");
    }
}