package de.j4rvis.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by micha on 22.12.15.
 */
public class ServiceDevice {

    private WifiP2pDevice mDevice;
    private String mInstanceName;
    private String mRegistrationType;

    public ServiceDevice(WifiP2pDevice device, String instanceName, String registrationType) {
        mDevice = device;
        mInstanceName = instanceName;
        mRegistrationType = registrationType;
    }
}
