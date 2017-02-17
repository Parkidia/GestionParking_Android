/*
 * "ScreenSlidePagerActivity.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import parkidia.parking.a4lpmms.gestionparking_android.classes.JsonManager;
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

    private SharedPreferences preferences;
    /* Page actuelle */
    private int currentPage = 0;
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

        // Préférences utilisateur
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
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
                currentPage = position;
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
     * Positionne la page sur le page position
     * @param position position de la page à laquelle on veut accéder
     */
    public void scrollTo(int position) {
        mPager.setCurrentItem(position, true);
    }

    /**
     * Clic sur le bouton Rafraîchir d'une liste
     * @param v TextView rafraichir de la listeView
     */
    public void clicRefresh(View v) {
        // TODO rafraichir la liste sélectionnée
    }

    /**
     * Clic sur le bouton "étoile" d'ajout aux favoris
     * Ajoute le parking aux favoris, ou l'enlève s'il est déjà en favoris
     * @param view
     */
    public void clicFavorite(View view) {
        ImageView iconeFav = (ImageView) view;
        LinearLayout sousLayout;
        LinearLayout layout = (LinearLayout) view.getParent().getParent();
        try {
            // Si le layout de l'item est celui sans preview
            sousLayout = (LinearLayout) layout.getChildAt(0);
        } catch (Exception e) {
            // Si le laout de l'item est celui avec preview
            sousLayout = (LinearLayout) layout.getChildAt(1);
        }
        // On récupère le TextView contenant le nom du parking
        TextView nomParking = (TextView) sousLayout.getChildAt(0);

        if (view.getTag().equals("nonfav")) {
            iconeFav.setImageResource(R.drawable.star_favori_full);
            view.setTag("fav");

            // Ajouter aux favoris dans les préférences
            String json = preferences.getString("favoris", "{\"favoris\": []}");
            json = JsonManager.encodeAddFavoris(json, nomParking.getText().toString());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("favoris", json);
            editor.commit();
        } else {
            iconeFav.setImageResource(R.drawable.star_favori);
            view.setTag("nonfav");
            // Retirer le favoris des préférences
            String json = preferences.getString("favoris", "{\"favoris\": []}");
            json = JsonManager.encodeRemFavoris(json, nomParking.getText().toString());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("favoris", json);
            editor.commit();
        }
        iconeFav.refreshDrawableState();

        // Rafraichir les listes des parkings = recharger les fragments
        reloadFragments();
    }

    /**
     * Recharge le contenu des fragments de l'activité
     */
    private void reloadFragments() {
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        // Positionne l'user à la page à laquelle il était
        scrollTo(currentPage);
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