package parkidia.parking.a4lpmms.gestionparking_android.guidage.tools;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Created by Matthieu BRAVO on 20/01/2017.
 */

public class UserLocationManager implements LocationListener {

    //TODO commenter

    private Context context;

    /**zoom auquel la map est lancée*/
    private static final int DEFAULT_ZOOM = 20;

    /**angle de vision de la map au lancement*/
    private static final int DEFAULT_TILT = 30;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;

    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    protected LocationManager locationManager;

    private GroundOverlay userPlacemark;

    private GoogleMap maps;

    /**
     * Gestion de la récupération de la position de l'utilisateur
     */
    public UserLocationManager(Context context) {

        this.context = context;
    }

    /**
     * Obtenir la localisation de l'utilisateur
     * @param updateInterval Temps entre chaque actualisation de localisation
     * @return Location, objet contenant les coordonnées de l'utilsateur
     */
    public Location getLocation(long updateInterval) {
        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        showSettingsAlert();
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateInterval, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateInterval, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateInterval, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.i("Latitude",""+latitude);
                                Log.i("longitude",""+longitude);
                            }

                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Arreter l'actualisation du GPS
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showSettingsAlert();
                return;
            }
            locationManager.removeUpdates(UserLocationManager.this);
        }
    }

    public double getLatitude()
    {
        if(location!=null)
        {
            latitude=location.getLatitude();
        }
        Log.i("Latitude",""+latitude);
        return latitude;

    }


    public double getLongitude()
    {
        if(location!=null)
        {
            longitude=location.getLongitude();
        }
        Log.i("longitude",""+longitude);
        return longitude;

    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    /**
     * Demander l'activation du GPS
     */
    public void showSettingsAlert(){
        Builder alertDialog=new Builder(context);
        alertDialog.setTitle("Localisation requise");
        alertDialog.setMessage("Le GPS n'est pas activé, il est primordial pour le bon fonctionnement de l'application");
        alertDialog.setPositiveButton("Paramètres", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    /**
     * Lorsque la localisation de l'utilisateur a changée,
     * cette méthode est appelée
     * @param location, nouvelle localisation de l'utilisateur
     */
    @Override
    public void onLocationChanged(Location location) {

        if(userPlacemark != null && maps != null) {

            LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());

            userPlacemark.setPosition(userLoc);

            //centrer la map sur la nouvelle localisation de l'utilisateur
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(userLoc)
                    .zoom(DEFAULT_ZOOM)
                    .tilt(DEFAULT_TILT)
                    .build();
            //maps.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    //----------------------------------------------------------------------------------------------
    //----------------------------------------- SETTERS --------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void setUserPlacemark(GroundOverlay userPlacemark){
        this.userPlacemark = userPlacemark;
    }

    public void setMaps(GoogleMap maps){
        this.maps = maps;
    }

}

