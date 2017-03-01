package parkidia.parking.a4lpmms.gestionparking_android.guidage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.classes.Parking;
import parkidia.parking.a4lpmms.gestionparking_android.constants.Constante;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.DetailView;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.ParkPlace;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.tools.MapOverlaysManager;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.tools.UserLocationManager;

/**
 * Created by matthieubravo on 13/02/2017.
 */

public class GuideActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    /**zoom auquel la map est lancée*/
    private static final int DEFAULT_ZOOM = 20;

    /**angle de vision de la map au lancement*/
    private static final int DEFAULT_TILT = 30;

    /**zoom minimum autorisé sur la map*/
    private static final int MAX_ZOOM = 25;
    private static final long INTERVAL_ACTU = 50;

    /**objet google map*/
    private GoogleMap map;

    /**définition de la localisation*/
    private LatLng userLocation;

    /**objet représentant l'image de l'utilisateur*/
    private ParkPlace userCarIMG;

    /**gestion de la boussole de l'appareil*/
    private SensorManager compassManager;

    /**ecouteur de changemen de localisation*/
    private LocationListener listener;

    /**vue de conteneur de la map*/
    private RelativeLayout mapsViewLayout;

    /** gestionnaire des details */
    private DetailView detailView;

    /** parking actuel */
    private Parking parking;

    /** gestionnaire d'overlay */
    private MapOverlaysManager mapOverlaysManager;

    /** gestionnaire de location */
    private UserLocationManager localisation;

    /**
     * Overlay de map représentant la position de l'utilisateur
     * utilisation d'un overlay plutot qu'un placemark
     * car il ne réagit pas au zoom et au dézoom
     * permettant ainsi de simuler la position à l'échelle
     * de la voiture de l'utilisateur sur la route
     */
    private static GroundOverlay userLocOverlay;

    /**
     * Activité guidant l'utilisateur dans sa quete d'une place libre.
     * Elle est composée d'une map et d'overlays représentant les
     * voitures du parking
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialisation des constantes
        Constante constante = new Constante(this);

        //demander la permission a l'utilisateur pour la localisation
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        //initilisation du layout de contenu de l'activité
        setContentView(R.layout.maps_view);

        RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.mapsView);

        //définir le manager de la boussole
        compassManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassManager.registerListener(this, compassManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

        //création de la voiture représentant l'utilisateur
        //réutilisation de la classe de création des voitures de parking
        userCarIMG = new ParkPlace(getApplicationContext(), 255, 255, 255, 0, 0, 0);

        //récupérer la vue conteneur
        mapsViewLayout = (RelativeLayout) findViewById(R.id.mapsView);

        //récupération des elements envoyé par l'activité précédente
        Intent intent = getIntent();

        this.parking = (Parking) intent.getSerializableExtra("parking");

        //création du layout de detail du parking
        this.detailView = new DetailView(getApplicationContext(), parking, this);
        LinearLayout.LayoutParams paramsDV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.detailView.setPadding(30,0,30,0);
        this.detailView.setLayoutParams(paramsDV);

        contentLayout.addView(detailView);
    }

    /**
     * appelé lors de la fin de création de la map
     * @param googleMap objet map construit
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        map.setMaxZoomPreference(MAX_ZOOM);

        //récupération des coordonées de l'utilsateur
        localisation = new UserLocationManager(this);
        Location posCurr = localisation.getLocation(INTERVAL_ACTU);
        userLocation = new LatLng(localisation.getLatitude(), localisation.getLongitude());

        //caché les interactions par défault de la map
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(false);

        //décalage du logo Google pour qu'il ne soit pas caché par l'interface
        //et etre ainsi dans la légalité
        map.setPadding(0, 0, 0, 100);

        //passer en mode satellite la map
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //ajout de la voiture représentant l'utilsateur
        addUserOverlay();

        // Construie une caméra avec les paramètres précédement définit
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(DEFAULT_ZOOM)
                .tilt(DEFAULT_TILT)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mapOverlaysManager = new MapOverlaysManager(getApplicationContext(), map, detailView, parking.getId(), this);

        //passer l'objet au gestionnaire de localisation pour gérer l'acutalisation de la position
        localisation.setUserPlacemark(userLocOverlay);
        localisation.setMaps(map);

    }

    /**
     * Appelé lors de la réponse de l'utilisateur a une autorisation
     * en l'occurance l'utilisation de la localisation de son appareil
     * @param requestCode
     * @param permissions ,  permission demandée
     * @param grantResults , réponse de l'utilisateur
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                //si l'utilisateur a annulé, le tableau est vide
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //récupération du fragment représentant la map
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                } else {
                    Toast.makeText(this, "L'application nécessite la localisation pour fonctionner", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    /**
     * Appelé lors d'un changement de capteur de
     * positionnement dans l'espace de l'appareil,
     * en l'occurance la boussole
     * @param event
     */
    @Override
    public void onSensorChanged(final SensorEvent event) {
        if(userLocOverlay != null){
            //récupération de la valeur de la boussole
            float degree = Math.round(event.values[0]);

            //définir la nouvelle orientation du point de l'utilisateur
            userLocOverlay.setBearing(degree);
        }
    }

    /**
     * ajouter l'overlay de l'utilisateur
     */
    public void addUserOverlay(){
        System.out.println("bite");

        //création de la voiture représentant l'utilisateur
        Bitmap userCarBitMap = userCarIMG.getVehicule();

        userLocOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(userCarBitMap))
                .bearing(0.0f) //rotation par default de 0 degres
                .anchor(0.5f,0.5f) //mettre le point d'ancrage au milieu de l'image
                .position(userLocation, userCarIMG.getWIDTH(), userCarIMG.getHEIGHT()));

        localisation.setUserPlacemark(userLocOverlay);

        // Construie une caméra avec les paramètres précédement définit
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(DEFAULT_ZOOM)
                .tilt(DEFAULT_TILT)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * pas utilisé
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * recharger la carte
     */
    public void refreshMap() {
        mapOverlaysManager.decodeJson();
    }
}