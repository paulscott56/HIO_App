package hackaday.io.hackadayio.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.R;
import hackaday.io.hackadayio.data.Feed;
import hackaday.io.hackadayio.data.FeedContent;
import hackaday.io.hackadayio.data.FeedItem;
import hackaday.io.hackadayio.data.FeedsArrayAdapter;
import hackaday.io.hackadayio.data.InfiniteScrollListener;

/**
 * Created by paul on 2015/07/10.
 */
public class FeedsFragment extends Fragment {

    private static final String TAG = "Feeds";
    private static final int REFRESH_ID = 1;
    private TextView txtDisplay;
    private Context appContext;
    private List<Feed> feeds;
    private RequestQueue queue;
    private JSONObject data;
    ListView lvItems;
    private int pageNumber = 1;

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    FeedsArrayAdapter adapter;
    ArrayList<FeedItem> feedItems;
    ListView listView;

    public FeedsFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadMoreDataFromApi(page);
            }
        });


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getActivity().getApplicationContext();
        initializeData();
        mAdapter = new ArrayAdapter<FeedItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, FeedContent.ITEMS);

        feedItems = new ArrayList<>();
        adapter = new FeedsArrayAdapter(getActivity().getApplicationContext(), feedItems);

        listView = new ListView(getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        getActivity().setContentView(listView);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, REFRESH_ID, 0, "refresh");
        return true;

    }

    public static FeedsFragment newInstance() {
        FeedsFragment ff = new FeedsFragment();
        Bundle b = new Bundle();

        return ff;
    }

    public void initializeData() {
        feeds = new ArrayList<>();
        fetchFeeds(Constants.FEED_URL + pageNumber);
    }

    private JSONObject fetchFeeds(String url) {
        queue = Volley.newRequestQueue(appContext);
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //VolleyLog.v("Response:%n %s", response.toString(4));
                        data = response;
                        try {
                            pageNumber = Integer.valueOf(data.get("page").toString());
                            JSONArray feedsdataArray = data.getJSONArray("feeds");

                            for(int i = 0 ; i < feedsdataArray.length(); i++) {
                                JSONObject itemdata = feedsdataArray.getJSONObject(i);
                                String id = itemdata.get("id").toString();
                                JSONObject content = new JSONObject(itemdata.getString("json"));
                                FeedContent.ITEMS.add(new FeedItem(id, content));
                            }

                            // Log.i(TAG, data.get("feeds").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i(TAG, data.toString(4));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Log.i(TAG, error.toString());
            }
        });
        queue.add(req);
        return data;
    }

    public void loadMoreDataFromApi(int page) {

        pageNumber += 1;
        Log.i(TAG, "Fetching more... " + pageNumber);
        fetchFeeds(Constants.FEED_URL + pageNumber);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FeedsFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public void onNewFeedItems(ArrayList<FeedItem> feedItems) {
        adapter.addAll(feedItems);
        adapter.notifyDataSetChanged();
    }
}
