/**
 * 
 */
package com.martymarron.traveldiaryandroid.milestone;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;

/**
 * @author x-masashik
 *
 */
public class MileStoneLoader {
	
	private static final String TAG = "MileStoneLoader";

	private MyRequestCallback callback;
	
	private MileStoneLoader() {
	}
	
	public MileStoneLoader(MileStoneLoaderCallback mscallback) {
		this();
		callback = new MyRequestCallback(mscallback);
	}
	
	public void load() throws MileStoneLoaderException {
		
		try {
		
		    Session session = Session.getActiveSession();
		
		    if (session != null) {

			    Bundle param = new Bundle();
			    param.putString("ids", "293823777472129,944207338929355,629132557185394,1407160042867804");
			    Request request = new Request(session, "", param, HttpMethod.GET, callback);
			    Log.d(TAG, "start get venues");
			
			    RequestAsyncTask task = new RequestAsyncTask(request);
			    task.execute();
		    }
		} catch (Exception e) {
			throw new MileStoneLoaderException(e);
		}
	}

	public interface MileStoneLoaderCallback {
		
		public void onLoaded(List<MileStone> milestones);
	}
	
	public class MileStoneLoaderException extends Exception {
		
		public MileStoneLoaderException(Exception e) {
			super(e);
			Log.d(TAG, e.getLocalizedMessage());
		}
	}
		
	private class MyRequestCallback implements Request.Callback {
		
		private MileStoneLoaderCallback callback;
		
		public MyRequestCallback(MileStoneLoaderCallback callback) {
			this.callback = callback;
		}

		@Override
		public void onCompleted(Response response) {

			FacebookRequestError error = response.getError();
			if (error != null) {
				
				Log.e(TAG, error.getErrorMessage());
				
			} else {

				Log.d(TAG, "start " + response.getRequest().getGraphPath());
				Log.d(TAG, "raw response=(" + response.getRawResponse() + ")");

	            JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	        
			    try {
				
				    callback.onLoaded(loadMileStones(graphResponse));
				
			    } catch (JSONException e) {
				    Log.w(TAG, "JSON error "+ e.getMessage());
			    }
			}
		}
		
		private List<MileStone> loadMileStones(JSONObject graphResponse) throws JSONException {
			Log.d(TAG, TAG + ".loadMileStones");
			
			List<MileStone> milestones = new ArrayList<MileStone>();
			
			JSONArray events = graphResponse.names();
			
			for (int i=0; i<events.length(); i++) {
				JSONObject event = graphResponse.getJSONObject(events.getString(i));
				JSONObject venue = event.getJSONObject("venue");

		        MileStone ms = new MileStone();
				ms.setLatitude(venue.getDouble("latitude"));
				ms.setLongitude(venue.getDouble("longitude"));
				ms.setLocation(event.getString("location"));
				
				milestones.add(ms);
			}

			return milestones;
		}
		
	}

}
