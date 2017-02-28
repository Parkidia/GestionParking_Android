package parkidia.parking.a4lpmms.gestionparking_android.guidage.tools;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import parkidia.parking.a4lpmms.gestionparking_android.classes.HTTPRequestManager;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.DetailView;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.ParkPlace;

/**
 * Created by matthieubravo on 15/02/2017.
 */

public class MapOverlaysManager {

    /**contexte actuel de l'application*/
    private Context context;

    /**objet google map construit dans l'activité principale*/
    private GoogleMap map;

    /** gestionnaire des details */
    private DetailView detailView;

    /**
     * Gère la création des overlays représentant les voitures occupants les places
     * du parking sélectionné par l'utilisateur
     * @param context       contexte actuel de l'application
     * @param map           objet google map construit dans l'activité principale
     * @param detailView    vue gérant le détails du parking
     */
    public MapOverlaysManager(Context context, GoogleMap map, DetailView detailView){

        this.context = context;

        this.map = map;

        this.detailView = detailView;

        decodeJson();
    }

    /**
     * décode le json recus et transforme les informations en overlays correspondants
     * transmet les informations a la vue de detail
     */
    private void decodeJson(){

        final String[] jsonRecu = new String[1];

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    jsonRecu[0] = HTTPRequestManager.getInfoParking(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try {

                    //transformation de la chaine de charactères en objet JSON
                    JSONObject jsonObj = new JSONObject(jsonRecu[0]);

                    //récupérer tout les objets places
                    JSONArray places = jsonObj.getJSONArray("places");

                    // boucler sur toutes les places du tableau JSON
                    for (int i = 0; i < places.length(); i++) {

                        //récupéraiton d'une place à l'index en cours
                        JSONObject place = places.getJSONObject(i);

                        //récupération de l'objet dernierStatut dans l'objet place
                        JSONObject lastStatut = place.getJSONObject("dernierStatut");

                        System.out.println("---------------------------------------------------------------");
                        System.out.println(lastStatut.getBoolean("disponible"));

                        //si la place est occupée alors créer une voiture et la placer sur cette dernière
                        if(lastStatut.getBoolean("disponible")) {

                            //orientation de la place en cours (par rapport au nord)
                            float degree = place.getInt("orientation");

                            //couleur de la voiture occupant la place
                            // [rouge, vert, bleu] -> si présent
                            // [null] -> si information vide
                            String[] carColor = lastStatut.getString("couleurVoiture").split(",");

                            //différentes composantes d'une couleurs
                            int red = -1;
                            int green = -1;
                            int blue = -1;
                            if(carColor.length == 3){
                                red = Integer.parseInt(carColor[0]);
                                green = Integer.parseInt(carColor[1]);
                                blue = Integer.parseInt(carColor[2]);
                            }

                            //récupération de l'objet localisation dans place
                            JSONObject location = place.getJSONObject("localisation");

                            //localisation de la place actuelle
                            Double lat = location.getDouble("latitude");
                            Double lng = location.getDouble("longitude");
                            System.out.println("-----------------------------------");
                            System.out.println(lat + "  " + lng + "  ");

                            //création d'une voiture avec les informations récupérées précédemment
                            ParkPlace parkPlace = new ParkPlace(context, red, green, blue, lat, lng, degree);

                            //ajouter la voiture créée à la map
                            map.addGroundOverlay(new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromBitmap(parkPlace.getVehicule()))
                                    .bearing(parkPlace.getDegrees())
                                    .anchor(0.5f, 0.5f)
                                    .position(parkPlace.getLatLng(), parkPlace.getWIDTH(), parkPlace.getHEIGHT()));
                        }

                    }

                    //informations de places du parking
                    detailView.setTextPlaceTot(jsonObj.getInt("nbPlacesLibres"));
                    detailView.setTextPlaceUsed(jsonObj.getInt("nbPlaces"));

                } catch (JSONException e) {
                    //erreurs liées à la lecture du JSON
                    e.printStackTrace();
                }
            }
        };

        /*String jsonRecu =

        //simulation du json recsu
        String jsonRecu = "{\n" +
                "    \"latitude\": 11,\n" +
                "    \"longitude\": 12,\n" +
                "    \"id\": 1,\n" +
                "    \"nom\": \"IUT de rodez\",\n" +
                "    \"places\": [\n" +
                "        {\n" +
                "            \"minX\": 10,\n" +
                "            \"minY\": 11,\n" +
                "            \"maxX\": 12,\n" +
                "            \"maxY\": 13,\n" +
                "            \"orientation\": 217,\n" +
                "            \"nom\": \"P01\",\n" +
                "            \"handicapee\": false,\n" +
                "            \"dernierStatut\": {\n" +
                "                \"date\": 1487014354986,\n" +
                "                \"couleurVoiture\": \"12,12,12\",\n" +
                "                \"disponible\": true\n" +
                "            },\n" +
                "            \"localisation\": {\n" +
                "                \"latitude\": 44.360105,\n" +
                "                \"longitude\": 2.577220\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"minX\": 10,\n" +
                "            \"minY\": 11,\n" +
                "            \"maxX\": 12,\n" +
                "            \"maxY\": 13,\n" +
                "            \"orientation\": 157,\n" +
                "            \"nom\": \"P02\",\n" +
                "            \"handicapee\": true,\n" +
                "            \"dernierStatut\": {\n" +
                "                \"date\": 1487170140472,\n" +
                "                \"couleurVoiture\": \"200,0,90\",\n" +
                "                \"disponible\": true\n" +
                "            },\n" +
                "            \"localisation\": {\n" +
                "                \"latitude\": 44.359851,\n" +
                "                \"longitude\": 2.577702\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"minX\": 14,\n" +
                "            \"minY\": 15,\n" +
                "            \"maxX\": 16,\n" +
                "            \"maxY\": 18,\n" +
                "            \"orientation\": 157,\n" +
                "            \"nom\": \"P03\",\n" +
                "            \"handicapee\": true,\n" +
                "            \"dernierStatut\": {\n" +
                "                \"date\": 1487170160471,\n" +
                "                \"couleurVoiture\": \"200,200,200\",\n" +
                "                \"disponible\": true\n" +
                "            },\n" +
                "            \"localisation\": {\n" +
                "                \"latitude\": 44.359857,\n" +
                "                \"longitude\": 2.577727\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"nbPlaces\": 3,\n" +
                "    \"nbPlacesLibres\": 3\n" +
                "}";

*/
    }
}
