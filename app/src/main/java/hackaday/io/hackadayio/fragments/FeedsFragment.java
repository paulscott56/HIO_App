package hackaday.io.hackadayio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.R;
import hackaday.io.hackadayio.data.Feed;

/**
 * Created by paul on 2015/07/10.
 */
public class FeedsFragment extends Fragment {

    private static final String TAG = "Feeds";
    private TextView txtDisplay;
    private Context appContext;
    private List<Feed> feeds;
    private RequestQueue queue;
    private JSONObject data;
    private TextView tv;


    public FeedsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.feeds_fragment, container, false);
        tv = (TextView) rootView.findViewById(R.id.textView);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = getActivity().getApplicationContext();
        initializeData();
    }

    public static FeedsFragment newInstance() {
        FeedsFragment ff = new FeedsFragment();
        Bundle b = new Bundle();

        return ff;
    }

    public void initializeData() {
        feeds = new ArrayList<>();
        fetchFeeds(Constants.FEED_URL);
    }

    private JSONObject fetchFeeds(String url) {
        queue = Volley.newRequestQueue(appContext);
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            data = response;
                            try {
                                tv.setText(data.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, data.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

}
