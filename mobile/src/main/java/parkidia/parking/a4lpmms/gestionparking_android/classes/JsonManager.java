/*
 * "JsonManager.java"   15/02/2017
 * Parkidia
 */
package parkidia.parking.a4lpmms.gestionparking_android.classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Gère les éléments JSON de l'application
 * Encoder et décoder le JSON
 */
public class JsonManager {

    /**
     * Récupère le fichier JSON du serveur JEE
     * le décode et le transforme en Parking[]
     *
     * @return un tableau correspondant au JSON
     */
    public static ArrayList<Parking> decodeParkings(String json) {
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
     * Encode les parkings favoris au format JSON en ajoutant un élément
     * @param json Le json contenant les favoris
     * @param element L'élément à ajouter au Json
     * @return Le JSON sous forme de String
     */
    public static String encodeAddFavoris(String json, String element) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("favoris");
            JSONObject object = new JSONObject();
            object.put("nom", element);
            elements.put(object);
            Log.e("JSON", jsonObject.toString());
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e("JSON", e.getMessage());
        }
        // Si une erreur s'est produite on return null
        return null;
    }

    /**
     * Encode les parkings favoris au format JSON en supprimant un élément
     * @param json Le json contenant les favoris
     * @param element L'élément à ajouter au Json
     * @return Le JSON sous forme de String
     */
    public static String encodeRemFavoris(String json, String element) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("favoris");

            for (int i = 0; i < jsonArray.length(); i++) {
                if (element.equals(jsonArray.getJSONObject(i).getString("nom"))) {
                    jsonArray.remove(i);
                    break;
                }
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Log.e("JSON", "Format JSON incorrect");
        }
        // Si une erreur s'est produite on return null
        return null;
    }

    /**
     * Décode le json des favoris
     * @param json json à décoder
     * @return Liste des noms des parkings favoris
     */
    public static ArrayList<String> decodeFavoris(String json) {
        ArrayList<String> favoris = new ArrayList<String>();
        try {
            // Création de l'object Json
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("favoris");
            // Parcours tous les noms du Json
            for (int i = 0; i < jsonArray.length(); i++) {
                String p = jsonArray.getJSONObject(i).getString("nom");
                // Ajout du nouveau parking à la liste
                favoris.add(p);
            }
        } catch (JSONException e) {
            Log.e("JSON", "Erreur du format JSON");
        }
        return favoris;
    }
}
