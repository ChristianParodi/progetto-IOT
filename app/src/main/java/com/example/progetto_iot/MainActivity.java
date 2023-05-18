package com.example.progetto_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignalStrengthTracker signalStrengthTracker = new SignalStrengthTracker(5); // Imposta la dimensione della finestra
        signalStrengthTracker.startCalculatingAverageSignalStrength(1000); // Avvia il calcolo ogni 1000 millisecondi (1 secondo)
        // Aggiungi un nuovo campione di potenza del segnale ogni volta che ottieni un valore
        int signalStrength = wifiInfo.getRssi(); // Ottieni il valore di potenza del segnale
        signalStrengthTracker.addSample(signalStrength);
    }
}