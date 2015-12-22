package de.j4rvis.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements WifiP2pManager.PeerListListener, Runnable,
        WifiP2pManager.DnsSdTxtRecordListener, WifiP2pManager.DnsSdServiceResponseListener {

    final static String TAG = MainActivity.class.getName();
    final static int SERVER_PORT = 4545;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiActionListener mActionListener;
    ListView mPeerListView;
    ListView mServiceListView;
    ServiceAdapter mServiceAdapter;
    WifiP2pDeviceList mPeers;
    Handler mHandler;

    final HashMap<String, String> buddies = new HashMap<String, String>();
    WifiP2pManager.DnsSdTxtRecordListener mTxtListener;
    WifiP2pManager.DnsSdServiceResponseListener mServListener;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(primaryDeviceTypeView view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        mPeerListView = (ListView) findViewById(R.id.peerListView);
//        mPeerAdapter = new PeerAdapter(this);
//        mPeerListView.setAdapter(mPeerAdapter);

        mServiceListView = (ListView) findViewById(R.id.peerListView);
        mServiceAdapter = new ServiceAdapter(this);
        mServiceListView.setAdapter(mServiceAdapter);

        mActionListener = new WifiActionListener(this);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        startRegistration();

//        mHandler = new Handler();
//        mHandler.post(this);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
//        mManager.discoverPeers(mChannel, mActionListener);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
//        Log.d("PEERS", "Peers available");
//        if(peers.getDeviceList().size() != 0 && !peers.equals(mPeers)){
//            mPeers = peers;
//        }
    }

    @Override
    public void run() {
//        if(mPeers!=null){
//            mPeerAdapter.setList(mPeers);
//        }
        mHandler.postDelayed(this, 1000);
    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "User" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_shark", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiActionListener(this));
        Log.d(TAG, "Registration finished.");
        startDiscovery();
    }

    private void startDiscovery(){
        mManager.setDnsSdResponseListeners(mChannel, this, this);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, serviceRequest, new WifiActionListener(this));
        mManager.discoverServices(mChannel, new WifiActionListener(this));
        Log.d(TAG, "Discovery started.");
    }

    @Override
    public void onDnsSdTxtRecordAvailable(
            String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {

        Log.d(TAG, "DnsSdTxtRecord available -" + txtRecordMap.toString());
        buddies.put(srcDevice.deviceAddress, txtRecordMap.get("buddyname"));
    }

    @Override
    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
        Log.d(TAG, "onServiceAvailable");
        // Update the device name with the human-friendly version from
        // the DnsTxtRecord, assuming one arrived.
        srcDevice.deviceName = buddies
                .containsKey(srcDevice.deviceAddress) ? buddies
                .get(srcDevice.deviceAddress) : srcDevice.deviceName;
        mServiceAdapter.add(new ServiceDevice(srcDevice,instanceName, registrationType));

//        Log.d(TAG, instanceName+"_"+registrationType+"_"+srcDevice.toString());
        // Add to the custom adapter defined specifically for showing
        // wifi devices.
//        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
//                .findFragmentById(R.id.frag_peerlist);
//        WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
//                .getListAdapter());
//
//        adapter.add(resourceType);
//        adapter.notifyDataSetChanged();
    }
}
