package com.example.progetto_iot.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.progetto_iot.BRs.WiFiReceiver;
import com.example.progetto_iot.R;
import com.example.progetto_iot.interfaces.WifiScanResult;

public class ForegroundScanService extends Service implements WifiScanResult {
    private static final String TAG = "BackgroundScanService";
    private static final String NOTIFICATION_CHANNEL_ID = "WiFiScannerId";
    public static boolean isRunning = false;
    private WifiManager wifiManager;
    private WiFiReceiver wifiReceiver;

    @Override
    public void onCreate() {
        Log.i(TAG, TAG + ": onCreate");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WiFiReceiver(wifiManager, this);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager.startScan();
        isRunning = true;
        Notify("SchoolAlert", "fuori dalla scuola");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiReceiver);
        isRunning = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void Notify(String title, String text){
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentText(text)
                .setContentTitle(title)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        startForeground(1001, notificationBuilder.build());
    }

    @Override
    public void onWifiScanCompleted(String wifiResHTML) {

    }

    @Override
    public void onWifiScanCompleted(ScanResult scanResult) {
        int level = scanResult.level;
        int averageDistance = -69;
        if(level > averageDistance){
            Notify("SchoolAlert", "Dentro la scuola");
        } else
            Notify("SchoolAlert", "Vicino alla scuola");
    }
}
