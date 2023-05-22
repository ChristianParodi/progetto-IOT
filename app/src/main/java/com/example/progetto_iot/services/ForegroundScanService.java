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

import java.util.ArrayList;
import java.util.List;

public class ForegroundScanService extends Service implements WifiScanResult {
    private static final String TAG = "BackgroundScanService";
    private static final String NOTIFICATION_CHANNEL_ID = "WiFiScannerId";
    public static boolean isRunning = false;
    private WifiManager wifiManager;
    private WiFiReceiver wifiReceiver;
    private List<Integer> signalSamples; // Lista delle potenze campionate
    private int windowSize; // Massimo numero di campioni che teniamo

    @Override
    public void onCreate() {
        Log.i(TAG, TAG + ": onCreate");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WiFiReceiver(wifiManager, this);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        signalSamples = new ArrayList<>();
        windowSize = 30;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager.startScan();
        isRunning = true;
        Notify("SchoolAlert", "fuori dalla scuola. distanza media = " + getAverageSignalStrength());
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
        signalSamples.add(level);
        if(signalSamples.size() > windowSize) // Se il segnale aggiunto eccede, tolgo il piu' vecchio
            signalSamples.remove(0);

        int averageDistance = getAverageSignalStrength();
        if(level > averageDistance){
            Notify("SchoolAlert", "Dentro la scuola. distanza media = " + averageDistance);
        } else
            Notify("SchoolAlert", "Vicino alla scuola. distanza media = " + averageDistance);
    }

    private int getAverageSignalStrength(){
        if(signalSamples.size() == 0)
            return 0;

        int sum = 0;
        for(int elem : signalSamples)
            sum += elem;
        return sum / signalSamples.size();
    }
}
