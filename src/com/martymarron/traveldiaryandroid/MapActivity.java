package com.martymarron.traveldiaryandroid;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import org.springframework.http.HttpMethod;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.martymarron.traveldiaryandroid.milestone.MileStone;
import com.martymarron.traveldiaryandroid.milestone.MileStoneLoader;
import com.martymarron.traveldiaryandroid.milestone.MileStoneLoader.MileStoneLoaderException;
import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;
import com.martymarron.traveldiaryapi.Response;

public class MapActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String TAG = "MapActivity";
	
	private static final String MAP_FRAGMENT_TAG = "map";
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private MapFragment mMapFragment;
	
	private MileStoneLoader msLoader;
		
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	private Diary diary;
	
	private static final int ADD_MILESTONE_ACTIVITY = 1;
	
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	
	boolean pendingPublishReauthorization = false;
	
	private LoaderManager loaderManager;
	
	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback sessionStateCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			Log.i(TAG, "StatusCallback.call");
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Publish permissions Approved...");
			Log.i(TAG, "Access Token: "+ session.getAccessToken());
			
			if (pendingPublishReauthorization
		     && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
	        	publishEvent();
			}
		} else if (state.isClosed()) {
			Log.i(TAG, "Session closed...");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "MapActivity.onCreate");
				
		setContentView(R.layout.activity_map);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		uiHelper = new UiLifecycleHelper(this, sessionStateCallback);
		uiHelper.onCreate(savedInstanceState);
		
		loaderManager = getLoaderManager();

		if (savedInstanceState != null) {
			pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}

	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Log.d(TAG, "MapActivity.onNavigationDrawerItemSelected");
		
		List<com.martymarron.traveldiaryapi.Diary.MileStone> msList = new ArrayList<>();
		if (getIntent().getSerializableExtra(StoryDetailFragment.ARG_ITEM_NAME) instanceof Diary) {
		    diary = (Diary)getIntent().getSerializableExtra(StoryDetailFragment.ARG_ITEM_NAME);
		    msList = diary.getMilestones();
		}

		
		mMapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
		if (mMapFragment == null) {
			mMapFragment = MapFragment.newInstance();
			
			// load and initialize map if first fragment created
			String key = "ids";

			StringBuilder sb = new StringBuilder();
			for (int i=0; i<msList.size(); i++) {
				String str = msList.get(i).getPageId();
				sb.append(str);
				
				if (i < msList.size()-1) {
				  sb.append(",");
				}
			}
			String value = sb.toString();

			Bundle param = new Bundle();
			param.putString(key, value);
			
			final int pos = position;
			msLoader = new MileStoneLoader(new MileStoneLoader.MileStoneLoaderCallback() {
												
				@Override
				public void onLoaded(List<MileStone> milestones) {
					
				    GoogleMap mMap = mMapFragment.getMap();
					List<String> titleList = new ArrayList<String>();
				    List<LatLng> pointList = new ArrayList<LatLng>();
					for (int i=0; i< milestones.size(); i++) {
					    MileStone mileStone = milestones.get(i);
						MileStone.Venue venue = mileStone.getVenue();
						LatLng location = new LatLng(venue.getLatitude(), venue.getLongitude());
				        MarkerOptions options = new MarkerOptions();
					    options.position(location);
					    options.title(mileStone.getLocation());
					    
					    if (mileStone.getTimezone() != null) {
					        Calendar updatedTime = 
					        		Calendar.getInstance(TimeZone.getTimeZone(mileStone.getTimezone()));
					        updatedTime.setTime(mileStone.getUpdatedTime());
					        options.snippet(DateFormat.getInstance().format(updatedTime.getTime()));
					    }
					    
					    mMap.addMarker(options);
					    titleList.add(mileStone.getName() + " at " + mileStone.getLocation());
					    pointList.add(location);
					    
					    if (i==pos) {
					        CameraPosition camerapos = 
				        		new CameraPosition.Builder().target(location).zoom(15.5f).build();
					        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camerapos));
				 	        getActionBar().setTitle(mileStone.getName());
				 	    }
					}
					
				    PolylineOptions pOptions = new PolylineOptions();
				    pOptions.addAll(pointList);
				    pOptions.color(Color.RED);
				    pOptions.width(5);
				    mMap.addPolyline(pOptions);

					
					mNavigationDrawerFragment.updateListAdapter(titleList);
				}
				
				@Override
				public void onPublished(String postId) {}
				
			}, param);
				
			try {
			    msLoader.load();
			} catch (MileStoneLoaderException e) {
				Log.e(TAG, e.getMessage());
			}
			
		} else {
			MileStone ms = msLoader.getMileStones().get(position);
			MileStone.Venue venue = ms.getVenue();
			mMapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLng(new LatLng(venue.getLatitude(), venue.getLongitude())));
			mTitle = ms.getName();
		}
		
		// update the main content by replacing fragments
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.container, mMapFragment, MAP_FRAGMENT_TAG);
		fragmentTransaction.commit();
		
	}
	
