package com.martymarron.traveldiaryandroid;

import org.springframework.http.HttpMethod;

import android.app.Fragment;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.model.GraphLocation;
import com.facebook.model.GraphPlace;
import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;
import com.martymarron.traveldiaryapi.Response;

/**
 * A fragment representing a single Story detail screen. This fragment is either
 * contained in a {@link StoryListActivity} in two-pane mode (on tablets) or a
 * {@link StoryDetailActivity} on handsets.
 */
public class StoryDetailFragment extends Fragment {
	
	private static final String TAG = "StoryDetailFragment";
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	
	private Diary diary;
	
	public static final String ARG_ITEM_NAME = "item_name";
	
	private static final int ADD_MILESTONE_ACTIVITY = 1;


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoryDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			String path = "/diaries/" + getArguments().getString(ARG_ITEM_ID) + "/";
			Request<Diary> request = new Request<Diary>(getActivity(), path, null, HttpMethod.GET, null,
				new Request.Callback<Diary>() {

					@Override
					public void onLoadFinished(Response<Diary> response) {
						Diary data = response.getBody();
						if (data != null) {
						    Log.d(TAG, data.toString());
						    ((TextView) getView().findViewById(R.id.story_detail)).setText(data.toString());
						    diary = data;
						}

					}

					@Override
					public void onLoaderReset(Loader<Diary> loader) {
						// TODO Auto-generated method stub
						
					}
				}, Diary.class);
			RequestAsyncTaskLoader<Diary> asyncTaskLoader =
					new RequestAsyncTaskLoader<>(request);
			asyncTaskLoader.execute(getLoaderManager());
		}
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_story_detail,
				container, false);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.story_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		Log.d(TAG, "onOptionsItemSelected: item=" + item.getTitle());

		if (id == R.id.item_edit_story) {
			Intent intent = new Intent(getActivity(), StoryEditActivity.class);
			intent.putExtra(ARG_ITEM_NAME, diary);
			startActivity(intent);
		}
		
		if (id == R.id.item_add_milestone) {

			Intent intent = new Intent(getActivity(), AddMileStoneActivity.class);
			intent.putExtra(ARG_ITEM_NAME, diary);
			startActivityForResult(intent, ADD_MILESTONE_ACTIVITY);
		}
		
		if (id == R.id.item_delete_story) {
			// TODO startActivity(): delete story
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ADD_MILESTONE_ACTIVITY) {
			displaySelectedPlace(resultCode);
		}
	}
	
    private void displaySelectedPlace(int resultCode) {
        String results = "";
        AddMileStoneApplication application = (AddMileStoneApplication) getActivity().getApplication();

        GraphPlace selection = application.getSelectedPlace();
        if (selection != null) {
            GraphLocation location = selection.getLocation();

            results = String.format("Name: %s\nCategory: %s\nLocation: (%f,%f)\nStreet: %s, %s, %s, %s, %s",
                    selection.getName(), selection.getCategory(),
                    location.getLatitude(), location.getLongitude(),
                    location.getStreet(), location.getCity(), location.getState(), location.getZip(),
                    location.getCountry());
        } else {
            results = "<No place selected>";
        }

        Log.d(TAG, results);

    }


}
