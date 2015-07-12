package hackaday.io.hackadayio.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 2015/07/12.
 */
public class FeedContent {

    /**
     * An array of feed items.
     */
    public static List<FeedItem> ITEMS = new ArrayList<FeedItem>();

    /**
     * A map of feed items, by ID.
     */
    public static Map<String, FeedItem> ITEM_MAP = new HashMap<String, FeedItem>();

    private static void addItem(FeedItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
