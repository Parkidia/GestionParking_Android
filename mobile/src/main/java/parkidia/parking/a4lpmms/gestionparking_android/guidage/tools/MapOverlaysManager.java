package parkidia.parking.a4lpmms.gestionparking_android.guidage.tools;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.classes.HTTPRequestManager;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.GuideActivity;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.DetailView;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.ParkPlace;

/**
 * Created by matthieubravo on 15/02/2017.
 */

public class MapOverlaysManager {

    /** contexte actuel de l'application */
    private Context context;

    /** objet google map construit dans l'activité principale */
    private GoogleMap map;

    /** gestionnaire des details */
    private DetailView detailView;

    /** id du parking en cours */
    private int idParking;

    /** activité actuelle */
    private GuideActivity guideActivity;

    /**
     * Gère la création des overlays représentant les voitures occupants les places
     * du parking sélectionné par l'utilisateur
     * @param context       contexte actuel de l'application
     * @param map           objet google map construit dans l'activité principale
     * @param detailView    vue gérant le détails du parking
     */
    public MapOverlaysManager(Context context, GoogleMap map, DetailView detailView, int idParking, GuideActivity guideActivity){

        this.context = context;

        this.map = map;

        this.detailView = detailView;

        this.idParking = idParking;

        this.guideActivity = guideActivity;

        decodeJson();
    }

    /**
     * décode le json recus et transforme les informations en overlays correspondants
     * transmet les informations a la vue de detail
     */
    public void decodeJson(){

        //enlever les overlays
        map.clear();

        guideActivity.addUserOverlay();

        final String[] jsonRecu = new String[1];

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //récupération des informations depuis le serveur
                try {
                    jsonRecu[0] = HTTPRequestManager.getInfoParking(idParking);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //récupération des informations du json
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


                        //orientation de la place en cours (par rapport au nord)
                        float degree = place.getInt("orientation");

                        //récupération de l'objet localisation dans place
                        //JSONObject location = place.getJSONObject("localisation");

                        //localisation de la place actuelle
                        Double lat = place.getDouble("latitude");
                        Double lng = place.getDouble("longitude");
                        System.out.println("-----------------------------------");
                        System.out.println(lat + "  " + lng + "  ");


                        //si la place est occupée alors créer une voiture et la placer sur cette dernière
                        if(!lastStatut.getBoolean("disponible")) {
                            //couleur de la voiture occupant la place
                            // [rouge, vert, bleu] -> si présent
                            // [null] -> si information vide
                            String[] carColor = lastStatut.getString("couleurVoiture").split(",");

                            //différentes composantes d'une couleurs
                            int red = -1;
                            int green = -1;
                            int blue = -1;
                            if(carColor.length == 3){
                                red = Integer.parseInt(carColor[0].trim());
                                green = Integer.parseInt(carColor[1].trim());
                                blue = Integer.parseInt(carColor[2].trim());
                            }

                            //création d'une voiture avec les informations récupérées précédemment
                            ParkPlace parkPlace = new ParkPlace(context, red, green, blue, lat, lng, degree);

                            //ajouter la voiture créée à la map
                            map.addGroundOverlay(new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromBitmap(parkPlace.getVehicule()))
                                    .bearing(parkPlace.getDegrees())
                                    .anchor(0.5f, 0.5f)
                                    .position(parkPlace.getLatLng(), parkPlace.getWIDTH(), parkPlace.getHEIGHT()));
                        } else { //si libre alors mettre un overlay vert
                            //ajouter la voiture créée à la map
                            //TODO gérer fans parkplace avec un objet par default
                            map.addGroundOverlay(new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),
                                            R.drawable.place_libre)))
                                    .bearing(degree)
                                    .anchor(0.5f, 0.5f)
                                    .position(new LatLng(lat, lng), 4.0f*0.5f, 4));
                        }

                    }

                    //informations de places du parking
                    System.out.println("jsonObj.getInt" +jsonObj.getInt("nbPlacesLibres"));
                    detailView.setPlaceTot(jsonObj.getInt("nbPlaces"));
                    detailView.setTextPlaceUsed(jsonObj.getInt("nbPlacesLibres"));

                } catch (JSONException e) {
                    //erreurs liées à la lecture du JSON
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("Erro","Something whent wrong");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