//	public void onSectionAttached(int number) {
//		switch (number) {
//		case 1:
//			mTitle = getString(R.string.title_section1);
//			break;
//		case 2:
//			mTitle = getString(R.string.title_section2);
//			break;
//		case 3:
//			mTitle = getString(R.string.title_section3);
//			break;
//		}
//	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.map, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_add) {
			Intent intent = new Intent(this, AddMileStoneActivity.class);
			intent.putExtra(StoryDetailFragment.ARG_ITEM_NAME, diary);
			startActivityForResult(intent, 	ADD_MILESTONE_ACTIVITY);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ADD_MILESTONE_ACTIVITY) {
			publishEvent();
//			displaySelectedPlace(resultCode);
		}
		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			
			@Override
			public void onError(PendingCall pendingCall, Exception error, Bundle data) {
				Log.e(TAG, String.format("Error %s", error.toString()));
			}
			
			@Override
			public void onComplete(PendingCall pendingCall, Bundle data) {
				Log.i(TAG, "Success!");
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	};
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	};
	
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

    
	@Override
	protected void onStop() {
		super.onStop();
		uiHelper.onStop();
	}



//	private void displaySelectedPlace(int resultCode) {
//    	
//    	String results = "";
//        AddMileStoneApplication application = (AddMileStoneApplication) getApplication();
//
//        GraphPlace selection = application.getSelectedPlace();
//        
//        if (selection != null) {
//            GraphLocation location = selection.getLocation();
//
//            results = String.format("ID: %s\nName: %s\nCategory: %s\nLocation: (%f,%f)\nStreet: %s, %s, %s, %s, %s",
//                    selection.getId(),
//            		selection.getName(), selection.getCategory(),
//                    location.getLatitude(), location.getLongitude(),
//                    location.getStreet(), location.getCity(), location.getState(), location.getZip(),
//                    location.getCountry());
//            
//        } else {
//            results = "<No place selected>";
//        }
//
//        Log.d(TAG, results);
//
//    }
	
	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}
    
    public void publishEvent() {

        AddMileStoneApplication application = (AddMileStoneApplication) getApplication();
        GraphPlace selection = application.getSelectedPlace();
    	
    	Session session = Session.getActiveSession();
    	
	    if (session != null && selection != null) {
	    	List<String> PUBLISH_PERMISSIONS = 
	    			Arrays.asList(getResources().getStringArray(R.array.app_permissions_publish));
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PUBLISH_PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest permRequest =
						new Session.NewPermissionsRequest(this, PUBLISH_PERMISSIONS);
	    	    session.requestNewPublishPermissions(permRequest);
	    	    return;
			}
	    }
    	    	
    	String graphPath = "/" + this.getString(R.string.app_page_id)  +"/feed";
    	
		Bundle param = new Bundle();
		param.putString("message", "Test Msg");
		
		param.putString("place", selection.getId());
    	
//		msLoader = new MileStoneLoader(new MileStoneLoader.MileStoneLoaderCallback() {
//			
//			@Override
//			public void onPublished(String postId) {
//				addMilestone(postId);
//			}
//			
//			@Override
//			public void onLoaded(List<MileStone> milestones) {}
//		}, param, graphPath);
//			
//		try {
//			
//		    msLoader.publish();
//		    
//		} catch (MileStoneLoaderException e) {
//			Log.e(TAG, e.getMessage());
//		}
		
//		param.putString("from", this.getString(R.string.app_id));
//		param.putString("to", this.getString(R.string.app_page_id));
		
		if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			// publish the post using the Share Dialog
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
			.setPlace(selection.getId())
			.build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else {
			WebDialog dialog =
					new WebDialog.FeedDialogBuilder(this, session, param)
			.setOnCompleteListener(new OnCompleteListener() {
				
				@Override
				public void onComplete(Bundle values, FacebookException error) {
					// TODO Auto-generated method stub
					
				}
			})
			.build();
			dialog.show();
		}				
		
		// Clear selection
		application.setSelectedPlace(null);
    	
    }
    
    public void addMilestone(String id) {
		String path = "/milestones/";
		Bundle params = new Bundle();

		com.martymarron.traveldiaryapi.Diary.MileStone milestone = 
				new com.martymarron.traveldiaryapi.Diary.MileStone();
		milestone.setPageId(id);
		milestone.setDiary(diary.getId());

		Request<com.martymarron.traveldiaryapi.Diary.MileStone> request = 
				new Request<>(this, path, params, HttpMethod.POST, milestone,
				new Request.Callback<com.martymarron.traveldiaryapi.Diary.MileStone>() {

					@Override
					public void onLoadFinished(Response<com.martymarron.traveldiaryapi.Diary.MileStone> response) {
						com.martymarron.traveldiaryapi.Diary.MileStone data = response.getBody();
						Toast.makeText(MapActivity.this, "Created New Story\"" + data.getPageId() + "\"", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onLoaderReset(Loader<com.martymarron.traveldiaryapi.Diary.MileStone> loader) {
						// TODO Auto-generated method stub
						
					}
					
				}, com.martymarron.traveldiaryapi.Diary.MileStone.class);
		
		RequestAsyncTaskLoader<com.martymarron.traveldiaryapi.Diary.MileStone> asyncTaskLoader = 
				new RequestAsyncTaskLoader<com.martymarron.traveldiaryapi.Diary.MileStone>(request, loaderManager);
		asyncTaskLoader.execute();
    	
    }

}
