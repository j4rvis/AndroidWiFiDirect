package de.j4rvis.wifidirect;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
