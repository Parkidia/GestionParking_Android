package parkidia.parking.a4lpmms.gestionparking_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragment;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragmentProches;
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

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView[] icones = new ImageView[NUM_PAGES];
                icones[0] = (ImageView) findViewById(R.id.home);
                icones[1] = (ImageView) findViewById(R.id.near);
                icones[2] = (ImageView) findViewById(R.id.search);
                icones[3] = (ImageView) findViewById(R.id.settings);

                for (ImageView ico: icones) {
                    ico.setAlpha(0.5f);
                }
                icones[position].setAlpha(1f);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
     * Réagit au clic de l'utilisateur sur un bouton du bandeau du bas
     * Défile les vues jusqu'à la vue sélectionnée
     * @param view bouton sur lequel on a cliqué
     */
    public void scrollTo(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        mPager.setCurrentItem(position, true);
    }

    /**
     * Pager qui représente le slider des 4 pages
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Positionne le fragment de vue aux positions données
         * @param position position qui correspond à la vue
         * @return Vue à ajouter à la position donnée
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new ListeParkingsFragment();
                    break;
                case 1:
                    fragment = new ListeParkingsFragmentProches();
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