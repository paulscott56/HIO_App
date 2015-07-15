package hackaday.io.hackadayio.data;

import android.graphics.Bitmap;

/**
 * Created by paul on 2015/07/15.
 */
public class ProjectItem {

    public int id;
    private String url;
    private int owner_id;
    private String name;
    private String summary;
    private String description;
    private String image_url;
    private int views;
    private int comments;
    private int followers;
    private int skulls;
    private int logs;
    private int details;
    private int instruction;
    private int components;
    private int images;
    private long created;
    private long updated;
    private String tags;
    public Bitmap avatar;

    public ProjectItem() {
    }

    public ProjectItem(int id, String url, int owner_id, String name, String summary,
                       String description, String image_url, int views, int comments,
                       int followers, int skulls, int logs, int details, int instruction,
                       int components, int images, long created, long updated, String tags) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getSkulls() {
        return skulls;
    }

    public void setSkulls(int skulls) {
        this.skulls = skulls;
    }

    public int getLogs() {
        return logs;
    }

    public void setLogs(int logs) {
        this.logs = logs;
    }

    public int getDetails() {
        return details;
    }

    public void setDetails(int details) {
        this.details = details;
    }

    public int getInstruction() {
        return instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    public int getComponents() {
        return components;
    }

    public void setComponents(int components) {
        this.components = components;
    }

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

}

