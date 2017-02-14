package parkidia.parking.a4lpmms.gestionparking_android.guidage.composants;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import parkidia.parking.a4lpmms.gestionparking_android.R;

/**
 * Created by matthieubravo on 13/02/2017.
 */

public class ParkPlace {

    /**Contexte de l'application*/
    private Context context;

    /**couleurs du véhicule occupant la place*/
    private int red = -1;
    private int green = -1;
    private int blue = -1;

    /**Alpha utilisé pour colorier la carroserie du véhicule (sur 250, mais pourquoi ...)*/
    private final int ALPHA = 190;

    /**localisation*/
    private double latitude = -1;
    private double longitude = -1;

    /**orientation par rapport au nord*/
    private double degrees = 0.0;

    /**bases de véhicules*/
    private int[] vehiculesBodyWorks = {R.drawable.cars1_bodywork, R.drawable.cars2_bodywork,
                                        R.drawable.cars3_bodywork, R.drawable.cars4_bodywork,
                                        R.drawable.cars5_bodywork, R.drawable.cars6_bodywork};

    /**parties du véhicule a ne pas colorier*/
    private int[] vehiculesOverlays = {R.drawable.cars1_overlay, R.drawable.cars2_overlay,
                                       R.drawable.cars3_overlay, R.drawable.cars4_overlay,
                                       R.drawable.cars5_overlay, R.drawable.cars6_overlay};

    /**bitmap représentant la voiture présente sur la place*/
    private Bitmap vehicule;

    /**
     * Instanciation d'un objet place
     * @param context
     * @param red int représentant le taux de rouge dans la couleur du véhicule, sur 255.
     * @param green int représentant le taux de vert dans la couleur du véhicule, sur 255.
     * @param blue int représentant le taux de bleu dans la couleur du véhicule, sur 255.
     * @param latitude double positionnant la place sur la latitude
     * @param longitude double positionnant la place sur la longitude
     * @param degrees double représentant l'orientation de la place par rapport au nord
     */
    public ParkPlace(Context context, int red, int green, int blue, double latitude, double longitude, double degrees) {

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
    private Bitmap buildVehicule(){

        //layout params de travail
        LinearLayout.LayoutParams paramsMP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        //choisir aléatoirement la représentation du véhicule
        int randomIndex = (int)(Math.random() * (vehiculesBodyWorks.length-1));

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

        System.out.println(Bitmap.createBitmap(compositeImage.getDrawingCache()));

        return Bitmap.createBitmap(compositeImage.getDrawingCache());
    }

    //----------------------------------------------------------------------------------------------
    //---------------------------------------- GETTERS ---------------------------------------------
    //----------------------------------------------------------------------------------------------

    public Bitmap getVehicule() {
        return vehicule;
    }
}
