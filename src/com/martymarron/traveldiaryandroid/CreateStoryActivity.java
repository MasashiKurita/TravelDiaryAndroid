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

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;
import com.martymarron.traveldiaryapi.Response;

public class CreateStoryActivity extends Activity {

	private static final String TAG = "CreateStoryActivity";
	
	private String userId;
	
	private RequestAsyncTaskLoader<Diary> asyncTaskLoader;
	
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback sessionStatusCallback = new Session.StatusCallback() {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session.isOpened()) {
			
		    // make request to the /me API
		    Log.d(TAG, "make request to the /me API");
		    com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {
			
			    // call back after Graph API response with user object
			    @Override
		 	    public void onCompleted(GraphUser user, com.facebook.Response response) {
				    Log.d(TAG, "newMeRequest#onCompleted");
				    if (user != null) {
					    CreateStoryActivity.this.userId = user.getId();
				    }	
			    }
		    }).executeAsync();
	    }
	}
	
	private void initLoader() {

		String path = "/diaries/";
		Bundle params = new Bundle();
		Diary diary = new Diary();
		diary.setUserId(userId);
		diary.setTitle(((TextView)findViewById(R.id.storyTitleText)).getText().toString());
		diary.setDescription(((TextView)findViewById(R.id.storyDescriptionText)).getText().toString());
		Request<Diary> request = 
				new Request<>(this, path, params, HttpMethod.POST, diary,
				new Request.Callback<Diary>() {

					@Override
					public void onLoadFinished(Response<Diary> response) {
						Diary data = response.getBody();
						Toast.makeText(CreateStoryActivity.this, "Created New Story\"" + data.getTitle() + "\"", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(CreateStoryActivity.this, StoryDetailActivity.class);
						intent.putExtra(StoryDetailFragment.ARG_ITEM_ID, String.valueOf(data.getId()));
						startActivity(intent);
					}

					@Override
					public void onLoaderReset(Loader<Diary> loader) {
						// TODO Auto-generated method stub
						
					}
					
				}, Diary.class);
		
		asyncTaskLoader = 
				new RequestAsyncTaskLoader<Diary>(request, getLoaderManager());
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);
		
		
        uiHelper = new UiLifecycleHelper(this, sessionStatusCallback);
        uiHelper.onCreate(savedInstanceState);
        
        initLoader();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_story, menu);
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
	
	public void initStory(View view) {

		asyncTaskLoader.execute();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		uiHelper.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
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
			View rootView = inflater.inflate(R.layout.fragment_create_story,
					container, false);
			return rootView;
		}
	}
}
