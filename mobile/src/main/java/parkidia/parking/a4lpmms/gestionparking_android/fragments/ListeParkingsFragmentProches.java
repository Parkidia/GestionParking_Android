/*
 * "ListeParkingsFragmentProches.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import parkidia.parking.a4lpmms.gestionparking_android.R;

/**
 * Fragment de l'activité principale, 3ème page
 * Gère une liste de parkings selon une recherche
 * @author Guillaume BERNARD
 */
public class ListeParkingsFragmentProches extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_liste_parkings, container, false);

        // TODO récupérer la position de l'utilisateur (permissions manifest)

        // Complète la liste avec les parkings à proximité de la position de l'utilisateur
        fillListView();
        return rootView;
    }

    /**
     * Rempli la listeView avec les données du JSON récupéré
     * TODO Récurérer ces infos du JSON
     */
    private void fillListView() {
        // TODO Récupérer le JSon

        // Liste contenant les items
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        // Contient la définition de chaque item
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("nom", "IUT Parking prof haut");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);

        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking prof bas");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "2km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "Parking Foch sous-terrain");
        map.put("distance", "5km");
        map.put("refreshTime", "À l'instant");
        items.add(map);

        // Associe les élements de la map aux views de l'item layout
        SimpleAdapter adapter = new SimpleAdapter(getContext(), items, R.layout.item_park_preview,
                new String[]{"nom", "refreshTime"},
                new int[]{R.id.nomPark, R.id.refreshTime});

        this.setListAdapter(adapter);
    }
}
