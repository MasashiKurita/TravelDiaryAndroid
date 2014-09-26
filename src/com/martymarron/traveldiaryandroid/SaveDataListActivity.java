package com.martymarron.traveldiaryandroid;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;

/**
 * An activity representing a list of SaveDatas. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link SaveDataDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SaveDataListFragment} and the item details (if present) is a
 * {@link SaveDataDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SaveDataListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class SaveDataListActivity extends FragmentActivity implements
		SaveDataListFragment.Callbacks {
	
	private static final String TAG = "SaveDataList";
	
	private static final List<String> PERMISSIONS = Arrays.asList("user_activities", "user_events", "user_location", "user_status");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";

	private UiLifecycleHelper uiHelper;
	
	private boolean pendingPublishReauthorization = false;
	
	private Session.StatusCallback sessionStateCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");
			if (pendingPublishReauthorization
		     && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
			}
		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
		}
	}
	
	private void loginFacebook() {
		
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this)
			.setPermissions(PERMISSIONS)
			.setCallback(sessionStateCallback));
		} else {
			Session.openActiveSession(this, true, sessionStateCallback);
		}		
	}
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_savedata_list);

		if (findViewById(R.id.savedata_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SaveDataListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.savedata_list))
					.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
		
		uiHelper = new UiLifecycleHelper(this, sessionStateCallback);
		uiHelper.onCreate(savedInstanceState);

		loginFacebook();
		
	}

	/**
	 * Callback method from {@link SaveDataListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(SaveDataDetailFragment.ARG_ITEM_ID, id);
			SaveDataDetailFragment fragment = new SaveDataDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.savedata_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			if (id.equals("0")) {

				Intent initiationIntent = new Intent(this, InitiationActivity.class);
			    startActivity(initiationIntent);
			} else {
				
			    Intent detailIntent = new Intent(this, SaveDataDetailActivity.class);
			    detailIntent.putExtra(SaveDataDetailFragment.ARG_ITEM_ID, id);
			    startActivity(detailIntent);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

}
