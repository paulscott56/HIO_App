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
 * Created by paul on 2015/07/14.
 */
public class ProjectParser {
    private static final String TAG = "ProjectParser";
    private RequestQueue queue;
    private Context appContext;
    private Bitmap bmp;
    private byte[] bmpArr;
    private ImageLoader mImageLoader;

    public List<Entry> parse(JSONObject stream, Context appContext) {
        this.appContext = appContext;
        List<Entry> entries = new ArrayList<Entry>();
        try {
            JSONArray projects = stream.getJSONArray("projects");
            for(int i = 0; i <= stream.length(); i++) {
                // download the image

                queue = Volley.newRequestQueue(appContext);
                //Initialising ImageDownloader
                Bitmap dinges = ImageCacheManager.getInstance().getBitmap(projects.getJSONObject(i).getString("image_url"));
                //bmpArr = DbBitmapUtility.getBytes(dinges);

                Entry e = new Entry(
                        projects.getJSONObject(i).getInt("id"),
                        projects.getJSONObject(i).getString("url"),
                        projects.getJSONObject(i).getInt("owner_id"),
                        projects.getJSONObject(i).getString("name"),
                        projects.getJSONObject(i).getString("summary"),
                        projects.getJSONObject(i).getString("description"),
                        projects.getJSONObject(i).getString("image_url"),
                        projects.getJSONObject(i).getInt("views"),
                        projects.getJSONObject(i).getInt("comments"),
                        projects.getJSONObject(i).getInt("followers"),
                        projects.getJSONObject(i).getInt("skulls"),
                        projects.getJSONObject(i).getInt("logs"),
                        projects.getJSONObject(i).getInt("details"),
                        projects.getJSONObject(i).getInt("instruction"),
                        projects.getJSONObject(i).getInt("components"),
                        projects.getJSONObject(i).getInt("images"),
                        projects.getJSONObject(i).getLong("created"),
                        projects.getJSONObject(i).getInt("updated"),
                        projects.getJSONObject(i).getString("tags"),
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
     * This class represents a single entry (project) in the JSON feed.
     *
     */
    public static class Entry {
        public final int projectId;
        public final String url;
        public final int owner_id;
        public final String name;
        public final String summary;
        public final String description;
        public final String image_url;
        public final int views;
        public final int comments;
        public final int followers;
        public final int skulls;
        public final int logs;
        public final int details;
        public final int instruction;
        public final int components;
        public final int images;
        public final long created;
        public final int updated;
        public final String tags;
        public final byte[] image;

        public Entry(int projectId, String url, int owner_id, String name, String summary,
                     String description, String image_url, int views, int comments, int followers,
                     int skulls, int logs, int details, int instruction, int components,
                     int images, long created, int updated, String tags, byte[] image) {

            this.projectId = projectId;
            this.url = url;
            this.owner_id = owner_id;
            this.name = name;
            this.summary = summary;
            this.description = description;
            this.image_url = image_url;
            this.views = views;
            this.comments = comments;
            this.followers = followers;
            this.skulls = skulls;
            this.logs = logs;
            this.details = details;
            this.instruction = instruction;
            this.components = components;
            this.images = images;
            this.created = created;
            this.updated = updated;
            this.tags = tags;
            this.image = image;
        }
    }
}