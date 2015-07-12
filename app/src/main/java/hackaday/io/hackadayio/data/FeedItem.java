package hackaday.io.hackadayio.data;

import org.json.JSONObject;

/**
 * Created by paul on 2015/07/12.
 */
public class FeedItem {

    public String id;
    public JSONObject content;

    public FeedItem(String id, JSONObject content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return content.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}

