package com.mobillium.omnicrow.webservice.models;

import com.mobillium.omnicrow.utils.Constants;

/**
 * Created by oguzhandongul on 15/11/2017.
 */

public class RegionName {
    private String prefix;
    private String beaconId;
    private String actionName;
    private ActionBeacon.EventType eventType;

    protected RegionName(String prefix, String beaconId, ActionBeacon.EventType eventType, String actionName) {
        this.prefix = prefix;
        this.beaconId = beaconId;
        this.actionName = actionName;
        this.eventType = eventType;
    }

    public static RegionName parseString(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("value is null");
        }
        String[] strings = value.split("::");
        if (strings != null && strings.length == 4) {
            return new RegionName(strings[0], strings[1], ActionBeacon.EventType.fromInt(Integer.parseInt(strings[2])), strings[3]);
        }
        return new RegionName("unknown", "unknown", ActionBeacon.EventType.EVENT_ENTERS_REGION, value);
    }

    public static String buildRegionNameId(ActionBeacon actionBeacon) {
        return Constants.REGION_NAME_PREFIX + "::" + actionBeacon.getBeaconId() + "::"
                + actionBeacon.getEventType().getValue() + "::" + actionBeacon.getName();
    }


    public boolean isApplicationRegion() {
        return this.prefix.equalsIgnoreCase(Constants.REGION_NAME_PREFIX);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public String getActionName() {
        return actionName;
    }

    public ActionBeacon.EventType getEventType() {
        return eventType;
    }
}