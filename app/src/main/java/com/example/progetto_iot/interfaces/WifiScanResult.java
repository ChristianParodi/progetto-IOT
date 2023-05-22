package com.example.progetto_iot.interfaces;

import android.net.wifi.ScanResult;

public interface WifiScanResult {
    void onWifiScanCompleted(String wifiResHTML);
    void onWifiScanCompleted(ScanResult scanResult);
}
