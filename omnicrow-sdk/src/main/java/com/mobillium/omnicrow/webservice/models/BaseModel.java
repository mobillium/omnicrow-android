package com.mobillium.omnicrow.webservice.models;

import android.content.pm.PackageInfo;

import com.google.gson.annotations.SerializedName;
import com.mobillium.omnicrow.OmniCrow;

import static com.mobillium.omnicrow.OmniCrow.generateUUID;
import static com.mobillium.omnicrow.OmniCrow.getUserId;

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

    public String getUuid() {
        return uuid;
    }

    public String getUser_id() {
        return user_id;
    }

    public BaseModel() {
        this.platform = "android";
        this.version = getVersionCode();
        this.uuid = generateUUID();
        this.user_id = getUserId();
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
