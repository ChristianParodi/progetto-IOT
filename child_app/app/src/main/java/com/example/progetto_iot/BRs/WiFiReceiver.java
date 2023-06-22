package com.example.progetto_iot.BRs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.progetto_iot.interfaces.WifiScanResult;

import java.util.List;

public class WiFiReceiver extends BroadcastReceiver {
    private final String TAG = "WiFiReceiver";
    private WifiManager wifiManager;
    private WifiScanResult wifiScanResult;
    private static final String SSID = "iPhone di Federico";
    private static final String BSSID = "2a:c2:1f:d7:df:e0";

    public WiFiReceiver(WifiManager wifiManager, WifiScanResult wiFiScanCompleted) {
        this.wifiManager = wifiManager;
        this.wifiScanResult = wiFiScanCompleted;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        @SuppressLint("MissingPermission")
        List<ScanResult> wifiScan = this.wifiManager.getScanResults();

        String _s = "";
        _s += "<b>Numero reti wifi trovate: " + wifiScan.size() + "</b><br><br>";

        for (int i = 0; i < wifiScan.size(); i++) {
            if(wifiScan.get(i).SSID.equals(SSID)){
                _s += " <b>SSID: </b>" + wifiScan.get(i).SSID + "<br>";
                _s += " <b>MAC:  </b>" + wifiScan.get(i).BSSID + "<br>";
                _s += " <b>RSSI: </b>" + wifiScan.get(i).level + "<br><br>";
                wifiScanResult.onWifiScanCompleted(wifiScan.get(i));
                Log.i(TAG, _s);
            }
        }

        // Passare la stringa alla main activity
        this.wifiScanResult.onWifiScanCompleted(_s);
    }
}
