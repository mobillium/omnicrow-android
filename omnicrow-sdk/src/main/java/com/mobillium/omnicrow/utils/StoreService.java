package com.mobillium.omnicrow.utils;

import com.mobillium.omnicrow.webservice.models.ActionBeacon;
import com.mobillium.omnicrow.webservice.models.TrackedBeacon;

import java.util.List;

/**
 * Created by oguzhandongul on 15/11/2017.
 */

public interface StoreService {

    boolean createBeacon(final TrackedBeacon beacon);

    boolean updateBeacon(final TrackedBeacon beacon);

    boolean deleteBeacon(final String id, boolean cascade);

    TrackedBeacon getBeacon(final String id);

    List<TrackedBeacon> getBeacons();

    boolean updateBeaconDistance(final String id, double distance);

    boolean updateBeaconAction(ActionBeacon beacon);

    boolean createBeaconAction(ActionBeacon beacon);

    List<ActionBeacon> getBeaconActions(final String beaconId);

    boolean deleteBeaconAction(final int id);

    boolean deleteBeaconActions(final String beaconId);

    List<ActionBeacon> getAllEnabledBeaconActions();

    boolean updateBeaconActionEnable(final int id, boolean enable);

    List<ActionBeacon> getEnabledBeaconActionsByEvent(final int eventType, final String beaconId);

    boolean isBeaconExists(String id);
}