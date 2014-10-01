package com.martymarron.traveldiaryandroid;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.martymarron.traveldiaryandroid.milestone.MileStone;
import com.martymarron.traveldiaryandroid.milestone.MileStoneLoader;
import com.martymarron.traveldiaryandroid.milestone.MileStoneLoader.MileStoneLoaderCallback;
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

		// Set up the drawer.
//		sectionTitles.add("Section 1");
//		sectionTitles.add("Section 2");
//		sectionTitles.add("Section 3");
//		sectionTitles.add("Section 4");

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateView(android.view.View, java.lang.String, android.content.Context, android.util.AttributeSet)
	 */
	@Override
	public View onCreateView(View parent, String name, Context context,
			AttributeSet attrs) {
		Log.d(TAG, "MapActivity.onCreateView: " + name);
		// TODO Auto-generated method stub
		return super.onCreateView(parent, name, context, attrs);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		Log.d(TAG, "MapActivity.onNavigationDrawerItemSelected");
		
		mMapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
		if (mMapFragment == null) {
			mMapFragment = MapFragment.newInstance();			
		}		
		
		// update the main content by replacing fragments
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.container, mMapFragment, MAP_FRAGMENT_TAG);
//		fragmentTransaction.replace(R.id.container, PlaceholderFragment.newInstance(position + 1));
		fragmentTransaction.commit();
		
		if (mMapFragment != null) {
			MileStoneLoaderCallback callback = new MyMileStoneLoaderCallback();
			MileStoneLoader msLoader = new MileStoneLoader(callback);
			try {
			    msLoader.load();
			} catch (MileStoneLoaderException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
	}
	
	private class MyMileStoneLoaderCallback implements MileStoneLoader.MileStoneLoaderCallback {
		@Override
		public void onLoaded(List<MileStone> milestones) {
			// TODO Auto-generated method stub
			
			GoogleMap mMap = mMapFragment.getMap();
			List<String> titleList = new ArrayList<String>();
			
			for (MileStone mileStone : milestones) {
				
				LatLng location = new LatLng(mileStone.getLatitude(), mileStone.getLongitude());
		        MarkerOptions options = new MarkerOptions();
		        options.position(location);
		        options.title(mileStone.getLocation());
		        options.snippet(location.toString());

		        mMap.addMarker(options);
		        titleList.add(mileStone.getLocation());
		        
		        if (true) {
		 	        CameraPosition camerapos = new CameraPosition.Builder().target(location).zoom(15.5f).build();
		 	        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));		        	
		        }
			}
			
			mNavigationDrawerFragment.updateListAdapter(titleList);
			
		}
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(TAG, "MapActivity.onStart");
		
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

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
	
	
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//		
//		private static final String TAG = "PlaceholderFragment";
//		/**
//		 * The fragment argument representing the section number for this
//		 * fragment.
//		 */
//		private static final String ARG_SECTION_NUMBER = "section_number";
//
//		private MapView mView;
//		
//		private GoogleMap mMap;
//
//		/**
//		 * Returns a new instance of this fragment for the given section number.
//		 */
//		public static PlaceholderFragment newInstance(int sectionNumber) {
//			PlaceholderFragment fragment = new PlaceholderFragment();
//			Bundle args = new Bundle();
//			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//			fragment.setArguments(args);
//			return fragment;
//		}
//
//		public PlaceholderFragment() {
//		}
//		
//
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_map, container, false);
//			
////			mView = (MapView) rootView.findViewById(R.id.map);
//			mView = new MapView(getActivity());
//			mView.setEnabled(true);
//			mView.setClickable(true);
//			mView.onCreate(savedInstanceState);
//			setUpMapIfNeeded();
//
//			rootView.addView(mView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//
//			return rootView;
//		}
//
//		@Override
//		public void onAttach(Activity activity) {
//			super.onAttach(activity);
//			((MapActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
//		}
//		
//		
//		 @Override
//		public void onResume() {
//			super.onResume();
//			mView.onResume();
//			
//			setUpMapIfNeeded();
//		}
//
//		private void setUpMapIfNeeded() {
//            // Do a null check to confirm that we have not already instantiated the map.
//            if (mMap == null) {
//                // Try to obtain the map from the SupportMapFragment.
//                mMap = mView.getMap();
//
//                // Check if we were successful in obtaining the map.
//                if (mMap != null) {
//                    setUpMap();
//                }
//            }
//        }
//
//        private void setUpMap() {
//        	
//        	MapsInitializer.initialize(this.getActivity()); 
//        	 
//        	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
// 			mMap.getUiSettings().setMyLocationButtonEnabled(true);
// 			mMap.setMyLocationEnabled(false);
// 			 
// 	        LatLng location = new LatLng(35.681382, 139.766084);
// 	        CameraPosition camerapos = new CameraPosition.Builder().target(location).zoom(15.5f).build();
// 	        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));
// 	        
// 	        MarkerOptions options = new MarkerOptions();
// 	        options.position(location);
// 	        options.title("Tokyo Station");
// 	        options.snippet(location.toString());
//
//            mMap.addMarker(options);
//            
//            MileStone mileStone = new MileStone(getActivity(), mMap);
//        }
//		
//	}

}
