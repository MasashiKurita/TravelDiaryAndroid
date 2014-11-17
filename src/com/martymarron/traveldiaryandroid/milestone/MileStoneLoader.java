/**
 * 
 */
package com.martymarron.traveldiaryandroid.milestone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
	
	private List<MileStone> mileStones;
	
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
			    param.putString("locale", Locale.getDefault().toString());
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
	
	public List<MileStone> getMileStones() {
		return this.mileStones;
	}

	public interface MileStoneLoaderCallback {
		
		public void onLoaded(List<MileStone> milestones);
	}
	
	public class MileStoneLoaderException extends Exception {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -244762229336558044L;

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
			    Log.d(TAG, "raw request=(" + response.getRequest().toString() + ")");
				Log.d(TAG, "raw response=(" + response.getRawResponse() + ")");

	            JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();

	            try {
				    mileStones = new ArrayList<MileStone>(loadMileStones(graphResponse));
				    callback.onLoaded(mileStones);
				
			    } catch (JSONException e) {
				    Log.w(TAG, "JSON error "+ e.getMessage());
			    }
			}
		}
		
		private List<MileStone> loadMileStones(JSONObject graphResponse) throws JSONException {
			Log.d(TAG, TAG + ".loadMileStones");
			Log.d(TAG, graphResponse.toString(4));
			List<MileStone> milestones = new ArrayList<MileStone>();
			
			JSONArray events = graphResponse.names();
			
			for (int i=0; i<events.length(); i++) {
		        MileStone ms = new MileStone();
		        
				JSONObject event = graphResponse.getJSONObject(events.getString(i));
				ms.setName(event.getString("name"));
				ms.setLocation(event.getString("location"));
				ms.setTimezone(event.getString("timezone"));
				try {
					SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd");
				    ms.setStartTime(stf.parse(event.getString("start_time")));
					
					SimpleDateFormat utf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
					ms.setUpdatedTime(utf.parse(event.getString("updated_time")));
				} catch (ParseException e) {
					Log.w(TAG, e.getMessage());
				}

				JSONObject jsVenue = event.getJSONObject("venue");
				MileStone.Venue venue = new MileStone.Venue();
				venue.setLatitude(jsVenue.getDouble("latitude"));
			    venue.setLongitude(jsVenue.getDouble("longitude"));
				ms.setVenue(venue);
				
				milestones.add(ms);
			}

			Collections.sort(milestones);
			return milestones;
		}
		
	}

}
