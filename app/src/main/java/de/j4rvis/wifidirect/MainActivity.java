package de.j4rvis.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements WifiP2pManager.PeerListListener,
        WifiP2pManager.DnsSdServiceResponseListener  {

    final static String TAG = MainActivity.class.getName();

    ListView mPeerListView;
    PeerAdapter mPeerAdapter;
    WifiController mController;
    Switch wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        wifiSwitch = (Switch) findViewById(R.id.wifi_switch);
        setSupportActionBar(toolbar);

        mPeerListView = (ListView) findViewById(R.id.peerListView);
        mPeerAdapter = new PeerAdapter(this);
        mPeerListView.setAdapter(mPeerAdapter);

        mController = new WifiController(this, this);
        mController.registerService();

        wifiSwitch.setChecked(false);

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mController.startDiscovering();
                } else {
                    mController.stopDiscovering();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(wifiSwitch.isChecked()){
//        mController.startDiscovering();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.stopDiscovering();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Log.d(TAG, "Peers available");
        if(peers.getDeviceList().size() != 0){
            mPeerAdapter.setList(peers);
            mPeerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDnsSdServiceAvailable(String instance, String type, WifiP2pDevice device) {

        device.deviceName = WifiController.buddies
                .containsKey(device.deviceAddress) ? WifiController.buddies
                .get(device.deviceAddress) : device.deviceName;

        mPeerAdapter.add(device);
        mPeerAdapter.notifyDataSetChanged();

    }
}
