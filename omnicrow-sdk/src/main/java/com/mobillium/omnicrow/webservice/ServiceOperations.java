package com.mobillium.omnicrow.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.mobillium.omnicrow.BuildConfig;
import com.mobillium.omnicrow.OmniCrow;
import com.mobillium.omnicrow.R;
import com.mobillium.omnicrow.webservice.models.RequestModel;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.mobillium.omnicrow.OmniCrow.baseUrl;
import static com.mobillium.omnicrow.OmniCrow.getGson;


/**
 * Created by oguzhandongul on 31/05/2017.
 */

public class ServiceOperations {
    //    private static String baseUrl = "https://tubitak.mobillium.com/api/";
    private static String sandboxUrl = "https://dev.tubitak.mobillium.com/api/";
    public static ProgressDialog pd;
    public static final boolean DEBUG = BuildConfig.DEBUG;


    public static <T> void makeRequest(final Context mContext,
                                       final RequestModel requestModel, final ServiceCallback<T> callback, final TypeToken typeToken) {


        if (OmniCrow.SANDBOX_MODE) {
            baseUrl = sandboxUrl;
        } else {
            baseUrl = baseUrl;
        }


        if (!isOnline(mContext)) {
            if (mContext != null && DEBUG) {
                try {
                    Toast.makeText(mContext, mContext.getString(R.string.error_internet), Toast.LENGTH_SHORT).show();
                    callback.error(new ServiceException(mContext.getString(R.string.error_internet)));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        }

        final WeakReference<Context> contextRef = new WeakReference<>(mContext);

        String getParams = "";
        if (requestModel.getServiceType() == Request.Method.GET) {
            getParams = "?" + encodeParameters(requestModel.getParams());

        }

        final String url = baseUrl + requestModel.getOffsetUrl() + getParams;


        if (DEBUG || true) {
            Log.d("OmniCrow", "İstek yapılan url: " + url);
            if (requestModel.getData() != null) {
                Log.d("OmniCrow", "İstek yapılan model: " + getGson().toJson(requestModel.getData()));
            }
        }

        if (requestModel.getPdMessage() != null && !TextUtils.isEmpty(requestModel.getPdMessage())) {
            try {
                pd = ProgressDialog.show(mContext, null, requestModel.getPdMessage());
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        final OmniCrowRequest request = new OmniCrowRequest(requestModel.getServiceType(), url, requestModel.getData() == null ? null : getGson().toJson(requestModel.getData()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (requestModel.getPdMessage() != null || !TextUtils.isEmpty(requestModel.getPdMessage())) {
                    try {
                        pd.dismiss();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (DEBUG) {
                    Log.d("OmniCrow", "Servis cevabı : " + response);
                }
                Log.d("OmniCrow", "Servis cevabı : " + response);
                if (callback != null) {

                    if (TextUtils.isEmpty(response)) {
                        callback.success(null);
                        return;
                    }

                    T baseResponse = null;
                    try {
                        baseResponse = getGson().fromJson(response, typeToken.getType());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        callback.error(new ServiceException(OmniCrow.getContext().getString(R.string.error_bilinmeyen)));
                        return;
                    }
                    if (baseResponse != null) {
                        callback.success(baseResponse);
                    } else {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String volleyErrorString = "";
                ServiceError serviceError = null;
                ServiceException serviceException = null;


                if (requestModel.getPdMessage() != null) {
                    try {
                        pd.dismiss();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


                if (DEBUG) {
                    Log.d("OmniCrow", "Servis cevabı : " + volleyError);
                }
                Log.d("OmniCrow", "Servis cevabı : " + volleyError);

                final Context context = contextRef.get();
                if (context == null) {
                    return;
                }

                // volleyError null döndüyse
                if (volleyError == null) {
                    if (requestModel.isShowErrorMessage()) {
                        if (DEBUG) {
                            Toast.makeText(mContext, mContext.getString(R.string.error_bilinmeyen), Toast.LENGTH_SHORT).show();
                        }
                    }
                    serviceException = new ServiceException(context.getString(R.string.error_bilinmeyen));
                    if (callback != null) {
                        callback.error(serviceException);
                    }
                    return;
                }

                // Timeout'a girildiyse
                if (volleyError instanceof TimeoutError) {
                    if (requestModel.isShowErrorMessage()) {
                        if (DEBUG) {
                            Toast.makeText(mContext, mContext.getString(R.string.error_cevap_yok), Toast.LENGTH_SHORT).show();
                        }
                    }
                    serviceException = new ServiceException(context.getString(R.string.error_cevap_yok));
                    if (callback != null) {
                        callback.error(serviceException);
                    }
                    return;
                }

                try {
                    volleyErrorString = new String(volleyError.networkResponse.data, "UTF-8");
                    serviceError = getGson()
                            .fromJson(volleyErrorString, ServiceError.class);

                } catch (Exception e) {
                    volleyErrorString = "";
                    e.printStackTrace();
                }

                if (DEBUG) {
                    Log.d("OmniCrow", "Servis cevabı : " + volleyErrorString);
                }
                Log.d("OmniCrow", "Servis cevabı : " + volleyErrorString);

                //Hata mesajı decode edilemediyse veya boşsa
                if (TextUtils.isEmpty(volleyErrorString)) {
                    if (requestModel.isShowErrorMessage() && DEBUG) {
                        Toast.makeText(mContext, mContext.getString(R.string.error_bilinmeyen), Toast.LENGTH_SHORT).show();
                    }
                    serviceException = new ServiceException(context.getString(R.string.error_bilinmeyen));
                    if (callback != null) {
                        callback.error(serviceException);
                    }
                } else {
                    serviceException = new ServiceException(serviceError.getError());
                    if (callback != null) {
                        callback.error(serviceException);
                    }

                    //Hata durumları bunlar ise yapılacak ekstra bişey varsa yap.
                    //Kullanıcıyı logout ettirmek vb.
                    if (volleyError.networkResponse.statusCode == 503) {

                    } else if (volleyError.networkResponse.statusCode == 403) {
                        //forbidden
//                        showForbiddenDialog();//TODO
                    } else if (volleyError.networkResponse.statusCode == 401) {
                        //unauthorized
//                        restartApp();//TODO
                    } else if (volleyError.networkResponse.statusCode == 110) {
                        //not approved
//                        showInfoDialog(); //TODO
                    } else {
                        if (requestModel.isShowErrorMessage() && DEBUG) {
                            try {
                                Toast.makeText(mContext, serviceError.getError(), Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //TODO
//                params.put("p", "2"); //Android
//                params.put("X-Mobillium-Token", Auth.getStoredToken());

//                if (!TextUtils.isEmpty(Auth.getAuth().getPinToken())) {
//                    params.put("token", Auth.getAuth().getPinToken()); //Android
//                }
                Log.d("HEADER", "getHeaders: " + params.toString());
                return params;
            }
        };


        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 1, 1.0f));

        if (requestModel.getServiceType() == Request.Method.DELETE || requestModel.getServiceType() == Request.Method.PUT) {
            String userAgent = "volley/0";
            try {
                String packageName = mContext.getPackageName();
                PackageInfo info = mContext.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            HttpStack httpStack = new OmniCrowHttpClientStack(AndroidHttpClient.newInstance(userAgent));
            RequestQueue requesQueue = Volley.newRequestQueue(mContext, httpStack);
            requesQueue.add(request);
        } else {
            OmniCrow.getMyVolley(mContext)
                    .getRequestQueue()
                    .add(request);
        }
    }


    /**
     * Checks the device is online or not
     */
    public static boolean isOnline(Context mContext) {
        if (mContext != null) {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }


    public static String encodeParameters(Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(),
                        "UTF-8"));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(),
                        "UTF-8"));
                encodedParams.append('&');
            }
            return "?" + encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: "
                    + "UTF-8", uee);
        }
    }
}
