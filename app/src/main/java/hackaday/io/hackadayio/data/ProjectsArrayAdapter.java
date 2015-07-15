package hackaday.io.hackadayio.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hackaday.io.hackadayio.R;

/**
 * Created by paul on 2015/07/15.
 */
public class ProjectsArrayAdapter extends ArrayAdapter<ProjectItem>  {

    Context context;
    List<ProjectItem> projectItems;
    Map<String,ViewHolder> viewholders;

    public ProjectsArrayAdapter(Context context, List<ProjectItem> projectItems) {
        super(context, R.layout.project_item, projectItems);
        this.context = context;
        this.projectItems = projectItems;
        this.viewholders = new HashMap<>();
    }

    @Override
    public void add(ProjectItem object) {
        this.projectItems.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends ProjectItem> collection) {
        this.projectItems.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.project_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.labelView = (TextView) convertView.findViewById(R.id.label);
            //viewHolder.valueView = (TextView) convertView.findViewById(R.id.value);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ProjectItem item = getItem(position);
        viewHolder.labelView.setText(item.getName() + " - " + item.getSummary());
        //viewHolder.valueView.setText(item.getSummary());
        viewHolder.avatar.setImageBitmap(item.getAvatar());

        return convertView;
    }

    private static class ViewHolder {

        public TextView labelView;
        //public TextView valueView;
        public ImageView avatar;

    }
}