package com.mobillium.omnicrow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mobillium.omnicrow.utils.MyVolley;
import com.mobillium.omnicrow.utils.OmniCrowAnalyticsException;
import com.mobillium.omnicrow.utils.OmniCrowAnalyticsLogger;
import com.mobillium.omnicrow.utils.OmniCrowAnalyticsSdkNotInitializedException;
import com.mobillium.omnicrow.webservice.ServiceCallback;
import com.mobillium.omnicrow.webservice.ServiceException;
import com.mobillium.omnicrow.webservice.ServiceOperations;
import com.mobillium.omnicrow.webservice.ServiceSuccess;
import com.mobillium.omnicrow.webservice.models.BaseModel;
import com.mobillium.omnicrow.webservice.models.BeaconModel;
import com.mobillium.omnicrow.webservice.models.CartModel;
import com.mobillium.omnicrow.webservice.models.CategoryModel;
import com.mobillium.omnicrow.webservice.models.ItemModel;
import com.mobillium.omnicrow.webservice.models.PurchaseModel;
import com.mobillium.omnicrow.webservice.models.PushModel;
import com.mobillium.omnicrow.webservice.models.RequestModel;
import com.mobillium.omnicrow.webservice.models.ResponsePopup;

import java.util.HashMap;
import java.util.Map;
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
    public static String baseUrl;
    static String userId = "";
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
     * @param baseUrl            String identifier value provided by OmniCrow
     * @param sandBox            boolean value determines SKD works in sandbox or live mode
     */
    public static synchronized void sdkInitialize(Context applicationContext, String baseUrl, boolean sandBox) {
        if (sdkInitialized == true) {
            return;
        }
        if (TextUtils.isEmpty(baseUrl)) {
            throw new OmniCrowAnalyticsException("You must set base url");
        }

        OmniCrow.applicationContext = applicationContext.getApplicationContext();
        OmniCrow.baseUrl = baseUrl;
        setSandBoxMode(sandBox);
        sdkInitialized = true;


    }


    private static void setSandBoxMode(boolean val) {
        SANDBOX_MODE = val;
    }

    public static String getUserId() {
//        if (TextUtils.isEmpty(userId)) {
//            throw new OmniCrowAnalyticsException("You must set userId");
//
//        }
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
            mSharedPrefs = getContext().getSharedPreferences("OmniCrowSdk",
                    MODE_PRIVATE);
        }
        return mSharedPrefs;
    }

    public static SharedPreferences.Editor getmPrefsEditor() {

        if (mPrefsEditor == null || mSharedPrefs == null) {
            mSharedPrefs = getContext().getSharedPreferences("OmniCrowSdk",
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


    public static void requestForPopup(final AppCompatActivity compatActivity) {
        if (TextUtils.isEmpty(getUserId())) {
            return;
        }
        BaseModel baseModel = new BaseModel();
        Map<String, String> params = new HashMap<>();
        params.put("uuid", baseModel.getUuid());
        params.put("platform", baseModel.getPlatform());
        params.put("version", baseModel.getVersion());
        params.put("user-id", baseModel.getUser_id());


        RequestModel requestModel = new RequestModel(Request.Method.GET, "popup", null, false, params);
        ServiceOperations.makeRequest(applicationContext, requestModel, new ServiceCallback<ResponsePopup>() {
            @Override
            public void success(ResponsePopup result) {
//                OmniCrowAnalyticsLogger.writeInfoLog(result.getMessage());
                if (compatActivity != null && !compatActivity.isDestroyed()) {
                    showPopup(compatActivity, result);

                }
            }

            @Override
            public void error(ServiceException e) {
                OmniCrowAnalyticsLogger.writeErrorLog(e.getMessage());

            }
        }, new TypeToken<ResponsePopup>() {
        });

//        params.put("test_mode", "1");
    }

    public static String generateUUID() {
        String uuid = getmSharedPrefs().getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            getmPrefsEditor().putString("uuid", uuid).commit();
        }

        return uuid;

    }


    private static void showPopup(final AppCompatActivity compatActivity, final ResponsePopup responsePopup) {
        LayoutInflater inflater = (LayoutInflater) compatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(compatActivity);
        View dialogView = inflater.inflate(R.layout.dialog_ad_popup, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //createviews
        ScrollView svPopUp = (ScrollView) dialogView.findViewById(R.id.nsvPopup);
        LinearLayout llPopUp = (LinearLayout) dialogView.findViewById(R.id.llPopup);
        RelativeLayout rlImage = (RelativeLayout) dialogView.findViewById(R.id.rlImage);
        Button btPositive = (Button) dialogView.findViewById(R.id.btKabul);
        Button btNegative = (Button) dialogView.findViewById(R.id.btKapat);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) dialogView.findViewById(R.id.tvContent);
        final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        final ImageView ivPopup = (ImageView) dialogView.findViewById(R.id.ivPopup);

        //setdatas
        btPositive.setText(responsePopup.getButton());
        tvTitle.setText(responsePopup.getTitle());
        tvContent.setText(responsePopup.getContent());

        if (responsePopup.getContent().length() > 300) {
            LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, convertDpiToPixel(compatActivity, 300));
            svPopUp.setLayoutParams(scrollParams);
        }

        if (responsePopup.getImage() != null && !TextUtils.isEmpty(responsePopup.getImage().getUrl())) {
            Glide.with(compatActivity)
                    .load(responsePopup.getImage().getUrl())
                    .asBitmap()
//                    .error(R.drawable.img_vitrinova_placeholder)
//                    .placeholder(R.drawable.img_vitrinova_placeholder)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            ivPopup.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                            progressBar.setIndeterminate(false);
                        }
                    });
        } else {
            ivPopup.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            progressBar.setIndeterminate(false);
        }

        //setlisteners
        btPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = responsePopup.getUri();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                compatActivity.startActivity(i);
            }
        });

        btNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public static int convertDpiToPixel(Context ctx, int dpi) {
        float pixel = 0;
        try {
            Resources r = ctx.getResources();
            pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi,
                    r.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int) pixel;
    }

}
