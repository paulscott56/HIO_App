package hackaday.io.hackadayio.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hackaday.io.hackadayio.imagecache.ImageCacheManager;

/**
 * Created by paul on 2015/07/23.
 */
public class UserParser {

    private static final String TAG = "UserParser";
    private RequestQueue queue;
    private Context appContext;
    private Bitmap bmp;
    private byte[] bmpArr;
    private ImageLoader mImageLoader;

    public List<UserEntry> parse(JSONObject stream, Context appContext) {
        this.appContext = appContext;
        List<UserEntry> entries = new ArrayList<UserEntry>();
        try {
            JSONArray users = stream.getJSONArray("users");
            for(int i = 0; i <= stream.length(); i++) {
                // download the image

                queue = Volley.newRequestQueue(appContext);
                //Initialising ImageDownloader
                Bitmap dinges = ImageCacheManager.getInstance().getBitmap(users.getJSONObject(i).getString("image_url"));
                //bmpArr = DbBitmapUtility.getBytes(dinges);

                UserEntry e = new UserEntry(
                        users.getJSONObject(i).getInt("id"),
                        users.getJSONObject(i).getString("url"),
                        users.getJSONObject(i).getString("username"),
                        users.getJSONObject(i).getString("screen_name"),
                        users.getJSONObject(i).getInt("rank"),
                        users.getJSONObject(i).getString("image_url"),
                        users.getJSONObject(i).getInt("followers"),
                        users.getJSONObject(i).getInt("following"),
                        users.getJSONObject(i).getInt("projects"),
                        users.getJSONObject(i).getInt("skulls"),
                        users.getJSONObject(i).getString("location"),
                        users.getJSONObject(i).getString("about_me"),
                        users.getJSONObject(i).getString("who_am_i"),
                        users.getJSONObject(i).getString("what_i_have_done"),
                        users.getJSONObject(i).getString("what_i_would_like_to_do"),
                        users.getJSONObject(i).getLong("created"),
                        users.getJSONObject(i).getString("tags"),
                        bmpArr
                );
                entries.add(e);
                Log.i(TAG, "adding entry " + e.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }

    /**
     * Parses the JSON and return an entry with a model of projects found via an inputStream
     */


    /**
     * This class represents a single entry (user) in the JSON feed.
     *
     */
    public static class UserEntry {
        public final int userId;
        public final String url;
        public final String username;
        public final String screen_name;
        public final int rank;
        public final String image_url;
        public final int followers;
        public final int following;
        public final int projects;
        public final int skulls;
        public final String location;
        public final String about_me;
        public final String who_am_i;
        public final String what_i_have_done;
        public final String what_i_would_like_to_do;
        public final long created;
        public final String tags;
        public final byte[] image;

        public UserEntry(int userId, String url, String username, String screen_name, int rank,
                         String image_url, int followers, int following, int pprojects, int skulls,
                         String location, String about_me, String who_am_i, String what_i_have_done,
                         String what_i_would_like_to_do, long created, String tags, byte[] image) {
            this.userId = userId;
            this.url = url;
            this.username = username;
            this.screen_name = screen_name;
            this.rank = rank;
            this.image_url = image_url;
            this.followers = followers;
            this.following = following;
            this.projects = pprojects;
            this.skulls = skulls;
            this.location = location;
            this.about_me = about_me;
            this.who_am_i = who_am_i;
            this.what_i_have_done = what_i_have_done;
            this.what_i_would_like_to_do = what_i_would_like_to_do;
            this.created = created;
            this.tags = tags;
            this.image = image;
        }
    }
}
