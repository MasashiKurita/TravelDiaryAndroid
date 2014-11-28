package com.martymarron.traveldiaryandroid;

import android.app.Application;

import com.facebook.model.GraphPlace;

public class AddMileStoneApplication extends Application {

    public GraphPlace getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(GraphPlace selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    private GraphPlace selectedPlace;

}
