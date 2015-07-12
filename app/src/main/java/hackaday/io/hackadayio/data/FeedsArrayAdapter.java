package hackaday.io.hackadayio.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import hackaday.io.hackadayio.R;

/**
 * Created by paul on 2015/07/12.
 */
public class FeedsArrayAdapter extends ArrayAdapter<FeedItem> {

    Context context;
    ArrayList<FeedItem> feedItems;
    Map<String,ViewHolder> viewholders;

    public FeedsArrayAdapter(Context context, ArrayList<FeedItem> feedItems) {
        super(context, R.layout.feed_item);
        this.context = context;
        this.feedItems = feedItems;
        this.viewholders = new HashMap<>();
    }

    @Override
    public void add(FeedItem object) {
        this.feedItems.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends FeedItem> collection) {
        this.feedItems.addAll(collection);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FeedItem n = feedItems.get(position);

        ViewHolder viewHolder;
        if(viewholders.get(position) != null) {

            viewHolder = viewholders.get(position);

        } else {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.feed_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.labelView = (TextView) convertView.findViewById(R.id.label);
            viewHolder.valueView = (TextView) convertView.findViewById(R.id.value);

            viewholders.put(String.valueOf(position), viewHolder);

        }

        viewHolder.labelView.setText(n.getId());
        viewHolder.valueView.setText(n.getContent().toString());

        return convertView;

    }

    private static class ViewHolder {

        public TextView labelView;
        public TextView valueView;

    }

}
