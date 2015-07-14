package hackaday.io.hackadayio.syncadapters;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.data.ProjectContract;
import hackaday.io.hackadayio.data.ProjectParser;

/**
 * Created by paul on 2015/07/14.
 */
public class ProjectSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String PROJECTS_URL = Constants.PROJECT_URI + "?api_key=" + Constants.API_KEY;
    private int pageNumber = 1;
    private ContentResolver mContentResolver;
    private static String TAG = "projectSync";
    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
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

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_PROJECT_ID = 1;
    public static final int COLUMN_URL = 2;
    public static final int COLUMN_OWNER_ID = 3;
    public static final int COLUMN_NAME = 4;
    public static final int COLUMN_SUMMARY = 5;
    public static final int COLUMN_DESCRIPTION = 6;
    public static final int COLUMN_IMAGE_URL = 7;
    public static final int COLUMN_VIEWS = 8;
    public static final int COLUMN_COMMENTS = 9;
    public static final int COLUMN_FOLLOWERS = 10;
    public static final int COLUMN_SKULLS = 11;
    public static final int COLUMN_LOGS = 12;
    public static final int COLUMN_DETAILS = 13;
    public static final int COLUMN_INSTRUCTION = 14;
    public static final int COLUMN_COMPONENTS = 15;
    public static final int COLUMN_IMAGES = 16;
    public static final int COLUMN_CREATED = 17;
    public static final int COLUMN_UPDATED = 18;
    public static final int COLUMN_TAGS = 19;
    private JSONObject projectsjson;
    private RequestQueue queue;


    public ProjectSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public ProjectSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {

        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            final String location = Constants.PROJECT_SYNC_URI + "?api_key=" + Constants.API_KEY;
            JSONObject stream = null;
            Log.i(TAG, "Streaming data from network: " + location);
            stream = downloadUrl(location);
            updateLocalProjectData(stream, syncResult);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

        /**
     * Read JSON from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in project feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateLocalProjectData(final JSONObject stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        final ProjectParser projectParser = new ProjectParser();
        final ContentResolver contentResolver = getContext().getContentResolver();

        Log.i(TAG, "Parsing stream as JSON");
        final List<ProjectParser.Entry> entries = projectParser.parse(stream);
        Log.i(TAG, "Parsing complete. Found " + entries.size() + " entries");


        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming projects
        HashMap<Integer, ProjectParser.Entry> projectMap = new HashMap<Integer, ProjectParser.Entry>();
        for (ProjectParser.Entry e : entries) {
            projectMap.put(e.projectId, e);
        }

        // Get list of all items
        Log.i(TAG, "Fetching local projects for merge");
        Uri uri = ProjectContract.Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        int projectId;
        String url;
        int owner_id;
        String name;
        String summary;
        String description;
        String image_url;
        int views;
        int comments;
        int followers;
        int skulls;
        int logs;
        int details;
        int instruction;
        int components;
        int images;
        long created;
        int updated;
        String tags;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getInt(COLUMN_ID);
            projectId = c.getInt(COLUMN_PROJECT_ID);
            url = c.getString(COLUMN_URL);
            owner_id = c.getInt(COLUMN_OWNER_ID);
            name = c.getString(COLUMN_NAME);
            summary = c.getString(COLUMN_SUMMARY);
            description = c.getString(COLUMN_DESCRIPTION);
            image_url = c.getString(COLUMN_IMAGE_URL);
            views = c.getInt(COLUMN_VIEWS);
            comments = c.getInt(COLUMN_COMMENTS);
            followers = c.getInt(COLUMN_FOLLOWERS);
            skulls = c.getInt(COLUMN_SKULLS);
            logs = c.getInt(COLUMN_LOGS);
            details = c.getInt(COLUMN_DETAILS);
            instruction = c.getInt(COLUMN_INSTRUCTION);
            components = c.getInt(COLUMN_COMPONENTS);
            images = c.getInt(COLUMN_IMAGES);
            created = c.getLong(COLUMN_CREATED);
            updated = c.getInt(COLUMN_UPDATED);
            tags = c.getString(COLUMN_TAGS);

            ProjectParser.Entry match = projectMap.get(projectId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                projectMap.remove(projectId);
                // Check to see if the entry needs to be updated
                Uri existingUri = ProjectContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((match.name != null && !match.name.equals(name)) ||
                        (match.url != null && !match.url.equals(url)) ||
                        (match.created != created)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ProjectContract.Entry.COLUMN_PROJECT_ID, projectId)
                            .withValue(ProjectContract.Entry.COLUMN_URL, url)
                            .withValue(ProjectContract.Entry.COLUMN_OWNER_ID, owner_id)
                            .withValue(ProjectContract.Entry.COLUMN_NAME, name)
                            .withValue(ProjectContract.Entry.COLUMN_SUMMARY, summary)
                            .withValue(ProjectContract.Entry.COLUMN_DESCRIPTION, description)
                            .withValue(ProjectContract.Entry.COLUMN_IMAGE_URL, image_url)
                            .withValue(ProjectContract.Entry.COLUMN_VIEWS, views)
                            .withValue(ProjectContract.Entry.COLUMN_COMMENTS, comments)
                            .withValue(ProjectContract.Entry.COLUMN_FOLLOWERS, followers)
                            .withValue(ProjectContract.Entry.COLUMN_SKULLS, skulls)
                            .withValue(ProjectContract.Entry.COLUMN_LOGS, logs)
                            .withValue(ProjectContract.Entry.COLUMN_DETAILS, details)
                            .withValue(ProjectContract.Entry.COLUMN_INSTRUCTION, instruction)
                            .withValue(ProjectContract.Entry.COLUMN_COMPONENTS, components)
                            .withValue(ProjectContract.Entry.COLUMN_IMAGES, images)
                            .withValue(ProjectContract.Entry.COLUMN_CREATED, created)
                            .withValue(ProjectContract.Entry.COLUMN_UPDATED, updated)
                            .withValue(ProjectContract.Entry.COLUMN_TAGS, tags)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = ProjectContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (ProjectParser.Entry e : projectMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.id);
            batch.add(ContentProviderOperation.newInsert(ProjectContract.Entry.CONTENT_URI)
                    .withValue(ProjectContract.Entry.COLUMN_PROJECT_ID, e.projectId)
                    .withValue(ProjectContract.Entry.COLUMN_URL, e.url)
                    .withValue(ProjectContract.Entry.COLUMN_OWNER_ID, e.owner_id)
                    .withValue(ProjectContract.Entry.COLUMN_NAME, e.name)
                    .withValue(ProjectContract.Entry.COLUMN_SUMMARY, e.summary)
                    .withValue(ProjectContract.Entry.COLUMN_DESCRIPTION, e.description)
                    .withValue(ProjectContract.Entry.COLUMN_IMAGE_URL, e.image_url)
                    .withValue(ProjectContract.Entry.COLUMN_VIEWS, e.views)
                    .withValue(ProjectContract.Entry.COLUMN_COMMENTS, e.comments)
                    .withValue(ProjectContract.Entry.COLUMN_FOLLOWERS, e.followers)
                    .withValue(ProjectContract.Entry.COLUMN_SKULLS, e.skulls)
                    .withValue(ProjectContract.Entry.COLUMN_LOGS, e.logs)
                    .withValue(ProjectContract.Entry.COLUMN_DETAILS, e.details)
                    .withValue(ProjectContract.Entry.COLUMN_INSTRUCTION, e.instruction)
                    .withValue(ProjectContract.Entry.COLUMN_COMPONENTS, e.components)
                    .withValue(ProjectContract.Entry.COLUMN_IMAGES, e.images)
                    .withValue(ProjectContract.Entry.COLUMN_CREATED, e.created)
                    .withValue(ProjectContract.Entry.COLUMN_UPDATED, e.updated)
                    .withValue(ProjectContract.Entry.COLUMN_TAGS, e.tags)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(ProjectContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                ProjectContract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private JSONObject downloadUrl(final String url) throws IOException {
        queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest req = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, response.toString(4));
                            projectsjson = response;
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
        return projectsjson;
    }



}
