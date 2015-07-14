package hackaday.io.hackadayio.data;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 2015/07/14.
 */
public class ProjectParser {
    private static final String TAG = "ProjectParser";

    public List<Entry> parse(JSONObject stream) {
        List<Entry> entries = new ArrayList<Entry>();

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
        public final int id;
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

        public Entry(int id, int projectId, String url, int owner_id, String name, String summary,
                     String description, String image_url, int views, int comments, int followers,
                     int skulls, int logs, int details, int instruction, int components,
                     int images, long created, int updated, String tags) {
            this.id = id;
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
        }
    }
}
