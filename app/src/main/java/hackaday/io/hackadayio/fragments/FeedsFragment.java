package hackaday.io.hackadayio.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.R;
import hackaday.io.hackadayio.data.FeedItem;
import hackaday.io.hackadayio.data.FeedsArrayAdapter;
import hackaday.io.hackadayio.data.InfiniteScrollListener;
import hackaday.io.hackadayio.tasks.ImageLoaderTask;

/**
 * Created by paul on 2015/07/10.
 */
public class FeedsFragment extends ListFragment {

    private static final String TAG = "ListFrag";
    private int pageNumber = 1;
    private List<FeedItem> mItems;
    private ArrayList<FeedItem> feeds;
    private Context appContext;
    private RequestQueue queue;
    private JSONObject data;
    private String pname;
    private FeedsArrayAdapter adapter;
    private ProgressBar spinner;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static FeedsFragment newInstance() {
        FeedsFragment ff = new FeedsFragment();
        Bundle b = new Bundle();

        return ff;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.appContext = getActivity().getApplicationContext();
        // initialize the items list
        mItems = new ArrayList<FeedItem>();
        Resources resources = getResources();
        initializeData();

        // initialize and set the list adapter
        adapter = new FeedsArrayAdapter(getActivity(), mItems);
        setListAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeds_fragment, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 1;
                initializeData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
        getListView().setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mSwipeRefreshLayout.setRefreshing(true);
                pageNumber += 1;
                initializeData();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        FeedItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.id, Toast.LENGTH_SHORT).show();
    }

    public void initializeData() {
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

                            for (int i = 0; i < feedsdataArray.length(); i++) {
                                JSONObject itemdata = feedsdataArray.getJSONObject(i);
                                String id = itemdata.get("id").toString();
                                String type = itemdata.getString("type");
                                String projectId = itemdata.getString("project_id");

                                String projectName = getProjectName(projectId);

                                String user2Id = itemdata.getString("user2_id");
                                String postId = itemdata.getString("post_id");
                                String userId = itemdata.getString("user_id");

                                String updateType = determineType(type);

                                JSONObject content = new JSONObject(itemdata.getString("json"));
                                String summary = null;
                                try {
                                    JSONObject extra = new JSONObject(content.getString("extra"));
                                    summary = extra.getString("summary");
                                } catch (JSONException j) {
                                    // do nothing, it just doesn't exist
                                }

                                JSONObject user = new JSONObject(content.getString("user"));
                                String screen_name = user.getString("screen_name");
                                String avatar_url = user.getString("avatar_url");
                                // check for default img
                                if ("//gravatar.com".equalsIgnoreCase(avatar_url.substring(0, 14))) {
                                    avatar_url = "https://hackaday.io/img/default-avatar.png";
                                }
                                int user_id = Integer.valueOf(user.getString("id"));
                                // decode the image to a drawable bitmap...
                                AsyncTask<String, Void, Bitmap> imgData = new ImageLoaderTask(appContext).execute(avatar_url);
                                try {
                                    Bitmap bmp = imgData.get();
                                    adapter.add(new FeedItem(id, screen_name, updateType, projectName, user2Id, postId, summary, userId, bmp));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spinner.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        queue.add(req);
        return data;
    }

    private String getProjectName(String projectId) {
        JsonObjectRequest req = new JsonObjectRequest(Constants.PROJECT_URI + projectId + "?api_key=" + Constants.API_KEY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            pname = response.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        queue.add(req);
        return pname;
    }

    private String determineType(String type) {
        switch (type) {

            case "updateprofile":
                return getString(R.string.updateprofile);


            case "projectFollow":
                return getString(R.string.projectFollow);

            case "projectSkull":
                return getString(R.string.projectSkull);

            case "newlog":
                return getString(R.string.newlog);

            case "contributorsAdded":
                return getString(R.string.contributorsAdded);

            case "updatedetails":
                return getString(R.string.updatedetails);

            case "discussionsComment":
                return getString(R.string.discussionsComment);

            case "discussionsCommentReply":
                return getString(R.string.discussionsCommentReply);

            case "projectnew":
                return getString(R.string.projectnew);

            case "projectupdate":
                return getString(R.string.projectupdate);


            case "listFollow":
                return getString(R.string.listFollow);

            case "eventFollow":
                return getString(R.string.eventFollow);

            default:
                return type;
        }
    }

    public void loadMoreDataFromApi(int page) {

        pageNumber += 1;
        Log.i(TAG, "Fetching more... " + pageNumber);
        fetchFeeds(Constants.FEED_URL + pageNumber);
    }


    public interface OnFragmentInteractionListener {
    }
}
