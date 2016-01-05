package de.j4rvis.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by micha on 22.12.15.
 */
public class WifiController extends BroadcastReceiver implements
        WifiP2pManager.DnsSdTxtRecordListener {

    public final static int DISCOVERING = 0;
    public final static int STOPPED = 1;
    public final static int INITIALIZED = 2;

    private MainActivity mActivity;
    private int mState;
    private Context mContext;
    private boolean mServiceIsRegistered = false;
    private final static String TAG = MainActivity.class.getName();
    private final static int SERVER_PORT = 4545;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    IntentFilter mIntentFilter;

    final static HashMap<String, String> buddies = new HashMap<>();
    private WifiP2pDnsSdServiceRequest serviceRequest;


    public WifiController(Context context, MainActivity activity) {
        mContext = context;
        mActivity = activity;
        mState = INITIALIZED;

        mManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mContext, mContext.getMainLooper(), null);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void registerService(){

        Map<String, String> record = new HashMap();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "User" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_shark", "_presence._tcp", record);

        mManager.addLocalService(mChannel, serviceInfo, new WifiActionListener("Add Local Service"));

        mManager.setDnsSdResponseListeners(mChannel, mActivity, this);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();

        mServiceIsRegistered = true;
        Log.d(TAG, "Registration finished.");
    }

    public void startDiscovering(){
        if(mServiceIsRegistered){
            mManager.addServiceRequest(
                    mChannel, serviceRequest, new WifiActionListener("Add service request"));
            mManager.discoverServices(mChannel, new WifiActionListener("Discover services"));
        } else {
            mContext.registerReceiver(this, mIntentFilter);
            mManager.discoverPeers(mChannel, new WifiActionListener("Discover peers"));
        }
        mState = DISCOVERING;
    }

    public void stopDiscovering(){
        if(isDiscovering()){
            if(mServiceIsRegistered){
                mManager.removeServiceRequest(
                        mChannel, serviceRequest, new WifiActionListener("Remove service request"));
            } else {
                mContext.unregisterReceiver(this);
                mManager.stopPeerDiscovery(mChannel, new WifiActionListener("Stop peer discovery"));
            }
            mState = STOPPED;
        }
    }

    public String getMACAddress() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            String address = br.readLine();
            br.close();
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isDiscovering(){
        if(mState == 0)
            return true;
        else
            return false;
    }

    public int getState() {
        return mState;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
        buddies.put(srcDevice.deviceAddress, txtRecordMap.get("buddyname").toString());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, mActivity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
