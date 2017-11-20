package com.mobillium.omnicrow.webservice.models;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oguzhandongul on 15/11/2017.
 */

public class ActionRegion {

    public static Region parseRegion(ActionBeacon actionBeacon) {
        if (actionBeacon == null) {
            throw new IllegalArgumentException("ActionBeacon object is null");
        }
        String[] idents = actionBeacon.getBeaconId().split(";");
        if (idents == null || idents.length < 3) {
            throw new IllegalArgumentException("ActionBeacon has invalid id");
        }
        List<Identifier> identifiers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            identifiers.add(Identifier.parse(idents[i]));
        }
        return new Region(RegionName.buildRegionNameId(actionBeacon), identifiers, idents[3]);
    }

}