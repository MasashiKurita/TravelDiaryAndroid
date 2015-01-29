package com.martymarron.traveldiaryandroid;

import org.springframework.http.HttpMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.martymarron.traveldiaryapi.Diary;
import com.martymarron.traveldiaryapi.Request;
import com.martymarron.traveldiaryapi.RequestAsyncTaskLoader;
import com.martymarron.traveldiaryapi.Response;

/**
 * A list fragment representing a list of Stories. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link StoryDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class StoryListFragment extends ListFragment {
		
	private static final String TAG = "StoryListFragment";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	
//	private ListView rootView;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoryListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		initStories();
	}
	
	private void initStories() {
		Request.Callback<Diary[]> apiCallback = new Request.Callback<Diary[]>() {

			@Override
			public void onLoadFinished(Response<Diary[]> response) {
				Diary[] data = response.getBody();

                setListAdapter(new ArrayAdapter<Diary>(
                		getActivity(),
                		R.layout.view_story_list,
                		R.id.text1,
                		data) {

							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								
								Button deleteButton = (Button)view.findViewById(R.id.button1);
								if (deleteButton != null) {
									deleteButton.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											deleteStory(position);
										}
									});
								}
								return view;
							}});
        		
			}

			@Override
			public void onLoaderReset(Loader<Diary[]> loader) {
				// TODO Auto-generated method stub
			}
		};
		
		
		Request<Diary[]> request = 
				new Request<Diary[]>(getActivity(), "/diaries/", null, HttpMethod.GET, null, apiCallback, Diary[].class);
		RequestAsyncTaskLoader<Diary[]> requestAsyncTaskLoader = 
				new RequestAsyncTaskLoader<Diary[]>(request, getLoaderManager());
		requestAsyncTaskLoader.execute();		
	}
	
	private void deleteStory(int position) {
		
		Log.d(TAG, "position="+String.valueOf(position));

		final Diary diary = (Diary)getListAdapter().getItem(position);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = String.format(getResources().getString(R.string.message_delete_confirmation), diary.getTitle());
		builder.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Request<Diary> request = 
						new Request<Diary>(
								getActivity(), 
								"/diaries/" + String.valueOf(diary.getId()) + "/", 
								null, 
								HttpMethod.DELETE, 
								null, 
								new Request.Callback<Diary>() {
									
									@Override
									public void onLoaderReset(
											Loader<Diary> loader) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onLoadFinished(
											Response<Diary> response) {
										Diary data = response.getBody();
										if (data != null) {
										    Log.d(TAG, data.toString());
										} else {
											Log.d(TAG, response.getStatusCode().toString()+", "+response.getHeaders().toString());
										}
										
										initStories();
									}
									
								}, Diary.class);
				RequestAsyncTaskLoader<Diary> requestAsyncTaskLoader = 
						new RequestAsyncTaskLoader<Diary>(request, getLoaderManager());
				requestAsyncTaskLoader.execute();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_story_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
//		mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
		Log.d(TAG, "onListItemClick: position=" + String.valueOf(position) + ", id=" + String.valueOf(id));
		Log.d(TAG, getListAdapter().getItem(position).toString());
		Diary diary = (Diary)getListAdapter().getItem(position);
		mCallbacks.onItemSelected(String.valueOf(diary.getId()));
		//mCallbacks.onItemSelected(DummyStory.ITEMS.get(position).id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
	
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
}
