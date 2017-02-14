/*
 * "ListeParkingsFragmentHome.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.classes.Parking;

/**
 * Fragment de l'activité principale, Page d'accueil,
 * Comporte la liste des parkings favoris de l'utilisateur
 *
 * @author Guillaume BERNARD
 */
public class ListeParkingsFragmentHome extends ListFragment {

    /**
     * Initialise le fragment
     *
     * @param inflater           layoutinflater
     * @param container          viewgroup
     * @param savedInstanceState Etat du fragment
     * @return Le fragment créé
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_liste_parkings, container, false);

        // Complète la liste avec les parkings favoris
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
        ArrayList<Parking> parks = decodeJson("{\"parking\": "+jsonRecu + "}");

        // Liste contenant les items
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < parks.size(); i++) {
            // Contient la définition de chaque item
            HashMap<String, String> map = new HashMap<String, String>();
            double occupation = (parks.get(i).getPlaceDispo()*1.0) / (parks.get(i).getPlaces()*1.0);
            map.put("nom", parks.get(i).getNom());
            map.put("distance", "1km");
            map.put("favoris", "true");
            map.put("refreshTime", "À l'instant");
            map.put("occupation", occupation+"");
            items.add(map);
        }

        // Met en place les éléments dans la liste avec le layout sans aperçu du parking (no-preview)
        SimpleAdapter adapter = new SimpleAdapter(getContext(), items, R.layout.item_park_nopreview,
                // Fait correspondre la valeur à la view de l'item layout
                new String[]{"nom", "refreshTime", "favoris", "occupation"},
                new int[]{R.id.nomPark, R.id.refreshTime, R.id.favorite, R.id.overlay});
        // On ajoute le binder personnalisé
        adapter.setViewBinder(new MyBinder());
        this.setListAdapter(adapter);
    }

    /**
     * Récupère le fichier JSON du serveur JEE
     * le décode et le transforme en Parking[]
     *
     * @return un tableau correspondant au JSON
     */
    private ArrayList<Parking> decodeJson(String json) {
        // Va contenir tous les parkings du JSON
        ArrayList<Parking> parkings = new ArrayList<Parking>();
        try {
            // Création de l'object Json
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("parking");
            // Parcours tous les parkings du Json
            for (int i = 0; i < jsonArray.length(); i++) {
                // Récupération des infos du parking
                String nom = jsonArray.getJSONObject(i).getString("nom");
                int nbPlaces = jsonArray.getJSONObject(i).getInt("nbPlaces");
                int nbPlacesLibres = jsonArray.getJSONObject(i).getInt("nbPlacesLibres");
                long longitude = jsonArray.getJSONObject(i).getLong("longitude");
                long latitude = jsonArray.getJSONObject(i).getLong("latitude");
                Parking p = new Parking(nom, nbPlaces, nbPlacesLibres, longitude, latitude);
                // Ajout du nouveau parking à la liste
                parkings.add(p);
            }
        } catch (JSONException e) {
            Log.e("JSON", "Erreur du format JSON");
        }
        return parkings;
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this.getContext(), "List click", Toast.LENGTH_SHORT).show();
    }
}
