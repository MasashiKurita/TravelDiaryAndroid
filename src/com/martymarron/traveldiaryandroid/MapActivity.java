package com.martymarron.traveldiaryandroid;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Log.d(TAG, "MapActivity.onNavigationDrawerItemSelected");
		
		mMapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
		if (mMapFragment == null) {
			mMapFragment = MapFragment.newInstance();
			
			// load and initialize map if first fragment created
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
					    Calendar updatedTime = 
					    		Calendar.getInstance(TimeZone.getTimeZone(mileStone.getTimezone()));
					    updatedTime.setTime(mileStone.getUpdatedTime());
					    options.snippet(DateFormat.getInstance().format(updatedTime.getTime()));
					    
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
			});
				
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
		return super.onOptionsItemSelected(item);
	}
	
}
