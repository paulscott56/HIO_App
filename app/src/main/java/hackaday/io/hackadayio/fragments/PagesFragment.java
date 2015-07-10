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
public class PagesFragment extends Fragment {

    public PagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pages_fragment, container, false);
        return rootView;
    }

    public static PagesFragment newInstance() {

        PagesFragment pf = new PagesFragment();
        Bundle b = new Bundle();

        return pf;
    }
}
