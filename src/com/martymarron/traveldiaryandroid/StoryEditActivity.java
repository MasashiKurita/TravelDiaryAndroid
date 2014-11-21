package com.martymarron.traveldiaryandroid;

import org.springframework.http.HttpMethod;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;
import com.martymarron.traveldiaryapi.Response;

public class StoryEditActivity extends Activity {
	
	private static final String TAG = "StoryEditActivity";
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	private String diaryId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story_edit);
		if (savedInstanceState == null) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			
			if (getIntent().getSerializableExtra(StoryDetailFragment.ARG_ITEM_NAME) instanceof Diary) {
			    Bundle arguments = new Bundle();
			    Diary diary = (Diary)getIntent().getSerializableExtra(StoryDetailFragment.ARG_ITEM_NAME);
			    arguments.putSerializable(StoryDetailFragment.ARG_ITEM_NAME, diary);
			    fragment.setArguments(arguments);
			    
			    diaryId = String.valueOf(diary.getId());
			}
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
	
	public void saveStory(View view) {
		Toast.makeText(this, "saveStory", Toast.LENGTH_LONG).show();
		
		String path = "/diaries/" + diaryId + "/";
		Bundle params = new Bundle();
		Diary diary = new Diary();
		diary.setTitle(((TextView)findViewById(R.id.storyTitleText)).getText().toString());
		diary.setDescription(((TextView)findViewById(R.id.storyDescriptionText)).getText().toString());
		Request<Diary> request = 
				new Request<>(this, path, params, HttpMethod.PUT, diary,
				new Request.Callback<Diary>() {

					@Override
					public void onLoadFinished(Response<Diary> response) {
						Diary data = response.getBody();
						if (data != null) {
						    Log.d(TAG, "Story saved: diary_id=" + diaryId);
						    Intent intent = new Intent(StoryEditActivity.this, StoryDetailActivity.class);
						    intent.putExtra(StoryDetailFragment.ARG_ITEM_ID, diaryId);
						    startActivity(intent);
						} else {
					        Toast.makeText(StoryEditActivity.this, "Falied to save Story due to \"" + response.getStatusCode().toString() + "\"", Toast.LENGTH_LONG).show();
					
						}
					}

					@Override
					public void onLoaderReset(Loader<Diary> loader) {
						// TODO Auto-generated method stub
						
					}

				}, Diary.class);
		
		RequestAsyncTaskLoader<Diary> asyncTaskLoader = 
				new RequestAsyncTaskLoader<Diary>(request);
		asyncTaskLoader.execute(getLoaderManager());
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_story_edit, container, false);
			
			if (getArguments().containsKey(StoryDetailFragment.ARG_ITEM_NAME)) {
				// Load the dummy content specified by the fragment
				// arguments. In a real-world scenario, use a Loader
				// to load content from a content provider.
				if (getArguments().getSerializable(StoryDetailFragment.ARG_ITEM_NAME) instanceof Diary) {
				    Diary diary = (Diary)getArguments().getSerializable(StoryDetailFragment.ARG_ITEM_NAME);
				    ((TextView)rootView.findViewById(R.id.storyTitleText)).setText(diary.getTitle());
				    ((TextView)rootView.findViewById(R.id.storyDescriptionText)).setText(diary.getDescription());
				}
			}

			return rootView;
		}
	}
}
