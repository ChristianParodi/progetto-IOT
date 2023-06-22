package com.example.progetto_iot.interfaces;

import android.net.wifi.ScanResult;

public interface WifiScanResult {
    void onWifiScanCompleted(ScanResult scanResult);
}
