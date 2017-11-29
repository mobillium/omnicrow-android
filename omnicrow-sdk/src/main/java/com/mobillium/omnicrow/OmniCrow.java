package com.mobillium.omnicrow;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mobillium.omnicrow.utils.MyVolley;
import com.mobillium.omnicrow.utils.OmniCrowAnalyticsLogger;
import com.mobillium.omnicrow.utils.OmniCrowAnalyticsSdkNotInitializedException;
import com.mobillium.omnicrow.webservice.ServiceCallback;
import com.mobillium.omnicrow.webservice.ServiceException;
import com.mobillium.omnicrow.webservice.ServiceOperations;
import com.mobillium.omnicrow.webservice.ServiceSuccess;
import com.mobillium.omnicrow.webservice.models.BeaconModel;
import com.mobillium.omnicrow.webservice.models.CartModel;
import com.mobillium.omnicrow.webservice.models.CategoryModel;
import com.mobillium.omnicrow.webservice.models.ItemModel;
import com.mobillium.omnicrow.webservice.models.PurchaseModel;
import com.mobillium.omnicrow.webservice.models.PushModel;
import com.mobillium.omnicrow.webservice.models.RequestModel;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class OmniCrow {
    private static MyVolley myVolley;
    public static Gson gson;
    public static SharedPreferences mSharedPrefs;
    public static SharedPreferences.Editor mPrefsEditor;
    private static RequestQueue requestQueue;

    static OmniCrow instance = null;
    static String appId;
    static String userId;
    static Context applicationContext;
    static Boolean sdkInitialized = false; //Default false
    boolean isDebugEnabled = false; //Default false

    public static boolean SANDBOX_MODE = false; //Default false


    /**
     * Default empty constructor
     */
    private OmniCrow() {
    }

    /**
     * This function creates an instance of OmniCrow
     * Must be called after "sdkInitialize" method otherwise will throw an exception
     */
    public static OmniCrow getInstance() {
        try {
            if (instance == null) {
                instance = new OmniCrow();
            }
            return instance;
        } catch (OmniCrowAnalyticsSdkNotInitializedException ex) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }
    }

    public String getPackageName() {
        if (applicationContext == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }
        return applicationContext.getPackageName();
    }

    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    /**
     * This function sets the debug state of OmniCrow SDK
     * If it is true, Logs will appear on Android Monitor / LogCat
     * If it is false Logs will not appear on Android Monitor / LogCat
     *
     * @param debugEnabled the boolean value of debug is enabled or not
     */
    public void setDebugEnabled(boolean debugEnabled) {
        isDebugEnabled = debugEnabled;
    }

    /**
     * This function initializes the OmniCrow SDK, the behavior of OmniCrow SDK functions are
     * undetermined if this function is not called. It should be called as early as possible.
     *
     * @param applicationContext The application context
     * @param appId              String identifier value provided by OmniCrow
     * @param sandBox            boolean value determines SKD works in sandbox or live mode
     */
    public static synchronized void sdkInitialize(Context applicationContext, String appId, boolean sandBox) {
        if (sdkInitialized == true) {
            return;
        }

        OmniCrow.applicationContext = applicationContext.getApplicationContext();
        OmniCrow.appId = appId;
        setSandBoxMode(sandBox);
        sdkInitialized = true;


    }


    private static void setSandBoxMode(boolean val) {
        SANDBOX_MODE = val;
    }

    public static String getUserId() {
        if(TextUtils.isEmpty(userId)){
           return null;

        }
        return userId;
    }

    public static void setUserId(String userId) {
        OmniCrow.userId = userId;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .create();
        }
        return gson;
    }


    public static SharedPreferences getmSharedPrefs() {
        if (mSharedPrefs == null) {
            mSharedPrefs = getContext().getSharedPreferences("tubitakSdk",
                    MODE_PRIVATE);
        }
        return mSharedPrefs;
    }

    public static SharedPreferences.Editor getmPrefsEditor() {

        if (mPrefsEditor == null || mSharedPrefs == null) {
            mSharedPrefs = getContext().getSharedPreferences("tubitakSdk",
                    MODE_PRIVATE);
            mPrefsEditor = mSharedPrefs.edit();
        }
        return mPrefsEditor;
    }

    public static MyVolley getMyVolley(Context ctx) {
        if (myVolley == null) {
            myVolley = new MyVolley(ctx);
        }
        return myVolley;
    }

    public static synchronized MyVolley getMyVolley() {
        if (myVolley == null) {
            myVolley = new MyVolley(getContext());
        }
        return myVolley;
    }

    public static Context getContext() {
        if (applicationContext == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException();
        }
        return applicationContext;
    }


    public synchronized static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext);
        }
        Log.d("TEST", "entering getRequestQueue");
        Log.d("TEST", "Application instance: " + applicationContext);
        Log.d("TEST", "requestQueue instance: " + requestQueue);

        return requestQueue;

    }


    public static void trackItemEvent(ItemModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "event/item", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }

    public static void trackCategoryEvent(CategoryModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "event/category", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }

    public static void trackPurchaseEvent(PurchaseModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "event/purchase", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }

    public static void trackCartEvent(CartModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "event/cart", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }


    public static void trackBeaconEvent(BeaconModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "event/beacon", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }


    public static void registerPushToken(PushModel itemModel) {
        if (itemModel == null) {
            throw new OmniCrowAnalyticsSdkNotInitializedException("You must initialize the OmniCrow SDK first");
        }

        RequestModel requestModel = new RequestModel(Request.Method.POST, "device", null, true, itemModel);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ServiceSuccess>() {
            @Override
            public void success(ServiceSuccess result) {
                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ServiceSuccess>() {
        });

    }

    public static String generateUUID() {
        String uuid = getmSharedPrefs().getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            getmPrefsEditor().putString("uuid", uuid).commit();
        }

        return uuid;

    }

}
