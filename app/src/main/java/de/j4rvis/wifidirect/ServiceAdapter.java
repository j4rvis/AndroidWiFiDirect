package de.j4rvis.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by micha on 22.12.15.
 */
public class ServiceAdapter extends BaseAdapter {

    private List<ServiceDevice> mServiceList;
    private Context mContext;

    public ServiceAdapter(Context context) {
        mContext = context;
        mServiceList = new LinkedList<>();
    }

    @Override
    public int getCount() {
        return mServiceList.size();
    }

    @Override
    public ServiceDevice getItem(int position) {
        return mServiceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(ServiceDevice device){
        if(!mServiceList.contains(device)){
            mServiceList.add(device);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_servicedevice, parent, false);

        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView service = (TextView) convertView.findViewById(R.id.service);
        TextView buddy = (TextView) convertView.findViewById(R.id.buddy);
        address.setText(device.getDevice().deviceAddress);
        service.setText(device.getRegistrationType());
        buddy.setText(device.getDevice().deviceName);
        return convertView;
    }
}
