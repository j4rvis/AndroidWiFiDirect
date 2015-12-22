package de.j4rvis.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by micha on 17.12.15.
 */
public class WifiActionListener implements WifiP2pManager.ActionListener {

    private static String TAG = null;

    public WifiActionListener(Context context) {
        TAG = context.getClass().getName();
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "SUCCESS");
    }

    @Override
    public void onFailure(int reason) {
        Log.d(TAG, "FAILURE");
        if(reason == WifiP2pManager.P2P_UNSUPPORTED){
            Log.d(TAG, "Wi-Fi P2P isn't supported on the device running the app.");
        } else if(reason == WifiP2pManager.BUSY){
            Log.d(TAG, "The system is to busy to process the request.");
        } else if(reason == WifiP2pManager.ERROR){
            Log.d(TAG, "The operation failed due to an internal error.");
        }
    }
}
