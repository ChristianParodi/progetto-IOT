package com.example.progetto_iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progetto_iot.BRs.WiFiReceiver;
import com.example.progetto_iot.interfaces.WifiScanResult;
import com.example.progetto_iot.services.ForegroundScanService;

public class MainActivity extends AppCompatActivity implements WifiScanResult {
    private Button btnMisura;
    private TextView tvResult;
    private WifiManager wifiManager;
    private WiFiReceiver wifiReceiver;
    private SignalStrengthTracker tracker;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }

        tracker = new SignalStrengthTracker(5);
        tracker.startCalculatingAverageSignalStrength(500);
        btnMisura = findViewById(R.id.btnMisura);
        tvResult = findViewById(R.id.tvResult);
        btnMisura.setOnClickListener(v -> {
            if(!wifiManager.isWifiEnabled()){
                Toast.makeText(getApplicationContext(), "Turning on the wifi...", Toast.LENGTH_LONG);
                wifiManager.setWifiEnabled(true);
            }

            tvResult.setText("Scansione in corso...");
            if(!ForegroundScanService.isRunning)
                startService(new Intent(this, ForegroundScanService.class));
        });

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WiFiReceiver(wifiManager, this);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

//        SignalStrengthTracker signalStrengthTracker = new SignalStrengthTracker(5); // Imposta la dimensione della finestra
//        signalStrengthTracker.startCalculatingAverageSignalStrength(1000); // Avvia il calcolo ogni 1000 millisecondi (1 secondo)
//        // Aggiungi un nuovo campione di potenza del segnale ogni volta che ottieni un valore
//        int signalStrength = wiFiReceiver.getRssi(); // Ottieni il valore di potenza del segnale
//        signalStrengthTracker.addSample(signalStrength);
    }

    @Override
    protected void onStop() {
        super.onStop();
        tracker.stopCalculatingAverageSignalStrength();
        unregisterReceiver(wifiReceiver);
        stopService(new Intent(this, ForegroundScanService.class));
    }

    @Override
    public void onWifiScanCompleted(String wifiResHTML) {
        tvResult.setText(Html.fromHtml(wifiResHTML));
    }

    @Override
    public void onWifiScanCompleted(ScanResult scanResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            // Se non ho ottenuto il permesso, visualizzo un errore
            if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                tvResult.setText("I permessi di localizzazione sono necessari per il corretto utilizzo dell'applicazione, per favore concedi i permessi");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}