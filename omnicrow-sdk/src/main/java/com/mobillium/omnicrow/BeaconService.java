package com.mobillium.omnicrow;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mobillium.omnicrow.utils.PreferencesUtil;
import com.mobillium.omnicrow.webservice.models.BeaconModel;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconService extends Service implements BeaconConsumer, RangeNotifier {

    final static String STATE_SCANNING = "STATE_SCANNING";

    protected Region mRegion;
    protected boolean isReadyForScan;
    protected boolean isScanning;
    protected BeaconManager mBeaconManager;
    protected boolean needContinueScan;

    ArrayList<Beacon> beaconArrayList = new ArrayList<>();
    Beacon foundBeacon;

    static final int SCAN_TIMEOUT = 400000;
    protected CountDownTimer mTimer;


    public BeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Service", "Scan Stopped");
        stopScan();
        mBeaconManager.unbind(this);
//        isReadyForScan = false;
//        mTimer.cancel();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERV", "onCreate: STARTED SERVICE");
        mBeaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        mRegion = new Region(PreferencesUtil.getDefaultRegionName(getApplicationContext()), null, null, null);
        mBeaconManager.bind(this);
        mBeaconManager.addRangeNotifier(this);

        setupTimer();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public void scanStartStopAction() {
        if (isScanning) {
            stopScan();
        } else {
            startScan();
        }
    }

    public void startScan() {
        mTimer.start();
        try {
            if (isCanScan() & mBeaconManager.isBound(this)) {
                mBeaconManager.startRangingBeaconsInRegion(mRegion);
                isScanning = true;
                Log.d("OMNI", "started X");
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

//    public abstract void onCanScan();
//
//    public abstract void updateBeaconList(final Collection<Beacon> beacons);
//
//    public abstract void updateBeaconList(final Collection<Beacon> beacons, final Region region);

    protected boolean isCanScan() {
        return isReadyForScan;
    }

    @Override
    public void onBeaconServiceConnect() {
        isReadyForScan = true;
        isScanning = false;
//        onCanScan();
        Log.d("OMNI", "connetced X");
        scanStartStopAction();
    }

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
        if (beacons != null) {
            if (beacons.size() > 0 && region != null && region.equals(mRegion)) {
                Log.d("BEACON", "size: " + beacons.size());
                beaconArrayList.clear();
                ArrayList<Beacon> rawList = new ArrayList<>(beacons);
                int minIndex = 0;
                double minDistance = Double.MAX_VALUE;
                for (int i = 0; i < rawList.size(); i++) {
                    if (minDistance > rawList.get(i).getDistance()) {
                        minDistance = rawList.get(i).getDistance();
                        beaconArrayList.add(0, rawList.get(i));
                    }
                }

                if (!TextUtils.isEmpty(beaconArrayList.get(0).getBluetoothName()) && beaconArrayList.get(0).getBluetoothName().equalsIgnoreCase("POI")) {

                    if (foundBeacon == null) {
                        foundBeacon = beaconArrayList.get(0);
//                        throwPush(foundBeacon);
                        OmniCrow.trackBeaconEvent(new BeaconModel(OmniCrow.getUserId(), foundBeacon.getIdentifier(2).toString(), foundBeacon.getIdentifier(1).toString()));

                    } else {
                        //check beacon already on the list
                        if (!foundBeacon.getIdentifier(2).toString().equalsIgnoreCase(beaconArrayList.get(0).getIdentifier(2).toString())) {
                            foundBeacon = beaconArrayList.get(0);
//                            throwPush(foundBeacon);
                            OmniCrow.trackBeaconEvent(new BeaconModel(OmniCrow.getUserId(), foundBeacon.getIdentifier(2).toString(), foundBeacon.getIdentifier(1).toString()));
                        }

                    }

                }
            } else {
//                updateBeaconList(beacons, region);
            }
        }
    }

    @Override
    public Context getApplicationContext() {
        return getApplication();
    }

//    @Override
//    public void unbindService(ServiceConnection serviceConnection) {
//        Log.d("OMNI", "scan fragment unbound from beacon service");
//        if (mBeaconManager.isBound(this)) {
////            unbindService(serviceConnection);
//        }
//        isReadyForScan = false;
//        isScanning = false;
//        mTimer.cancel();
//    }

//    @Override
//    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
//        Log.d("OMNI", "scan fragment bound to beacon service");
//        return bindService(intent, serviceConnection, i);
//    }


    private void setupTimer() {
        mTimer = new CountDownTimer(SCAN_TIMEOUT, PreferencesUtil.getManualScanTimeout(getApplicationContext())) {
            public void onFinish() {
                stopScanTimeout();
            }

            public void onTick(long tick) {
            }
        };
    }

    private void stopScanTimeout() {
        stopScan();
        Toast.makeText(getApplication(), "NOT FOUND", Toast.LENGTH_SHORT).show();
    }


    private void throwPush(Beacon beacon) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.omnicrow_logo_small)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.omnicrow_logo))
                .setContentTitle("BEACON FOUND")
                .setContentText(beacon.getBluetoothName());


        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notificationBuilder.build());
    }

}
