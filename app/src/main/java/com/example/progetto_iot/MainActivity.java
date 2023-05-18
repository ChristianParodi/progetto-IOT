package com.example.progetto_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.progetto_iot.BRs.WiFiReceiver;
import com.example.progetto_iot.interfaces.WifiScanResult;

public class MainActivity extends AppCompatActivity {
    private Button btnMisura;
    private TextView tvResult;

    private WifiManager wifiManager = null;
    private WiFiReceiver wiFiReceiver = null;
    private WifiScanResult wiFiScanResult = null;
    private SignalStrengthTracker tracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = new SignalStrengthTracker(5);
        tracker.startCalculatingAverageSignalStrength(500);
        btnMisura = findViewById(R.id.btnMisura);
        tvResult = findViewById(R.id.tvResult);
        btnMisura.setOnClickListener(v -> {
            tvResult.setText(tracker.getAverageSignalStrength());
        });

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
    }
}