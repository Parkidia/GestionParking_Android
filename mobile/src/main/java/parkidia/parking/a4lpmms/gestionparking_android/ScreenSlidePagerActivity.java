/*
 * "ScreenSlidePagerActivity.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragmentHome;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragmentProches;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ListeParkingsFragmentSearch;
import parkidia.parking.a4lpmms.gestionparking_android.fragments.ParametersFragment;

/**
 * Activité principale, affiche les différentes listes de parking
 * @author Guillaume BERANRD
 */
public class ScreenSlidePagerActivity extends FragmentActivity {
    /** Nombre de pages dans le slider */
    private static final int NUM_PAGES = 4;

    /** Pager de l'activité, récupère les demande de changement de page et animations */
    private ViewPager mPager;

    /** Fourni la page au pager */
    private PagerAdapter mPagerAdapter;

    /**
     * Initialise le viewPager qui va gérer les différentes pages de l'activité
     * @param savedInstanceState Etat de l'instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantie le viewPager
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // On ajoute un listener sur le viewpager pour "éclairer" l'icone correspondante
        // à la page actuelle
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * Corps vide : non utilisé
             * Est appelé quand on fait défiler la page
             * @param position Numéro de la page concernée
             * @param positionOffset position du défilement
             * @param positionOffsetPixels position du défilement
             */
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset, int positionOffsetPixels) {
            }

            /**
             * Est appelé quand on chage de page
             * Eclaire l'icone de la page actuelle dans le menu en bas de page
             * @param position Numéro de la page actuelle
             */
            @Override
            public void onPageSelected(int position) {
                // Association avec le layout
                ImageView[] icones = new ImageView[NUM_PAGES];
                icones[0] = (ImageView) findViewById(R.id.home);
                icones[1] = (ImageView) findViewById(R.id.near);
                icones[2] = (ImageView) findViewById(R.id.search);
                icones[3] = (ImageView) findViewById(R.id.settings);
                // "Eteind" toutes les icones du menu
                for (ImageView ico : icones) {
                    ico.setAlpha(0.5f);
                }
                // Eclaire l'icone de la page actuelle
                icones[position].setAlpha(1f);
            }

            /**
             * Corps vide : non utilisé
             * Est appelé quand l'état du défilement est modifié
             * @param state Etat de la page
             */
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * Réagit au clic de l'utilisateur sur un bouton du bandeau du bas
     * Défile les vues jusqu'à la vue sélectionnée
     *
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
         *
         * @param position position qui correspond à la vue
         * @return Vue à ajouter à la position donnée
         */
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new ListeParkingsFragmentHome();
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
                    fragment = new ListeParkingsFragmentHome();
            }
            return fragment;
        }

        /**
         * Nombre de pages
         * @return le nombre de pages du viewpager
         */
        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}