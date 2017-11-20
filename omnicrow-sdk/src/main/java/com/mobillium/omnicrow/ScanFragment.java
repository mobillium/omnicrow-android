package com.mobillium.omnicrow;

/**
 * Created by oguzhandongul on 15/11/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mobillium.omnicrow.utils.PreferencesUtil;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


/**
 * Created by vitas on 8/12/15.
 */
public abstract class ScanFragment extends Fragment implements BeaconConsumer, RangeNotifier {

    final static String STATE_SCANNING = "STATE_SCANNING";

    protected Region mRegion;
    protected boolean isReadyForScan;
    protected boolean isScanning;
    protected BeaconManager mBeaconManager;
    protected boolean needContinueScan;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBeaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        mRegion = new Region(PreferencesUtil.getDefaultRegionName(getApplicationContext()), null, null, null);
        mBeaconManager.bind(this);
        mBeaconManager.addRangeNotifier(this);

        if (savedInstanceState != null) {
            needContinueScan = savedInstanceState.getBoolean(STATE_SCANNING);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBeaconManager.isBound(this)) {
            mBeaconManager.unbind(this);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(false);
    }

    @Override
    public void onPause() {
        //if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(PreferencesUtil.isBackgroundScan(getActivity()));
        super.onPause();
    }

    public void scanStartStopAction() {
        if (isScanning) {
            stopScan();
        } else {
            startScan();
        }
    }

    public void startScan() {
        try {
            if (isCanScan() & mBeaconManager.isBound(this)) {
                mBeaconManager.startRangingBeaconsInRegion(mRegion);
                isScanning = true;
//                if (getActivity() instanceof MainNavigationActivity) {
//                    ((MainNavigationActivity) getActivity()).swappingFloatingScanIcon(isScanning);
//                }
            }
        } catch (RemoteException e) {
            Log.d("OMNI", "Start scan beacon problem", e);
        }
    }

    public void stopScan() {
        try {
            if (mBeaconManager.isBound(this)) {
                mBeaconManager.stopRangingBeaconsInRegion(mRegion);
            }
            isScanning = false;
//            if (getActivity() instanceof MainNavigationActivity) {
//                ((MainNavigationActivity) getActivity()).swappingFloatingScanIcon(isScanning);
//            }
        } catch (RemoteException e) {
            Log.d("OMNI", "Stop scan beacon problem", e);
        }
    }

    public abstract void onCanScan();

    public abstract void updateBeaconList(final Collection<Beacon> beacons);

    public abstract void updateBeaconList(final Collection<Beacon> beacons, final Region region);

    protected boolean isCanScan() {
        return isReadyForScan;
    }

    @Override
    public void onBeaconServiceConnect() {
        isReadyForScan = true;
        isScanning = false;
        onCanScan();


        if (needContinueScan) {
            scanStartStopAction();
        }
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
        if (beacons != null) {
            if (beacons.size() > 0 && region != null && region.equals(mRegion)) {
                updateBeaconList(beacons);
            } else {
                updateBeaconList(beacons, region);
            }
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplication();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        Log.d("OMNI", "scan fragment unbound from beacon service");
        if (mBeaconManager.isBound(this)) {
            getActivity().unbindService(serviceConnection);
        }
        isReadyForScan = false;
        isScanning = false;
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        Log.d("OMNI", "scan fragment bound to beacon service");
        return getActivity().bindService(intent, serviceConnection, i);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SCANNING, isScanning);
    }
}
