package com.martymarron.traveldiaryandroid;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martymarron.traveldiaryapi.Diary;

public class StoryEditActivity extends Activity {
	
	private static final String TAG = "StoryEditActivity";
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story_edit);
		if (savedInstanceState == null) {
			Diary diary = (Diary)getIntent().getSerializableExtra(StoryDetailFragment.ARG_ITEM_NAME);
			Log.d(TAG, diary.toString());
			Bundle arguments = new Bundle();
			arguments.putSerializable(StoryDetailFragment.ARG_ITEM_NAME, diary);
			PlaceholderFragment fragment = new PlaceholderFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.story_edit, menu);
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
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_story_edit,
					container, false);
			if (getArguments().containsKey(StoryDetailFragment.ARG_ITEM_NAME)) {
				// Load the dummy content specified by the fragment
				// arguments. In a real-world scenario, use a Loader
				// to load content from a content provider.
				Diary diary = (Diary)getArguments().getSerializable(StoryDetailFragment.ARG_ITEM_NAME);
				((TextView)rootView.findViewById(R.id.storyTitleText)).setText(diary.getTitle());
				((TextView)rootView.findViewById(R.id.storyDescriptionText)).setText(diary.getDescription());
			}

			return rootView;
		}
	}
}
