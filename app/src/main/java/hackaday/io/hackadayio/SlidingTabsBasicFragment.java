package hackaday.io.hackadayio;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hackaday.io.hackadayio.fragments.CommentsFragment;
import hackaday.io.hackadayio.fragments.FeedsFragment;
import hackaday.io.hackadayio.fragments.MessagesFragment;
import hackaday.io.hackadayio.fragments.PagesFragment;
import hackaday.io.hackadayio.fragments.ProjectsFragment;
import hackaday.io.hackadayio.fragments.SearchFragment;
import hackaday.io.hackadayio.fragments.UsersFragment;

/**
 * Created by paul on 2015/07/01.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String TAG = "SlidingTabsBasicFrag";

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    public SlidingTabsBasicFragment() {
    }

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ProfilePagerAdapter(getFragmentManager()));

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private class ProfilePagerAdapter extends FragmentPagerAdapter {
        private static final String TAG = "ProfilePager";

        int NumberOfPages = 7;

        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
            super.getPageTitle(0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return getString(R.string.feeds);

                case 1:
                    return getString(R.string.projects);

                case 2:
                    return getString(R.string.users);

                case 3:
                    return getString(R.string.pages);

                case 4:
                    return getString(R.string.messages);

                case 5:
                    return getString(R.string.comments);

                case 6:
                    return getString(R.string.search);



            }


            return null;
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return FeedsFragment.newInstance();
                case 1: return ProjectsFragment.newInstance();
                case 2: return UsersFragment.newInstance();
                case 3: return PagesFragment.newInstance();
                case 4: return MessagesFragment.newInstance();
                case 5: return CommentsFragment.newInstance();
                case 6: return SearchFragment.newInstance();

                default: return FeedsFragment.newInstance();
            }
        }


        @Override
        public int getCount() {
            return NumberOfPages;
        }

    }
}
