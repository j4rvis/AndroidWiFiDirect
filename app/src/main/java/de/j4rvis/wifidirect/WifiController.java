package de.j4rvis.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by micha on 22.12.15.
 */
public class WifiController implements
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
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    final static HashMap<String, String> buddies = new HashMap<String, String>();
    WifiP2pManager.DnsSdTxtRecordListener mTxtListener;
    WifiP2pManager.DnsSdServiceResponseListener mServListener;
    private WifiP2pDnsSdServiceRequest serviceRequest;


    public WifiController(Context context, MainActivity activity) {
        mContext = context;
        mActivity = activity;
        mState = INITIALIZED;

        mManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(mContext, mContext.getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, mActivity);
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

//        mManager.setDnsSdResponseListeners(mChannel, this, this);
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
            mContext.registerReceiver(mReceiver, mIntentFilter);
            mManager.discoverPeers(mChannel, new WifiActionListener("Discover peers"));
        }
        mState = DISCOVERING;
    }

    public void stopDiscovering(){
        if(mServiceIsRegistered){
            mManager.removeServiceRequest(
                    mChannel, serviceRequest, new WifiActionListener("Remove service request"));
        } else {
            mManager.stopPeerDiscovery(mChannel, new WifiActionListener("Stop peer discovery"));
        }
        mState = STOPPED;
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
}
