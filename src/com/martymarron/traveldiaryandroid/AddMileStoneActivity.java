package com.martymarron.traveldiaryandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.FacebookSdk;

public class AddMileStoneActivity extends FragmentActivity {
	
//	PlacePickerFragment placePickerFragment;

	private static final String PICKER_FRAGMENT_TAG = "picker";
	
	private static final String TAG = "AddMilestoneActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_add_milestone);
		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
		}

//		placePickerFragment = (PlacePickerFragment) getSupportFragmentManager().findFragmentByTag(PICKER_FRAGMENT_TAG);
//		if (placePickerFragment == null) {
//			placePickerFragment = new PlacePickerFragment();
//		     placePickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
//		            @Override
//		            public void onSelectionChanged(PickerFragment<?> fragment) {
//		                if (placePickerFragment.getSelection() != null) {
//		                    finishActivity();
//		                }
//		            }
//		        });
//		        placePickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
//		            @Override
//		            public void onDoneButtonClicked(PickerFragment<?> fragment) {
//		                finishActivity();
//		            }
//		        });
//		}
//		placePickerFragment.setFilter(new GraphObjectFilter<GraphPlace>() {

//			@Override
//			public boolean includeItem(GraphPlace graphObject) {
//				return true;
//			}
//		});
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//		fragmentTransaction.replace(R.id.container, placePickerFragment, PICKER_FRAGMENT_TAG);
		fragmentTransaction.commit();
		
	}

	private void finishActivity() {
		Log.d(TAG, getApplication().toString());
		AddMileStoneApplication application = (AddMileStoneApplication) getApplication();
//		application.setSelectedPlace(placePickerFragment.getSelection());
		
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_milestone, menu);
		return true;
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_add_milestone,
					container, false);
			return rootView;
		}
	}
	
    private void onError(Exception error) {
        String text = error.getMessage();
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
        
        Log.e(TAG, text);
        
    }

	@Override
	protected void onStart() {
		super.onStart();
//		try {
//			placePickerFragment.loadData(false);
//		} catch (Exception e) {
//			onError(e);
//		}
	}
	
}
