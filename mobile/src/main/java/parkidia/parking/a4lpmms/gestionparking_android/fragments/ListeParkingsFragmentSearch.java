/*
 * "ListeParkingsFragmentSearch.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.classes.JsonManager;
import parkidia.parking.a4lpmms.gestionparking_android.classes.Parking;

/**
 * Fragment de l'activité principale, seconde page
 * Gère la liste des parkings proches de la position de l'utilisateur
 * @author Guillaume BERNARD
 */
public class ListeParkingsFragmentSearch extends ListFragment {

    private EditText saisieRecherche;
    private SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_liste_parkings_search, container, false);

        saisieRecherche = (EditText) rootView.findViewById(R.id.saisieRecherche);
        // Complète la liste avec les parkings proches
        fillListView();

        return rootView;
    }

    /**
     * Rempli la listeView avec les données du JSON récupéré
     * TODO Récurérer ces infos du JSON
     */
    private void fillListView() {
        // TODO Récupérer le JSon du serveur pour les parkings favoris
        // Json en brut pour tester le fonctionnement
        // Ce json sera récupérer plus tard au serveur JEE
        String jsonRecu = "[{\"nom\": \"IUT de rodez\",\"nbPlaces\": 15,\"nbPlacesLibres\": 8,\"latitude\": 11,\"longitude\": 12}, {\"nom\": \"Geant\",\"nbPlaces\": 50,\"nbPlacesLibres\": 10,\"latitude\": 15.52,\"longitude\": 16.95}]";

        // Récupère les parkings favoris
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String jsonFav = prefs.getString("favoris", "{\"favoris\": []}");

        ArrayList<Parking> parks = JsonManager.decodeParkings("{\"parking\": "+jsonRecu + "}");
        ArrayList<String> favoris = JsonManager.decodeFavoris(jsonFav);

        // Liste contenant les items
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < parks.size(); i++) {
            // Contient la définition de chaque item
            HashMap<String, String> map = new HashMap<String, String>();
            double occupation = (parks.get(i).getPlaceDispo()*1.0) / (parks.get(i).getPlaces()*1.0);
            map.put("nom", parks.get(i).getNom());
            map.put("distance", "1km");
            if (favoris.contains(parks.get(i).getNom())) {
                map.put("favoris", "true");
            } else {
                map.put("favoris", "false");
            }
            map.put("refreshTime", "À l'instant");
            map.put("occupation", occupation+"");
            items.add(map);
            saisieRecherche.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // unsed
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // unsed
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String texte = saisieRecherche.getText().toString();
                    adapter.getFilter().filter(texte);
                }
            });
        }

        // Met en place les éléments dans la liste avec le layout sans aperçu du parking (no-preview)
        adapter = new SimpleAdapter(getContext(), items, R.layout.item_park_nopreview,
                // Fait correspondre la valeur à la view de l'item layout
                new String[]{"nom", "refreshTime", "favoris", "occupation"},
                new int[]{R.id.nomPark, R.id.refreshTime, R.id.favorite, R.id.overlay});
        // On ajoute le binder personnalisé
        adapter.setViewBinder(new MyBinder());
        this.setListAdapter(adapter);
    }
    /**
     * Classe binder pour faire correspondre les items de la map avec les views
     */
    class MyBinder implements SimpleAdapter.ViewBinder {
        /**
         * TODO commenter
         *
         * @param view
         * @param data
         * @param textRepresentation
         * @return
         */
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            // Bind l'icone favoris
            if (view.getId() == R.id.favorite) {
                ImageView icone = (ImageView) view;
                String stringval = (String) data; // "true" ou "false"
                if (stringval.equals("true")) {
                    icone.setTag("fav");
                    icone.setImageResource(R.drawable.star_favori_full);
                } else {
                    icone.setTag("nonfav");
                    icone.setImageResource(R.drawable.star_favori);
                }
                return true;
            }
            // Bind les voitures pour la disponibilité
            if (view.getId() == R.id.overlay) {
                ImageView icone = (ImageView) view;
                int overlay;
                double dispo = Double.parseDouble((String) data); // On récupère le taux de remplissage
                if (dispo <= 0.40) {
                    overlay = R.drawable.cars_green;
                } else if (dispo <= 0.75) {
                    overlay = R.drawable.cars_orange;
                } else {
                    overlay = R.drawable.cars_red;
                }

                // On transforme le drawable en bitmap pour le manipuler
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), overlay);
                // On créé l'overlay avec les voitures de la bonne couleur
                Bitmap resized = Bitmap.createBitmap(bmp, 0, 0, (int) (bmp.getWidth() * dispo), bmp.getHeight());
                icone.setImageBitmap(resized);

                return true;
            }
            return false;
        }
    }
}
