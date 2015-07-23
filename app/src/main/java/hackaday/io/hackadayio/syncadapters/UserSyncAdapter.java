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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import hackaday.io.hackadayio.Constants;
import hackaday.io.hackadayio.data.UserContract;
import hackaday.io.hackadayio.data.UserParser;

/**
 * Created by paul on 2015/07/23.
 */
public class UserSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String USERS_URL = Constants.USERS_URI + "?api_key=" + Constants.API_KEY;
    private int pageNumber = 1;
    private ContentResolver mContentResolver;
    private static String TAG = "UserSync";
    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    private int userPageNum = 1;
    private int userLastPage;



    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
            UserContract.Entry._ID,
            UserContract.Entry.COLUMN_USER_ID,
            UserContract.Entry.COLUMN_URL,
            UserContract.Entry.COLUMN_USERNAME,
            UserContract.Entry.COLUMN_SCREEN_NAME,
            UserContract.Entry.COLUMN_RANK,
            UserContract.Entry.COLUMN_IMAGE_URL,
            UserContract.Entry.COLUMN_FOLLOWERS,
            UserContract.Entry.COLUMN_FOLLOWING,
            UserContract.Entry.COLUMN_PROJECTS,
            UserContract.Entry.COLUMN_SKULLS,
            UserContract.Entry.COLUMN_LOCATION,
            UserContract.Entry.COLUMN_ABOUT_ME,
            UserContract.Entry.COLUMN_WHO_AM_I,
            UserContract.Entry.COLUMN_WHAT_I_HAVE_DONE,
            UserContract.Entry.COLUMN_WHAT_I_WOULD_LIKE_TO_DO,
            UserContract.Entry.COLUMN_CREATED,
            UserContract.Entry.COLUMN_TAGS,
    };

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_USER_ID = 1;
    public static final int COLUMN_URL = 2;
    public static final int COLUMN_USERNAME = 3;
    public static final int COLUMN_SCREEN_NAME = 4;
    public static final int COLUMN_RANK = 5;
    public static final int COLUMN_IMAGE_URL = 6;
    public static final int COLUMN_FOLLOWERS = 7;
    public static final int COLUMN_FOLLOWING = 8;
    public static final int COLUMN_PROJECTS = 9;
    public static final int COLUMN_SKULLS = 10;
    public static final int COLUMN_LOCATION = 11;
    public static final int COLUMN_ABOUT_ME = 12;
    public static final int COLUMN_WHO_AM_I = 13;
    public static final int COLUMN_WHAT_I_HAVE_DONE = 14;
    public static final int COLUMN_WHAT_I_WOULD_LIKE_TO_DO = 15;
    public static final int COLUMN_CREATED = 16;
    public static final int COLUMN_TAGS = 17;

    private JSONObject usersjson;
    private RequestQueue queue;

    public UserSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public UserSyncAdapter(
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
            int pages = getUserPageNum(Constants.USERS_SYNC_URI + "?api_key=" + Constants.API_KEY);
            // loop through all pages??? ugh!
            for( int projectPageNum = 1; projectPageNum <= pages; projectPageNum++) {
                final String location = Constants.USERS_SYNC_URI + "?api_key=" + Constants.API_KEY + "&page=" + projectPageNum;

                Log.i(TAG, "Streaming datapage " + userPageNum + " from network: " + location);
                JSONObject stream = downloadUrl(location);
                if(! stream.isNull("projects")) {
                    updateLocalUserData(stream, syncResult);
                }

            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "User URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing user: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing user: " + e.toString());
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
    public void updateLocalUserData(final JSONObject stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        final UserParser userParser = new UserParser();
        final ContentResolver contentResolver = getContext().getContentResolver();

        Log.i(TAG, "Parsing stream as JSON");
        final List<UserParser.UserEntry> entries = userParser.parse(stream, getContext());
        Log.i(TAG, "Parsing complete. Found " + entries.size() + " entries");


        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming projects
        HashMap<Integer, UserParser.UserEntry> userMap = new HashMap<Integer, UserParser.UserEntry>();
        for (UserParser.UserEntry e : entries) {
            userMap.put(e.userId, e);
        }

        // Get list of all items
        Log.i(TAG, "Fetching local projects for merge");
        Uri uri = UserContract.Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        int userId;
        String url;
        String username;
        String screen_name;
        int rank;
        String image_url;
        int followers;
        int following;
        int projects;
        int skulls;
        String location;
        String about_me;
        String who_am_i;
        String what_i_have_done;
        String what_i_would_like_to_do;
        long created;
        String tags;
        byte[] image;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getInt(COLUMN_ID);
            userId = c.getInt(COLUMN_USER_ID);
            url = c.getString(COLUMN_URL);
            username = c.getString(COLUMN_USERNAME);
            screen_name = c.getString(COLUMN_SCREEN_NAME);
            rank = c.getInt(COLUMN_RANK);
            image_url = c.getString(COLUMN_IMAGE_URL);
            followers = c.getInt(COLUMN_FOLLOWERS);
            following = c.getInt(COLUMN_FOLLOWING);
            projects = c.getInt(COLUMN_PROJECTS);
            skulls = c.getInt(COLUMN_SKULLS);
            location = c.getString(COLUMN_LOCATION);
            about_me = c.getString(COLUMN_ABOUT_ME);
            who_am_i = c.getString(COLUMN_WHO_AM_I);
            what_i_have_done = c.getString(COLUMN_WHAT_I_HAVE_DONE);
            what_i_would_like_to_do = c.getString(COLUMN_WHAT_I_WOULD_LIKE_TO_DO);
            created = c.getLong(COLUMN_CREATED);
            tags = c.getString(COLUMN_TAGS);

            UserParser.UserEntry match = userMap.get(userId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                userMap.remove(userId);
                // Check to see if the entry needs to be updated
                Uri existingUri = UserContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(userId)).build();
                if ((match.username != null && !match.username.equals(username)) ||
                        (match.url != null && !match.url.equals(url)) ||
                        (match.created != created)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(UserContract.Entry.COLUMN_USER_ID, userId)
                            .withValue(UserContract.Entry.COLUMN_URL, url)
                            .withValue(UserContract.Entry.COLUMN_USERNAME, username)
                            .withValue(UserContract.Entry.COLUMN_SCREEN_NAME, screen_name)
                            .withValue(UserContract.Entry.COLUMN_RANK, rank)
                            .withValue(UserContract.Entry.COLUMN_IMAGE_URL, image_url)
                            .withValue(UserContract.Entry.COLUMN_FOLLOWERS, followers)
                            .withValue(UserContract.Entry.COLUMN_FOLLOWING, following)
                            .withValue(UserContract.Entry.COLUMN_PROJECTS, projects)
                            .withValue(UserContract.Entry.COLUMN_SKULLS, skulls)
                            .withValue(UserContract.Entry.COLUMN_LOCATION, location)
                            .withValue(UserContract.Entry.COLUMN_ABOUT_ME, about_me)
                            .withValue(UserContract.Entry.COLUMN_WHO_AM_I, who_am_i)
                            .withValue(UserContract.Entry.COLUMN_WHAT_I_HAVE_DONE, what_i_have_done)
                            .withValue(UserContract.Entry.COLUMN_WHAT_I_WOULD_LIKE_TO_DO, what_i_would_like_to_do)
                            .withValue(UserContract.Entry.COLUMN_CREATED, created)
                            .withValue(UserContract.Entry.COLUMN_TAGS, tags)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = UserContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(userId)).build();
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (UserParser.UserEntry e : userMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.userId);
            batch.add(ContentProviderOperation.newInsert(UserContract.Entry.CONTENT_URI)
                    .withValue(UserContract.Entry.COLUMN_USER_ID, e.userId)
                    .withValue(UserContract.Entry.COLUMN_URL, e.url)
                    .withValue(UserContract.Entry.COLUMN_USERNAME, e.username)
                    .withValue(UserContract.Entry.COLUMN_SCREEN_NAME, e.screen_name)
                    .withValue(UserContract.Entry.COLUMN_RANK, e.rank)
                    .withValue(UserContract.Entry.COLUMN_IMAGE_URL, e.image_url)
                    .withValue(UserContract.Entry.COLUMN_FOLLOWERS, e.followers)
                    .withValue(UserContract.Entry.COLUMN_FOLLOWING, e.following)
                    .withValue(UserContract.Entry.COLUMN_PROJECTS, e.projects)
                    .withValue(UserContract.Entry.COLUMN_SKULLS, e.skulls)
                    .withValue(UserContract.Entry.COLUMN_LOCATION, e.location)
                    .withValue(UserContract.Entry.COLUMN_ABOUT_ME, e.about_me)
                    .withValue(UserContract.Entry.COLUMN_WHO_AM_I, e.who_am_i)
                    .withValue(UserContract.Entry.COLUMN_WHAT_I_HAVE_DONE, e.what_i_have_done)
                    .withValue(UserContract.Entry.COLUMN_WHAT_I_WOULD_LIKE_TO_DO, e.what_i_would_like_to_do)
                    .withValue(UserContract.Entry.COLUMN_CREATED, e.created)
                    .withValue(UserContract.Entry.COLUMN_TAGS, e.tags)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(UserContract.USER_CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                UserContract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private JSONObject downloadUrl(final String url) throws IOException {
        queue = Volley.newRequestQueue(getContext());
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest req = new JsonObjectRequest(url, null, future, future);
        queue.add(req);
        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            usersjson = response;
            return usersjson;
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return usersjson;
    }

    private int getUserPageNum(final String url) throws IOException {
        queue = Volley.newRequestQueue(getContext());
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest req = new JsonObjectRequest(url, null, future, future);
        queue.add(req);
        try {
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            userLastPage = response.getInt("last_page");
            return userLastPage;
        } catch (InterruptedException e) {
            // exception handling
        } catch (ExecutionException e) {
            // exception handling
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userLastPage;
    }
}