package com.appsomniac.newshour.sync;

/**
 * Created by Saurabh on 27-08-2017.
 */


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NewsHourSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static NewsHourSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new NewsHourSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}