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
        implements WifiP2pManager.PeerListListener {

    final static String TAG = MainActivity.class.getName();

    ListView mPeerListView;
    PeerAdapter mPeerAdapter;
    WifiController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPeerListView = (ListView) findViewById(R.id.peerListView);
        mPeerAdapter = new PeerAdapter(this);
        mPeerListView.setAdapter(mPeerAdapter);

        mController = new WifiController(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.startDisccovering();
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
}
