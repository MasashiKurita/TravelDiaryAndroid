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

import com.martymarron.traveldiaryandroid.dummy.DummyContent;
import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;

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
	private DummyContent.DummyItem mItem;
	
	private Diary diary;
	
	public static final String ARG_ITEM_NAME = "item_name";


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
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
			String path = "/diaries/" + getArguments().getString(ARG_ITEM_ID) + "/";
			Request<Diary> request = new Request<Diary>(getActivity(), path, null, HttpMethod.GET, null,
				new Request.Callback<Diary>() {

					@Override
					public void onLoadFinished(Loader<Diary> loader, Diary data) {
						if (data != null) {
						    Log.d(TAG, data.toString());
						    ((TextView) getView().findViewById(R.id.story_detail))
						    .setText(data.toString());
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_story_detail,
				container, false);

		// Show the dummy content as text in a TextView.
//		if (mItem != null) {
//			((TextView) rootView.findViewById(R.id.story_detail))
//					.setText(mItem.content);
//		}

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.story_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		int id = item.getItemId();
		
		Log.d(TAG, "onOptionsItemSelected: item=" + item.getTitle());

		if (id == R.id.item_edit_story) {
			Intent intent = new Intent(getActivity(), StoryEditActivity.class);
			intent.putExtra(ARG_ITEM_NAME, diary);
			startActivity(intent);
		}
		
		if (id == R.id.item_add_milestone) {
			// TODO startActivity(): milestone
		}
		
		if (id == R.id.item_delete_story) {
			// TODO startActivity(): delete story
		}

		return super.onOptionsItemSelected(item);
	}

}
