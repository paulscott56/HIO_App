package hackaday.io.hackadayio.data;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * Created by paul on 2015/07/12.
 */
public class FeedItem {

    public String id;
    private String screen_name;
    private String updateType;
    private String projectId;
    private String user2Id;
    private String postId;
    private String summary;
    private String userId;

    public JSONObject content;
    public Bitmap avatar;

    public FeedItem(String id, JSONObject content, Bitmap avatar) {
        this.id = id;
        this.content = content;
        this.avatar = avatar;
    }

    public FeedItem(String id, String screen_name, String updateType, String projectId, String user2Id, String postId, String summary, String userId, Bitmap bmp) {

        this.id = id;
        this.screen_name = screen_name;
        this.avatar = bmp;
        this.updateType = updateType;
        this.projectId = projectId;
        this.user2Id = user2Id;
        this.postId = postId;
        this.summary = summary;
        this.userId = userId;
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


    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

