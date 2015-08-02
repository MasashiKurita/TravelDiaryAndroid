package com.martymarron.traveldiaryandroid;

import android.app.Application;

import com.facebook.FacebookSdk;

public class AddMileStoneApplication extends Application {

//    public GraphPlace getSelectedPlace() {
//        return selectedPlace;
//    }
//
//    public void setSelectedPlace(GraphPlace selectedPlace) {
//        this.selectedPlace = selectedPlace;
//    }
//
//    private GraphPlace selectedPlace;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
