package hackaday.io.hackadayio.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import hackaday.io.hackadayio.Constants;

/**
 * Created by paul on 2015/07/14.
 */
public class ProjectProvider extends ContentProvider {

    ProjectDatabase mDatabaseHelper;
    private static final String AUTHORITY = Constants.CONTENT_AUTHORITY;

    /**
     * URI ID for route: /projects
     */
    public static final int ROUTE_PROJECTS = 1;

    /**
     * URI ID for route: /projects/{ID}
     */
    public static final int ROUTE_PROJECTS_ID = 2;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "projects", ROUTE_PROJECTS);
        sUriMatcher.addURI(AUTHORITY, "projects/*", ROUTE_PROJECTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new ProjectDatabase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_PROJECTS:
                return ProjectContract.Entry.CONTENT_TYPE;
            case ROUTE_PROJECTS_ID:
                return ProjectContract.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     *
     * <p>Currently supports returning all entries (/projects) and individual entries by ID
     * (/projects/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_PROJECTS_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(ProjectContract.Entry._ID + "=?", id);
            case ROUTE_PROJECTS:
                // Return all known entries.
                builder.table(ProjectContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_PROJECTS:
                long id = db.insertOrThrow(ProjectContract.Entry.TABLE_NAME, null, values);
                result = Uri.parse(ProjectContract.Entry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_PROJECTS_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_PROJECTS:
                count = builder.table(ProjectContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_PROJECTS_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(ProjectContract.Entry.TABLE_NAME)
                        .where(ProjectContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    /**
     * Update an etry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_PROJECTS:
                count = builder.table(ProjectContract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_PROJECTS_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(ProjectContract.Entry.TABLE_NAME)
                        .where(ProjectContract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }


    static class ProjectDatabase extends SQLiteOpenHelper {
        /** Schema version. */
        public static final int DATABASE_VERSION = 1;
        /** Filename for SQLite file. */
        public static final String DATABASE_NAME = "feed.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";
        /** SQL statement to create "entry" table. */
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + ProjectContract.Entry.TABLE_NAME + " (" +
                        ProjectContract.Entry._ID + " INTEGER PRIMARY KEY," +
                        ProjectContract.Entry.COLUMN_PROJECT_ID + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_URL    + TYPE_TEXT + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_OWNER_ID + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_SUMMARY + TYPE_TEXT + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_IMAGE_URL + TYPE_TEXT + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_VIEWS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_COMMENTS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_FOLLOWERS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_SKULLS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_LOGS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_DETAILS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_INSTRUCTION + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_COMPONENTS + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_IMAGES + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_CREATED + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_UPDATED + TYPE_INTEGER + COMMA_SEP +
                        ProjectContract.Entry.COLUMN_TAGS + TYPE_TEXT +
                        ")";

        /** SQL statement to drop "entry" table. */
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + ProjectContract.Entry.TABLE_NAME;

        public ProjectDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}