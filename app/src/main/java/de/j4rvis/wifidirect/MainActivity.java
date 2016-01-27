package de.j4rvis.wifidirect;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements
//        WifiP2pManager.PeerListListener,
        WifiP2pManager.DnsSdServiceResponseListener,
        WifiP2pManager.DnsSdTxtRecordListener {

    final static String TAG = MainActivity.class.getName();

    ListView mPeerListView;
    PeerAdapter mPeerAdapter;
    WifiController mController;
    Switch wifiSwitch;
    Context mContext = this;
    CompoundButton.OnCheckedChangeListener mSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                Toast.makeText(mContext, "Start discovering...", Toast.LENGTH_SHORT).show();
                mController.startDiscovering();
            } else {
                mPeerAdapter.clearList();
                Toast.makeText(mContext, "Stop discovering.", Toast.LENGTH_SHORT).show();
                mController.stopDiscovering();
            }
        }
    };

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

        TextView deviceAddress = (TextView) findViewById(R.id.deviceAddress);
        deviceAddress.setText(mController.getMACAddress());

        wifiSwitch.setChecked(false);
        wifiSwitch.setOnCheckedChangeListener(mSwitchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.stopDiscovering();
    }

//    @Override
//    public void onPeersAvailable(WifiP2pDeviceList peers) {
//
//        Toast.makeText(mContext, peers.getDeviceList().size() + " Peer(s) found.", Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "Peers available");
//        if(peers.getDeviceList().size() != 0){
//            mPeerAdapter.setList(peers);
//            mPeerAdapter.notifyDataSetChanged();
//        }
//    }
//
    @Override
    public void onDnsSdServiceAvailable(String instance, String type, WifiP2pDevice device) {

        device.deviceName = WifiController.buddies
                .containsKey(device.deviceAddress) ? WifiController.buddies
                .get(device.deviceAddress) : device.deviceName;

        mPeerAdapter.add(device);
        mPeerAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, fullDomainName + "\n" + txtRecordMap.toString() + "\n" + srcDevice.toString());
        mPeerAdapter.add(srcDevice);
    }
}
