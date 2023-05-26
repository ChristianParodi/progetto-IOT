package com.example.progetto_iot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SignalStrengthTracker {
    private int windowSize;
    private List<Integer> signalStrengthSamples;

    private Timer timer;
    private TimerTask timerTask;
    private int averageSignalStrength;

    public SignalStrengthTracker(int windowSize) {
        this.windowSize = windowSize;
        signalStrengthSamples = new ArrayList<>(Arrays.asList(1));
    }

    public void startCalculatingAverageSignalStrength(long intervalMillis) {
        stopCalculatingAverageSignalStrength(); // Assicurati che il calcolo periodico sia fermato prima di avviarlo 

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                averageSignalStrength = getAverageSignalStrength();
                // Usa la media mobile aggiornata come necessario 
            }
        };

        timer.schedule(timerTask, 0, intervalMillis);
    }

    public void stopCalculatingAverageSignalStrength() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public void addSample(int signalStrength) {
        signalStrengthSamples.add(signalStrength);
        if (signalStrengthSamples.size() > windowSize) {
            signalStrengthSamples.remove(0);
        }
    }

    public int getAverageSignalStrength() {
        int sum = 0;
        for (int sample : signalStrengthSamples) {
            sum += sample;
        }
        return sum / signalStrengthSamples.size();
    }
}