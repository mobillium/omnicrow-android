package com.mobillium.omnicrow.webservice.models;

import android.content.pm.PackageInfo;

import com.google.gson.annotations.SerializedName;
import com.mobillium.omnicrow.OmniCrow;

import static com.mobillium.omnicrow.OmniCrow.generateUUID;

/**
 * Created by oguzhandongul on 20/11/2017.
 */

public class BaseModel {
    private String platform;
    private String version;
    private String uuid;
    @SerializedName("user-id")
    private String user_id;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BaseModel(String user_id) {
        this.platform = "android";
        this.version = getVersionCode();
        this.uuid = generateUUID();
        this.user_id = user_id;
    }


    private String getVersionCode() {
        try {
            PackageInfo pInfo = OmniCrow.getContext().getPackageManager().getPackageInfo(OmniCrow.getContext().getPackageName(), 0);
            return pInfo.versionName;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
