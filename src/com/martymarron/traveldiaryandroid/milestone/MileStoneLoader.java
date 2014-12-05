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
import java.util.Map;

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
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObject.Factory;
import com.facebook.model.GraphPlace;

/**
 * @author x-masashik
 *
 */
public class MileStoneLoader {
	
	private static final String TAG = "MileStoneLoader";

	private MyRequestCallback callback;
	
	private Bundle param;
	
	private String graphPath = "";
	
	private List<MileStone> mileStones;
	
	private MileStoneLoader() {
	}
	
	public MileStoneLoader(MileStoneLoaderCallback mscallback, Bundle param) {
		this(mscallback, param, "");
	}
	
	public MileStoneLoader(MileStoneLoaderCallback mscallback, Bundle param, String graphpath) {
		this();
		callback = new MyRequestCallback(mscallback);
		this.graphPath = graphpath;
		this.param = param;
	}
	
	public void load() throws MileStoneLoaderException {
		
		try {
		
		    Session session = Session.getActiveSession();
		
		    if (session != null) {

			    Bundle parameter = new Bundle(this.param);
			    parameter.putString("locale", Locale.getDefault().toString());
			    Request request = new Request(session, graphPath, parameter, HttpMethod.GET, callback);
			    Log.d(TAG, request.toString());
			
			    RequestAsyncTask task = new RequestAsyncTask(request);
			    task.execute();
		    }
		} catch (Exception e) {
			throw new MileStoneLoaderException(e);
		}
	}
	
	public void publish() throws MileStoneLoaderException {
		try {
			
		    Session session = Session.getActiveSession();
		
		    if (session != null) {

			    Bundle parameter = new Bundle(this.param);
			    parameter.putString("locale", Locale.getDefault().toString());
			    Request request = 
			    		new Request(session, graphPath, parameter, HttpMethod.POST, callback);
			    Log.d(TAG, request.toString());
			
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
		
		private static final String TAG = "MyRequestCallback";
		
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
				
				try {

				    mileStones = new ArrayList<MileStone>(loadMileStones(response.getGraphObject()));
				    callback.onLoaded(mileStones);
				
			    } catch (JSONException e) {
				    Log.w(TAG, "JSON error "+ e.getMessage());
			    }
			}
		}
		
		private List<MileStone> loadMileStones(GraphObject graphObject) throws JSONException {
			Log.d(TAG, TAG + ".loadMileStones");
			
			Log.d(TAG, graphObject.toString());
			Log.d(TAG, graphObject.asMap().keySet().toString());
			
			Map<String, Object> goMap = graphObject.asMap();

			List<MileStone> milestones = new ArrayList<MileStone>();
			for (String key : goMap.keySet()) {
		        MileStone ms = new MileStone();
		        
		        Map<String, Object> map =
		        		Factory.create(new JSONObject(goMap.get(key).toString())).asMap();
		        
		        if (map.containsKey("venue")) {
		        
		            ms.setName(map.get("name").toString());
		            ms.setLocation(map.get("location").toString());
		            ms.setTimezone(map.get("timezone").toString());
				    try {
					    SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd");
				        ms.setStartTime(stf.parse(map.get("start_time").toString()));
					
					    SimpleDateFormat utf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
					    ms.setUpdatedTime(utf.parse(map.get("updated_time").toString()));
				    } catch (ParseException e) {
					    Log.w(TAG, e.getMessage());
				    }

				    JSONObject json = new JSONObject(map.get("venue").toString());
				    Log.d(TAG, json.toString(4));
				    Map<String, Object> gvenue =
						Factory.create(json).asMap();

				    MileStone.Venue venue = new MileStone.Venue();
			        venue.setLatitude(Double.valueOf(gvenue.get("latitude").toString()));
		            venue.setLongitude(Double.valueOf(gvenue.get("longitude").toString()));
			        ms.setVenue(venue);
			        
		        } else {
				    JSONObject json = new JSONObject(goMap.get(key).toString());
				    Log.d(TAG, json.toString(4));
		        	GraphPlace gPlace = 
		        			Factory.create(json, GraphPlace.class);
		        	ms.setName(gPlace.getName());

		        	GraphLocation gLocation = gPlace.getLocation();
		        	
		        	MileStone.Venue venue = new MileStone.Venue();
		        	venue.setLatitude(gLocation.getLatitude());
		        	venue.setLongitude(gLocation.getLongitude());
		        	ms.setVenue(venue);
		        }
			
			    milestones.add(ms);
			}

			Collections.sort(milestones);
			return milestones;
		}
		
	}

}
