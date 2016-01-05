package de.j4rvis.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by micha on 17.12.15.
 */
public class PeerAdapter extends BaseAdapter {

    private List<WifiP2pDevice> mDeviceList;
    private Context mContext;

    public PeerAdapter(Context context){
        mContext = context;
        mDeviceList = new LinkedList<>();
    }

    @Override
    public int getCount() {
        return mDeviceList.size();
    }

    @Override
    public WifiP2pDevice getItem(int position) {
        return mDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(WifiP2pDevice device){
        if(!mDeviceList.contains(device)){
            mDeviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public void setList(WifiP2pDeviceList devices){
        Iterator iterator = devices.getDeviceList().iterator();
        mDeviceList.clear();
        while(iterator.hasNext()){
            mDeviceList.add((WifiP2pDevice) iterator.next());
        }
//        Log.d("setList", ""+mDeviceList.size());
        notifyDataSetChanged();
    }

    public void clearList(){
        mDeviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiP2pDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_wifidirectdevice, parent, false);

        TextView address = (TextView) convertView.findViewById(R.id.textViewDeviceAddress);
        TextView name = (TextView) convertView.findViewById(R.id.textViewDeviceName);
        TextView status = (TextView) convertView.findViewById(R.id.textViewDeviceStatus);
        TextView owner = (TextView) convertView.findViewById(R.id.textViewDeviceIsGroupOwner);
        TextView discovery = (TextView) convertView.findViewById(R.id.textViewDeviceIsServiceDiscoverCapable);
        TextView primeType = (TextView) convertView.findViewById(R.id.textViewDevicePrimeType);
        TextView secondType = (TextView) convertView.findViewById(R.id.textViewDeviceSecType);
        address.setText(device.deviceAddress);
        name.setText(device.deviceName);
        String statusText = "";
        switch (device.status){
            case WifiP2pDevice.CONNECTED:
                statusText = "CONNECTED";
                break;
            case WifiP2pDevice.INVITED:
                statusText = "INVITED";
                break;
            case WifiP2pDevice.FAILED:
                statusText = "FAILED";
                break;
            case WifiP2pDevice.AVAILABLE:
                statusText = "AVAILABLE";
                break;
            case WifiP2pDevice.UNAVAILABLE:
                statusText = "UNAVAILABLE";
                break;
        }
        status.setText(statusText);
        owner.setText(""+device.isGroupOwner());
        discovery.setText(""+device.isServiceDiscoveryCapable());
        primeType.setText(device.primaryDeviceType);
        secondType.setText(device.secondaryDeviceType);
        return convertView;
    }
}
