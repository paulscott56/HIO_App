package hackaday.io.hackadayio.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hackaday.io.hackadayio.R;

/**
 * Created by paul on 2015/07/10.
 */
public class CommentsFragment extends Fragment {

    public CommentsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comments_fragment, container, false);
        return rootView;
    }

    public static CommentsFragment newInstance() {

        CommentsFragment cf = new CommentsFragment();
        Bundle b = new Bundle();

        return cf;
    }
}
