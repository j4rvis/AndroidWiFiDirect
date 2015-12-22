package de.j4rvis.wifidirect;

import android.annotation.TargetApi;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by micha on 17.12.15.
 */
public class WifiDnsSdTxtRecordListener implements WifiP2pManager.DnsSdTxtRecordListener {

    private static final String TAG = WifiDnsSdTxtRecordListener.class.getName();
    HashMap<String, String> mBuddies;

    public WifiDnsSdTxtRecordListener(HashMap<String, String> buddies) {
        mBuddies = buddies;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
        mBuddies.put(srcDevice.deviceAddress, txtRecordMap.get("buddyname"));
    }
}
