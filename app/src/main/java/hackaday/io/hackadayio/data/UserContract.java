package hackaday.io.hackadayio.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import hackaday.io.hackadayio.Constants;

/**
 * Created by paul on 2015/07/23.
 */
public class UserContract {

        private UserContract() {
        }

        /**
         * Content provider authority.
         */
        public static final String USER_CONTENT_AUTHORITY = Constants.USER_CONTENT_AUTHORITY;

        /**
         * Base URI.
         */
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + USER_CONTENT_AUTHORITY);

        /**
         * Path component for "entry"-type resources..
         */
        public static final String PATH_ENTRIES = "users";

        /**
         * Columns supported by "entries" records.
         */
        public static class Entry implements BaseColumns {
            /**
             * MIME type for lists of entries.
             */
            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.projectsyncadapter.users";
            /**
             * MIME type for individual entries.
             */
            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.projectsyncadapter.users";

            /**
             * Fully qualified URI for "entry" resources.
             */
            public static final Uri CONTENT_URI =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

            /**
             * Table name where records are stored for "entry" resources.
             */
            public static final String TABLE_NAME = "users";

            public static final String COLUMN_USER_ID = "id";

            public static final String COLUMN_URL = "url";

            public static final String COLUMN_USERNAME = "username";

            public static final String COLUMN_SCREEN_NAME = "screen_name";

            public static final String COLUMN_RANK = "rank";

            public static final String COLUMN_IMAGE_URL = "image_url";

            public static final String COLUMN_FOLLOWERS = "followers";

            public static final String COLUMN_FOLLOWING = "following";

            public static final String COLUMN_PROJECTS = "projects";

            public static final String COLUMN_SKULLS = "skulls";

            public static final String COLUMN_LOCATION = "location";

            public static final String COLUMN_ABOUT_ME = "about_me";

            public static final String COLUMN_WHO_AM_I = "who_am_i";

            public static final String COLUMN_WHAT_I_HAVE_DONE = "what_i_have_done";

            public static final String COLUMN_WHAT_I_WOULD_LIKE_TO_DO = "what_i_would_like_to_do";

            public static final String COLUMN_CREATED = "created";

            public static final String COLUMN_TAGS  = "tags";

        }
    }

