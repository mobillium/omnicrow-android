
# OmniCrow Android SDK


![](https://img.shields.io/badge/platform-android-green.svg)
![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)
![](https://img.shields.io/badge/Gradle-v2.2.1-red.svg)




## Requirements

- Android 4.1+
- Android Studio 2.0.0+
- JDK 1.8+


## Setup
Add the **OmniCrowSdk.aar** file as a dependency to your project's **libs** folder manually .

After that, add this line to inside of your project's gradle file's dependency block.
 
```groovy
dependencies {
    implementation(name:'omnicrowsdk', ext:'aar')
}
```

## Usage



### Initialization

You need to initialize OmniCrow SDK before using it. 

```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;

OmniCrowAnalytics.sdkInitialize(getApplicationContext(), "BASE_URL",false);

```
There are 8 different types of features: 
- **Product View**, 
- **Category View**, 
- **Add to Cart**, 
- **Purchase** 
- **Device Register** 
- **Popup Ads** 
- **Push Notification** 
- **Beacon Detecting** 



### Tracking Product Views

After initializing OmniCrow SDK correctly, you can track product views like below:


```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;
import com.mobillium.omnicrowsdk.webservice.models.ItemModel;

OmniCrowAnalytics.trackItemEvent(new ItemModel("CUSTOMER_ID", "PRODUCT_ID"));
```
### Tracking Category Views

You can track category views like the example below:

```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;
import com.mobillium.omnicrowsdk.webservice.models.CategoryModel;

String categoryPath = "Category > " + "CATEGORY_NAME";

OmniCrowAnalytics.trackCategoryEvent(new CategoryModel("CUSTOMER_ID", categoryPath));
```

### Tracking Add to Cart Events

You can track add to cart events like the example below:

```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;
import com.mobillium.omnicrowsdk.webservice.models.ProductModel;
import com.mobillium.omnicrowsdk.webservice.models.CartModel;

ProductModel productModel = new ProductModel("QUANTITY_OF_PRODUCTS", "PRODUCT_ID", "PRODUCT_PRICE");
ArrayList<ProductModel> list = new ArrayList<>();
            list.add(productModel);
            
OmniCrowAnalytics.trackCartEvent(new CartModel("CUSTOMER_ID", list));
```


### Tracking Purchase Events

You can track purchase events like the example below:

```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;
import com.mobillium.omnicrowsdk.webservice.models.PurchaseModel;
import com.mobillium.omnicrowsdk.webservice.models.ProductModel;

//Add each shopping item to the list
Product product = response3d.getOrder_product();
            ProductModel productModel = new ProductModel("PRODUCT_QUANTITY", "PRODUCT_ID", "PRODUCT_PRICE");
ArrayList<ProductModel> list = new ArrayList<>();
list.add(productModel);

//Use whole item list to send event
OmniCrowAnalytics.trackPurchaseEvent(new PurchaseModel("CUSTOMER_ID", "ORDER_ID", list, "TOTAL_PAID_PRICE"));
```


### Showing Advertisement Popup

After initializing OmniCrow SDK correctly, you can make a request for showing advertisement popup, like the example below:

```java
OmniCrow.requestForPopup(activityContext);
```


### Registering Device for Push Notifications

You can register any device for push notification service like the example below:

```java
OmniCrow.registerPushToken(new PushModel("PUSH_REGISTER_TOKEN");
```

### Detecting Beacons

You can detect beacons after implementing these:

First, add these permissions to your manifest.xml file:

```xml
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
Secondly, implement **BootstrapNotifier** and **RangeNotifier** interfaces on Application Class like this
```java
public class ApplicationClass extends MultiDexApplication implements BootstrapNotifier, RangeNotifier 
```
After that, initialise beacon manager and start tracking on your onCreate() in your Application class.
```java
mBeaconManager = BeaconManager.getInstanceForApplication(this);
initBeaconManager();
enableBackgroundScan(PreferencesUtil.isBackgroundScan(this));
```

Finally add these methods to your application class


```java
import com.mobillium.omnicrowsdk.OmniCrowAnalytics;
import com.mobillium.omnicrowsdk.TubitakAnalytics;
import com.mobillium.omnicrowsdk.utils.Constants;
import com.mobillium.omnicrowsdk.utils.PreferencesUtil;
import com.mobillium.omnicrowsdk.webservice.models.ActionBeacon;
import com.mobillium.omnicrowsdk.webservice.models.ActionRegion;
import com.mobillium.omnicrowsdk.webservice.models.RegionName;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

private void initBeaconManager() {
        mBeaconManager.setBackgroundMode(PreferencesUtil.isBackgroundScan(this));

        if (PreferencesUtil.isEddystoneLayoutUID(this)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        }
        if (PreferencesUtil.isEddystoneLayoutURL(this)) {
            mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        }
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));

       
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconManager.setBackgroundBetweenScanPeriod(PreferencesUtil.getBackgroundScanInterval(this));

        mBeaconManager.setBackgroundScanPeriod(10000L);          // default is 10000L
        mBeaconManager.setForegroundBetweenScanPeriod(0L);      // default is 0L
        mBeaconManager.setForegroundScanPeriod(1100L);          // Default is 1100L

        /*
        RangedBeacon.setMaxTrackingAge() only controls the period of time ranged beacons will continue to be
        returned after the scanning service stops detecting them.
        It has no affect on when monitored regions trigger exits. It is set to 5 seconds by default.
        Monitored regions are exited whenever a scan period finishes and the BeaconManager.setRegionExitPeriod()
        has passed since the last detection.
        By default, this is 10 seconds, but you can customize it.
        Using the defaults, the library will stop sending ranging updates five seconds after a beacon was last seen,
         and then send a region exit 10 seconds after it was last seen.
        You are welcome to change these two settings to meet your needs, but the BeaconManager.setRegionExitPeriod()
        should generally be the same or longer than the RangedBeacon.setMaxTrackingAge().
         */

        mBeaconManager.addRangeNotifier(this);

        try {
            if (mBeaconManager.isAnyConsumerBound()) {
                mBeaconManager.updateScanPeriods();
            }
        } catch (RemoteException e) {
            Log.e(Constants.TAG, "update scan periods error", e);
        }
    }

    public void enableBackgroundScan(boolean enable) {
        if (enable) {
            Log.d(Constants.TAG, "Enable Background Scan");
            enableRegions();
        } else {
            Log.d(Constants.TAG, "Disable Background Scan");
            disableRegions();
        }
    }

    private void disableRegions() {
        if (mRegionBootstrap != null) {
            mRegionBootstrap.disable();
        }
    }

    private void enableRegions() {
        mRegions = getAllEnabledRegions();
        if (mRegions.size() > 0) {
            mRegionBootstrap = new RegionBootstrap(this, mRegions);
        } else {
            Log.d(Constants.TAG, "Ignore Background scan, no regions");
        }
    }

    public List<Region> getAllEnabledRegions() {
        List<Region> regions = new ArrayList<>();
        List<ActionBeacon> actions = new ArrayList<>();
        
        for (ActionBeacon action : actions) {
            regions.add(ActionRegion.parseRegion(action));
        }
        return regions;
    }

    @Override
    public void didEnterRegion(Region region) {
        RegionName regName = RegionName.parseString(region.getUniqueId());

        if (regName.isApplicationRegion()) {
            if (regName.getEventType() == ActionBeacon.EventType.EVENT_NEAR_YOU) {
                try {
                    mBeaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.e(Constants.TAG, "Error start ranging region: " + regName, e);
                }
            }
            if (regName.getEventType() == ActionBeacon.EventType.EVENT_ENTERS_REGION) {
                Intent intent = new Intent();
                intent.setAction(Constants.NOTIFY_BEACON_ENTERS_REGION);
                intent.putExtra("REGION", (Parcelable)region);
                getApplicationContext().sendOrderedBroadcast(intent, null);
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {

        RegionName regName = RegionName.parseString(region.getUniqueId());

        if (regName.isApplicationRegion()) {
            if (regName.getEventType() == ActionBeacon.EventType.EVENT_NEAR_YOU) {
                try {
                    mBeaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.e(Constants.TAG, "Error stop ranging region: " + regName, e);
                }
            }
            if (regName.getEventType() == ActionBeacon.EventType.EVENT_LEAVES_REGION) {
                Intent intent = new Intent();
                intent.setAction(Constants.NOTIFY_BEACON_LEAVES_REGION);
                intent.putExtra("REGION", (Parcelable) region);
                getApplicationContext().sendOrderedBroadcast(intent, null);
            }
        }
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(Constants.TAG, "Region State  " + i + " region " + region);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons != null && beacons.size() > 0 && region != null) {
            RegionName regName = RegionName.parseString(region.getUniqueId());
            if (regName.isApplicationRegion()) {
                if (regName.getEventType() == ActionBeacon.EventType.EVENT_NEAR_YOU) {
                    Iterator<Beacon> iterator = beacons.iterator();
                    while (iterator.hasNext()) {
                        Beacon beacon = iterator.next();
                    }
                }
            }
        }
    }

```