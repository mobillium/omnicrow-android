package com.mobillium.omnicrow.webservice.models;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class BeaconModel extends BaseModel {
    private String major;
    private String minor;

    public BeaconModel(String user_id, String minor, String major) {
        super(user_id);
        this.major = major;
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
