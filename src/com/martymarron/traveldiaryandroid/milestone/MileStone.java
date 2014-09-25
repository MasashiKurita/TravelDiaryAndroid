package com.martymarron.traveldiaryandroid.milestone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MileStone {
	
	private static final String TAG = "MileStone";

	private Context context;
		
	private GoogleMap mMap;
	
	private MileStone() {
	}
	
	public MileStone(Context context, GoogleMap mMap) {
		this.context = context;
		
		this.mMap = mMap;
		
		Session session = Session.getActiveSession();
		
		if (session != null) {
			Request.Callback callback = new MyRequestCallback();

			Bundle param = new Bundle();
			param.putString("ids", "293823777472129,944207338929355,629132557185394,1407160042867804");
			Request request = new Request(session, "", param, HttpMethod.GET, callback);
			Log.d(TAG, "start get venues");
			
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}
		
	private class MyRequestCallback implements Request.Callback {

		@Override
		public void onCompleted(Response response) {
			Log.d(TAG, "start " + response.getRequest().getGraphPath());
			Log.d(TAG, "raw response=(" + response.getRawResponse() + ")");

	        JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	        
			try {
				
				setMarkers(graphResponse);
				
			} catch (JSONException e) {
				Log.i(TAG, "JSON error "+ e.getMessage());
			}
			FacebookRequestError error = response.getError();
			if (error != null) {
				Toast.makeText(context, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
			} else {
			}
		}
		
	    private void setMarkers(JSONObject graphResponse) throws JSONException {

			JSONArray events = graphResponse.names();
			
			for (int i=0; i<events.length(); i++) {
				JSONObject event = graphResponse.getJSONObject(events.getString(i));
				JSONObject venue = event.getJSONObject("venue");

				LatLng location = new LatLng(venue.getDouble("latitude"), venue.getDouble("longitude"));
		        MarkerOptions options = new MarkerOptions();
		        options.position(location);
		        options.title(event.getString("location"));
		        options.snippet(location.toString());
		        
		        mMap.addMarker(options);
			}
		
	    }
	}

}
