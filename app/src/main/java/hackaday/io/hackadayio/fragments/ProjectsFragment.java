package hackaday.io.hackadayio.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.R;
import hackaday.io.hackadayio.data.ProjectContract;
import hackaday.io.hackadayio.data.ProjectItem;
import hackaday.io.hackadayio.data.ProjectsArrayAdapter;
import hackaday.io.hackadayio.tasks.ImageLoaderTask;

/**
 * Created by paul on 2015/07/10.
 */
public class ProjectsFragment extends ListFragment {

    private static final String TAG = "ProjectsFrag";
    private static final String[] PROJECTION = new String[]{
            ProjectContract.Entry._ID,
            ProjectContract.Entry.COLUMN_PROJECT_ID,
            ProjectContract.Entry.COLUMN_URL,
            ProjectContract.Entry.COLUMN_OWNER_ID,
            ProjectContract.Entry.COLUMN_NAME,
            ProjectContract.Entry.COLUMN_SUMMARY,
            ProjectContract.Entry.COLUMN_DESCRIPTION,
            ProjectContract.Entry.COLUMN_IMAGE_URL,
            ProjectContract.Entry.COLUMN_VIEWS,
            ProjectContract.Entry.COLUMN_COMMENTS,
            ProjectContract.Entry.COLUMN_FOLLOWERS,
            ProjectContract.Entry.COLUMN_SKULLS,
            ProjectContract.Entry.COLUMN_LOGS,
            ProjectContract.Entry.COLUMN_DETAILS,
            ProjectContract.Entry.COLUMN_INSTRUCTION,
            ProjectContract.Entry.COLUMN_COMPONENTS,
            ProjectContract.Entry.COLUMN_IMAGES,
            ProjectContract.Entry.COLUMN_CREATED,
            ProjectContract.Entry.COLUMN_UPDATED,
            ProjectContract.Entry.COLUMN_TAGS,
    };

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = Constants.CONTENT_AUTHORITY;

    public static final Uri PROJECT_CONTENT_URI =
            Uri.parse("content://" + Constants.AUTHORITY + "/projects");

    /**
     * Path component for "entry"-type resources..
     */
    public static final String PATH_ENTRIES = "projects";

    SwipeRefreshLayout mSwipeRefreshLayout;
    private int pageNumber = 1;
    private List<ProjectItem> mItems;
    private ArrayList<ProjectItem> feeds;
    private Context appContext;
    private RequestQueue queue;
    private JSONObject data;
    private String pname;
    private ProjectsArrayAdapter adapter;
    private ProgressBar spinner;
    private ContentResolver mContentResolver;

    public ProjectsFragment() {
    }

    public static ProjectsFragment newInstance() {

        ProjectsFragment prf = new ProjectsFragment();
        Bundle b = new Bundle();

        return prf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = getActivity().getApplicationContext();
        mContentResolver = getActivity().getContentResolver();
        // initialize the items list
        mItems = new ArrayList<ProjectItem>();
        // initialize and set the list adapter
        adapter = new ProjectsArrayAdapter(getActivity(), mItems);
        setListAdapter(adapter);

        Resources resources = getResources();
        initializeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragment, container, false);
        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNumber = 1;
                initializeData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });*/

        spinner = (ProgressBar) view.findViewById(R.id.progressBar1);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        //getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ProjectItem item = mItems.get(position);
        // do something
        Toast.makeText(getActivity(), item.id, Toast.LENGTH_SHORT).show();
    }

    public void initializeData() {
        fetchProjects();
    }

    private void fetchProjects() {
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor =
                resolver.query(PROJECT_CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null);
        Toast.makeText(getActivity(), "Total projects to display " + cursor.getCount(), Toast.LENGTH_LONG).show();
        if (cursor.moveToFirst()) {
            do {
                String avatar_url = cursor.getString(7);
                AsyncTask<String, Void, Bitmap> imgData = new ImageLoaderTask(appContext).execute(avatar_url);
                try {
                    Bitmap bmp = imgData.get();
                    int id = cursor.getInt(0);
                    String name = cursor.getString(4);
                    String summary = cursor.getString(5);
                    // do something meaningful
                    ProjectItem item = new ProjectItem();
                    item.setId(id);
                    item.setName(name);
                    item.setAvatar(bmp);
                    item.setSummary(summary);
                    adapter.add(item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
    }
}