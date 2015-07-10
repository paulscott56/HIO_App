package hackaday.io.hackadayio.data;

import org.json.JSONObject;

/**
 * Created by paul on 2015/07/10.
 */
public class Feed {

    private int total;
    private int per_page;
    private int last_page;
    private int page;
    private JSONObject json;


    public Feed(int total, int per_page, int last_page, int page, JSONObject json) {
        this.total = total;
        this.per_page = per_page;
        this.last_page = last_page;
        this.page = page;
        this.json = json;
    }

    public Feed() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }
}
