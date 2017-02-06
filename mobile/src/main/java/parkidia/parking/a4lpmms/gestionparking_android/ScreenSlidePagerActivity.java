package parkidia.parking.a4lpmms.gestionparking_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragment;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragmentSearch;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ParametersFragment;

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * Nombre de pages dans le slider
     */
    private static final int NUM_PAGES = 4;

    /**
     * Pager de l'activité, récupère les demande de changement de page et animations
     */
    private ViewPager mPager;

    /**
     * Fourni la page au pager
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new ListeParkingsFragment();
                    break;
                case 2:
                    fragment = new ListeParkingsFragmentSearch();
                    break;
                case 3:
                    fragment = new ParametersFragment();
                    break;
                default:
                    fragment = new ListeParkingsFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}