package parkidia.parking.a4lpmms.gestionparking_android.guidage.composants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import parkidia.parking.a4lpmms.gestionparking_android.R;

/**
 * Created by matthieubravo on 13/02/2017.
 */

public class ParkPlace {

    /** Contexte de l'application */
    private final Context context;

    /** Alpha utilisé pour colorier la carroserie du véhicule (sur 250, mais pourquoi ...) */
    private final int ALPHA = 190;

    /** taille d'un place */
    private static final float HEIGHT = 4;
    private static final float WIDTH = HEIGHT * 0.5f;

    /** bases de véhicules soit la carrosserie */
    private final int[] vehiculesBodyWorks = {R.drawable.cars1_bodywork, R.drawable.cars2_bodywork,
            R.drawable.cars3_bodywork, R.drawable.cars4_bodywork,
            R.drawable.cars5_bodywork, R.drawable.cars6_bodywork};

    /** parties du véhicule a ne pas colorier */
    private final int[] vehiculesOverlays = {R.drawable.cars1_overlay, R.drawable.cars2_overlay,
            R.drawable.cars3_overlay, R.drawable.cars4_overlay,
            R.drawable.cars5_overlay, R.drawable.cars6_overlay};

    /** couleurs du véhicule occupant la place */
    private int red;
    private int green;
    private int blue;

    /** localisation */
    private double latitude;
    private double longitude;

    /** orientation par rapport au nord */
    private float degrees = 0.0f;

    /** bitmap représentant la voiture présente sur la place */
    private Bitmap vehicule;

    /**
     * Instanciation d'un objet place
     *
     * @param context
     * @param red       int représentant le taux de rouge dans la couleur du véhicule, sur 255.
     * @param green     int représentant le taux de vert dans la couleur du véhicule, sur 255.
     * @param blue      int représentant le taux de bleu dans la couleur du véhicule, sur 255.
     * @param latitude  double positionnant la place sur la latitude
     * @param longitude double positionnant la place sur la longitude
     * @param degrees   double représentant l'orientation de la place par rapport au nord
     */
    public ParkPlace(Context context, int red, int green, int blue, double latitude, double longitude, float degrees) {

        this.context = context;

        this.red = red;
        this.green = green;
        this.blue = blue;

        this.latitude = latitude;
        this.longitude = longitude;

        this.degrees = degrees;

        vehicule = buildVehicule();
    }

    /**
     * Construire aléatoirement un véhicule avec les couleurs appropriées
     * @return un bitmap représentant le véhicule
     */
    private Bitmap buildVehicule() {

        //layout params de travail
        LinearLayout.LayoutParams paramsMP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //choisir aléatoirement la représentation du véhicule
        int randomIndex = (int) (Math.random() * (vehiculesBodyWorks.length - 1));

        //image de carrosserie
        ImageView bodyWork = new ImageView(context);
        bodyWork.setLayoutParams(paramsMP);
        bodyWork.setImageResource(vehiculesBodyWorks[randomIndex]);

        //coloriser la carroserie
        bodyWork.setColorFilter(Color.argb(ALPHA, red, green, blue));

        //image d'overlay
        ImageView overlayIMG = new ImageView(context);
        overlayIMG.setLayoutParams(paramsMP);
        overlayIMG.setImageResource(vehiculesOverlays[randomIndex]);

        //vue regroupant les différentes images
        FrameLayout compositeImage = new FrameLayout(context);
        compositeImage.setLayoutParams(paramsMP);
        compositeImage.addView(bodyWork);
        compositeImage.addView(overlayIMG);

        //travail d'obtention du bitmap de la vue
        compositeImage.setDrawingCacheEnabled(true);
        compositeImage.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        compositeImage.buildDrawingCache(true);
        compositeImage.layout(0, 0, compositeImage.getMeasuredWidth(), compositeImage.getMeasuredHeight());

        return Bitmap.createBitmap(compositeImage.getDrawingCache());
    }

    //----------------------------------------------------------------------------------------------
    //---------------------------------------- GETTERS ---------------------------------------------
    //----------------------------------------------------------------------------------------------

    public Bitmap getVehicule() {
        return vehicule;
    }

    public static float getHEIGHT() {
        return HEIGHT;
    }

    public static float getWIDTH() {
        return WIDTH;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public float getDegrees() {
        return degrees;
    }
}
