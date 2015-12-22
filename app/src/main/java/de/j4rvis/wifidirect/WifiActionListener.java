package de.j4rvis.wifidirect;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by micha on 17.12.15.
 */
public class WifiActionListener implements WifiP2pManager.ActionListener {

    private String mMessage;

    public WifiActionListener(String message) {
        mMessage = message;
    }

    @Override
    public void onSuccess() {
        Log.d("ACTIONLISTENER", mMessage + " initiated.");
    }

    @Override
    public void onFailure(int reason) {
        Log.d("ACTIONLISTENER", mMessage + " failed.");
        if(reason == WifiP2pManager.P2P_UNSUPPORTED){
            Log.d("ACTIONLISTENER", "Wi-Fi P2P isn't supported on the device running the app.");
        } else if(reason == WifiP2pManager.BUSY){
            Log.d("ACTIONLISTENER", "The system is to busy to process the request.");
        } else if(reason == WifiP2pManager.ERROR){
            Log.d("ACTIONLISTENER", "The operation failed due to an internal error.");
        }
    }
}
