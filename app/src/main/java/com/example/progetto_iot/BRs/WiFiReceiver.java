package com.example.progetto_iot.BRs;

import android.Manifest;
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

    private WifiManager wifiManager = null;
    private WifiScanResult wifiScanResult = null;

    public WiFiReceiver(WifiManager wifiManager, WifiScanResult wiFiScanCompleted) {
        this.wifiManager = wifiManager;
        this.wifiScanResult = wiFiScanCompleted;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> wifiScan = this.wifiManager.getScanResults();

        String _s = "";
        _s += "<b>Numero reti wifi trovate: " + wifiScan.size() + "</b><br><br>";

        for (int i = 0; i < wifiScan.size(); i++) {
            _s += " <b>SSID: </b>" + wifiScan.get(i).SSID + "<br>";
            _s += " <b>MAC:  </b>" + wifiScan.get(i).BSSID + "<br>";
            _s += " <b>RSSI: </b>" + wifiScan.get(i).level + "<br><br>";
        }

        // Passare la stringa alla main activity
        this.wifiScanResult.onWifiScanCompleted(_s);

    }
}
