package com.mobillium.omnicrow.webservice.models;

import java.util.List;

/**
 * Created by oguzhandongul on 15/11/2017.
 */

public interface ITriggerable {
    List<ActionBeacon> getActions();

    void addAction(ActionBeacon action);

    void addActions(List<ActionBeacon> actions);
}