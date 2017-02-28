/*
 * "ListeParkingsFragmentProches.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.ScreenSlidePagerActivity;
import parkidia.parking.a4lpmms.gestionparking_android.classes.JsonManager;
import parkidia.parking.a4lpmms.gestionparking_android.classes.Parking;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.GuideActivity;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.tools.UserLocationManager;

/**
 * Fragment de l'activité principale, 3ème page
 * Gère une liste de parkings selon une recherche
 * @author Guillaume BERNARD
 */
public class ListeParkingsFragmentProches extends ListFragment {

    private static final long INTERVALLE_ACTUALISATION_GPS = 30000;
    /* Coordonnées GPS */
    private Location localisation;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_liste_parkings_proches, container, false);

        // récupérer la position de l'utilisateur
        UserLocationManager locationManager = new UserLocationManager(getContext());
        localisation = locationManager.getLocation(INTERVALLE_ACTUALISATION_GPS);

        // Complète la liste avec les parkings à proximité de la position de l'utilisateur
        fillListView();

        return rootView;
    }

    /**
     * Rempli la listeView avec les données du JSON récupéré
     */
    private void fillListView() {
        String jsonParkings = ScreenSlidePagerActivity.listeParkingsJson;
        // Récupère les parkings favoris
        SharedPreferences prefs = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String jsonFav = prefs.getString("favoris", "{\"favoris\": []}");

        ArrayList<Parking> parks = JsonManager.decodeParkings("{\"parking\": "+jsonParkings + "}");
        ArrayList<String> favoris = JsonManager.decodeFavoris(jsonFav);

        // Liste contenant les items
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        int distanceArea = prefs.getInt("distanceArea", 5);

        for (int i = 0; i < parks.size(); i++) {
            // Contient la définition de chaque item
            HashMap<String, String> map = new HashMap<String, String>();
            double occupation = (parks.get(i).getPlaceDispo()*1.0) / (parks.get(i).getPlaces()*1.0);
            map.put("nom", parks.get(i).getNom());
            map.put("id", parks.get(i).getId()+"");
            if (favoris.contains(parks.get(i).getNom())) {
                map.put("favoris", "true");
            } else {
                map.put("favoris", "false");
            }
            map.put("refreshTime", "À l'instant");
            map.put("occupation", occupation+"");

            // On récupère la distance entre l'user et le parking
            Location parking = new Location("");
            parking.setLatitude(parks.get(i).getLatitude());
            parking.setLongitude(parks.get(i).getLongitude());
            // distance approximative en km
            if(localisation != null) {
                int distance = (int) (localisation.distanceTo(parking) / 1000);
                if (distance <= distanceArea) {
                    map.put("distance", distance+"km");
                    if (distance == 0) {
                        map.put("distance", "> 1km");
                    }
                    items.add(map);
                }
            } else {
                localisation = new Location("");
            }

        }

        // Met en place les éléments dans la liste avec le layout sans aperçu du parking (no-preview)
        SimpleAdapter adapter = new SimpleAdapter(getContext(), items, R.layout.item_park_preview,
                // Fait correspondre la valeur à la view de l'item layout
                new String[]{"nom", "refreshTime", "favoris", "occupation", "id", "distance"},
                new int[]{R.id.nomPark, R.id.refreshTime, R.id.favorite, R.id.overlay, R.id.id, R.id.distance});
        // On ajoute le binder personnalisé
        adapter.setViewBinder(new MyBinder());
        this.setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        HashMap<String, String> value = (HashMap<String, String>) getListAdapter().getItem(position);
        int idP = Integer.parseInt(value.get("id"));
        Parking p = new Parking(value.get("nom"), 0, 0, 0, 0, idP);
        boolean fav = Boolean.parseBoolean(value.get("favori"));
        p.setFavoris(fav);
        Intent intent = new Intent(getContext(), GuideActivity.class);
        intent.putExtra("parking", p);
        startActivity(intent);
    }

    /**
     * Classe binder pour faire correspondre les items de la map avec les views
     */
    class MyBinder implements SimpleAdapter.ViewBinder {

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
                if (bmp.getWidth() * dispo > 0) {
                    Bitmap resized = Bitmap.createBitmap(bmp, 0, 0, (int) (bmp.getWidth() * dispo), bmp.getHeight());
                    icone.setImageBitmap(resized);
                }
                return true;
            }
            return false;
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getListAdapter().isEmpty()) {
            // Créé un message si il n'y a aucun élément dans la liste
            View emptyListView = view.findViewById(R.id.emptyListView);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }
}
