package hackaday.io.hackadayio.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import hackaday.io.hackadayio.Constants;

/**
 * Created by paul on 2015/07/14.
 */
public class ProjectContract {

    private ProjectContract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = Constants.CONTENT_AUTHORITY;

    /**
     * Base URI.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    public static final String PATH_ENTRIES = "projects";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.projectsyncadapter.projects";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.projectsyncadapter.project";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "projects";

        /**
         * Project ID. (Note: Not to be confused with the database primary key, which is _ID.
         */
        public static final String COLUMN_PROJECT_ID = "id";

        /**
         * Project URL
         */
        public static final String COLUMN_URL = "url";

        /**
         * Project owner_id
         */
        public static final String COLUMN_OWNER_ID = "owner_id";

        /**
         * Project name
         */
        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_SUMMARY = "summary";

        /**
         * Project description
         */
        public static final String COLUMN_DESCRIPTION = "description";

        /**
         * Immage url
         */
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static final String COLUMN_VIEWS = "views";

        public static final String COLUMN_COMMENTS = "comments";

        public static final String COLUMN_FOLLOWERS = "followers";

        public static final String COLUMN_SKULLS = "skulls";

        public static final String COLUMN_LOGS = "logs";

        public static final String COLUMN_DETAILS = "details";

        public static final String COLUMN_INSTRUCTION = "instruction";

        public static final String COLUMN_COMPONENTS = "components";

        public static final String COLUMN_IMAGES = "images";

        /**
         * Date project was published.
         */
        public static final String COLUMN_CREATED = "created";

        public static final String COLUMN_UPDATED = "updated";

        public static final String COLUMN_TAGS  = "tags";

        public static final String COLUMN_IMAGE = "image";
    }
}
