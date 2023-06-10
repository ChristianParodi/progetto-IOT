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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.progetto_iot.BRs.WiFiReceiver;
import com.example.progetto_iot.R;
import com.example.progetto_iot.interfaces.WifiScanResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForegroundScanService extends Service implements WifiScanResult {
    private static final String TAG = "BackgroundScanService";
    private static final String NOTIFICATION_CHANNEL_ID = "WiFiScannerId";
    public static boolean isRunning = false;
    private WifiManager wifiManager;
    private WiFiReceiver wifiReceiver;
    private List<Integer> signalSamples; // Lista delle potenze campionate
    private int windowSize; // Massimo numero di campioni che teniamo

    private static final String IP = "192.168.151.187";
    private static final int childId = 1;
    private static final String nome = "Gino";
    private static final String cognome = "Paoli";

    enum State {
        OUT,
        IN
    };

    private State state;
    private State previous;
    private boolean currentRecordType;
    private static int stateCount = 0;

    @Override
    public void onCreate() {
        Log.i(TAG, TAG + ": onCreate");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WiFiReceiver(wifiManager, this);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        state = State.OUT;
        previous = State.OUT;
        signalSamples = new ArrayList<>();
        windowSize = 10;

        // prendiamo il tipo dell'ultimo record in modo da poter
        // decidere lo stato corrente
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, "http://" + IP + ":3000/select/" + childId, null,
                response -> {
                    try {
                        if(response.length() == 0)
                            currentRecordType = false;
                        else {

                            JSONObject last = response.getJSONObject(0);
                            Log.i(TAG, last.toString());
                            String value = last.getString("tipo");
                            currentRecordType = value.equalsIgnoreCase("ingresso");
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.e(TAG, error.toString());
                }
        );
        requestQueue.add(arrayRequest);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager.startScan();
        isRunning = true;
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
        Log.i(TAG, "ForegroundService: onWifiScanCompleted");
        int level = scanResult.level;
        signalSamples.add(level);

        if(signalSamples.size() > windowSize) // Se il segnale aggiunto eccede, tolgo il piu' vecchio
            for(int i = 0; i < windowSize/3; i++)
                signalSamples.remove(i);
        else if(signalSamples.size() == windowSize){
            double averageDistance = getAverageSignalStrength();
            int soglia = -45;

            previous = state;
            state = averageDistance > soglia ? State.IN : State.OUT;

            if(state != previous)
                stateCount = 0;
            else
                stateCount++;

            for(int i = 0; i < windowSize/3; i++)
                signalSamples.remove(i);
        }

        Log.i(TAG, "" + stateCount);
        // inserimento in db
        if(stateCount == 3){
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // Dobbiamo controllare che il record precedente sia un ingresso,
            // in modo tale da non inserire ogni volta l'ingresso dopo essere gia' entrati
            Log.i(TAG, "currentRecordType: " + currentRecordType);

            if(currentRecordType && state == State.OUT || !currentRecordType && state == State.IN){
                String url = "http://" + IP + ":3000/insert";
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("child_id", childId);
                    jsonBody.put("nome", nome);
                    jsonBody.put("cognome", cognome);

                    if(state == State.IN){
                        jsonBody.put("tipo", "Ingresso");
                        currentRecordType = true;
                    } else {
                        jsonBody.put("tipo", "Uscita");
                        currentRecordType = false;
                    }

                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = dateTime.format(formatter);
                    jsonBody.put("data", formattedDateTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, jsonBody.toString());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                        response -> {
                            try {
                                if(response.getBoolean("status")) {
                                    Notify("SchoolAlert", "INSERITO RECORD");
                                } else Notify("SchoolAlert", "PROBLEMA CON L'API");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            Notify("SchoolAlert", "ERRORE: " + error.toString());
                            Log.e(TAG, error.toString());
                        });
                requestQueue.add(jsonObjectRequest);
            }
            stateCount = 0;
        }

        wifiManager.startScan();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private double getAverageSignalStrength(){
        if(signalSamples.size() == 0)
            return 0;

        double sum = 0;
        double p = 0;
        for(int i = 0; i<signalSamples.size(); i++){
            p += (1.0/(signalSamples.size() - i));
            sum += signalSamples.get(i) * (1.0/(signalSamples.size() - i)); // media pesata
        }

        return new BigDecimal(sum / p).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

}
