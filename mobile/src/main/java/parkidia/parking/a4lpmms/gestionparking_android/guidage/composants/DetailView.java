package parkidia.parking.a4lpmms.gestionparking_android.guidage.composants;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.classes.HTTPRequestManager;
import parkidia.parking.a4lpmms.gestionparking_android.classes.Parking;
import parkidia.parking.a4lpmms.gestionparking_android.constants.Constante;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.GuideActivity;

import static java.lang.Math.round;

/**
 * Created by matthieubravo on 21/02/2017.
 */

public class DetailView extends LinearLayout implements View.OnTouchListener, View.OnClickListener {

    /** Base du temps d'acualisation */
    private static final String LAST_ACTU_BASE = "Il y a ";

    /** temps depuis la dernière actu */
    private int lastActuTime = 0;

    /** Unité pour la durée d'actualisation */
    private static final String UNIT_TIME = " min";

    /** pourcentage de la vue avant qu'elle bascule en ouvert / fermé */
    private static final float BASCUL_RATIO = 1.2f;

    /** activité de la vue */
    private GuideActivity guideActivity;

    /** constante */
    private static final Constante constante = new Constante();

    /** Titre du parking */
    private static TextView titleParking;

    /** Temps de dernière actualisation */
    private static TextView lastActu;

    /** Étoile de favoris */
    private static ImageButton favorisBt;

    /** Barre de délimitation */
    private static View delimView;

    /** indicateur de places libres */
    private static TextView parkPlaceFree;

    /** indicateur de total de place */
    private static TextView parkPlaceTotal;

    /** bouton de rafraichisement des données */
    private static Button refrechBt;

    /** coordonée enregistrée lors du down sur la vue */
    private static int yBase = Integer.MAX_VALUE;
    private static float yBaseView;

    /** Offset en cours lors du scroll */
    private static int offset;

    /** Bascule d'ouverture de vue */
    private static boolean isOpen = false;

    /** Offset du début lorsque la vue est d-fermée */
    private static float yDelimOffset;

    /** nombre de places totale du parking */
    private int totalPlaces = 1;

    public DetailView(Context context, Parking parking, GuideActivity guideActivity) {
        super(context);

        setOrientation(VERTICAL);

        this.guideActivity = guideActivity;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.detail_layout, null);

        //récupération des éléments créés dans le XML
        //----
        titleParking = (TextView) view.findViewById(R.id.titleParking);

        lastActu = (TextView) view.findViewById(R.id.actuTV);

        favorisBt = (ImageButton) view.findViewById(R.id.favBt);

        delimView = view.findViewById(R.id.delimViewL);

        parkPlaceFree = (TextView) view.findViewById(R.id.nbPlaces);
        setTextPlaceUsed(0);

        parkPlaceTotal = (TextView) view.findViewById(R.id.nbTotPlaces);

        refrechBt = (Button) view.findViewById(R.id.refreshBT);
        refrechBt.setOnClickListener(this);
        //----

        //remplir les données
        //----
        titleParking.setText(parking.getNom());

        lastActu.setText(LAST_ACTU_BASE + lastActuTime + UNIT_TIME);

        System.out.println("is favoris " + parking.isFavoris());

        setStateFavorisBt(parking.isFavoris());
        //----

        //récupérer les actions de touché pour la gesiton du scroll
        setOnTouchListener(this);

        addView(view);

        //attendre la création de la vue avant de récupérer le Y de la vue de délimitation
        //et de positionner au bon endrait la vue de detail
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                yDelimOffset = constante.getScreenHeight() - (int) delimView.getY();

                setY(yDelimOffset);

                yBaseView = getY();
            }
        }, 100);


        timer();
    }



    /**
     * Gestionnaire de touché afin de gérer le scroll de la vue.
     * Gère la bascule ouvert/fermé de la vue
     * @param v     vue touchée
     * @param event événement effectué
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //y du touché
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                //définir les valeurs de base lors de l'ouverture
                if(isOpen) {
                    yBase = Y;
                    yBaseView = getY();
                }
                break;
            case MotionEvent.ACTION_UP:

                //y lorsque la vue est ouverte
                float yOpen = constante.getScreenHeight()-getHeight();

                //point de rupture
                if(getY() <= yBaseView * BASCUL_RATIO){

                    //animer jusqu'a l'ouverture complète
                    animate().y(yOpen).setDuration(300);

                    isOpen = true;

                } else {

                    //animer jusqu'a la fermeture complete
                    animate().y(yDelimOffset).setDuration(300);

                    isOpen = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                //calcul du déplacement du doigt
                offset = yBase-Y;

                //Y a definir a la vue selon l'offset
                float newY = constante.getScreenHeight() - getHeight() - offset;

                //Y maximum que la vue ne doit pas dépasser
                float maxY = constante.getScreenHeight() - getHeight();

                //si le newY ne dépasse pas maxY (inversé car on part du haut de l'écran)
                //alors bouger la vue sinon la bloquer au maximum de l'ouverture
                if(newY > maxY){
                    setY(newY);
                } else if(newY > 0) {
                    setY(constante.getScreenHeight() - getHeight());
                }
                break;
        }
        return true;
    }

    /**
     * timer d'actualisation
     */
    public void timer(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    setTextLastActu();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //----------------------------------------------------------------------------------------------
    //--------------------------------------- SETTERS ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void setTextTitleParking(String titleParkingS) {
        this.titleParking.setText(titleParkingS);
    }

    public void setTextLastActu() {

        lastActuTime++;

        guideActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastActu.setText(LAST_ACTU_BASE + lastActuTime + UNIT_TIME);
            }
        });
    }

    public void setStateFavorisBt(Boolean fav) {
        if (fav) {
            this.favorisBt.setImageResource(R.drawable.star_favori_full);
        } else {
            this.favorisBt.setImageResource(R.drawable.star_favori);
        }
    }

    public void setTextPlaceUsed(int nbPlaces){

        System.out.println("iorzeurozeuoizeuoirzuoiruzoieurzeoiruzoieurzieuroizuroizuoizeuorizu"+((float)nbPlaces*100)/(float)totalPlaces);

        if(((float)nbPlaces*100)/(float)totalPlaces <= 40){
            parkPlaceFree.setTextColor(getResources().getColor(R.color.greenColor));
        } else if(nbPlaces*100/totalPlaces <= 75) {
            parkPlaceFree.setTextColor(getResources().getColor(R.color.yellowColor));
        } else {
            parkPlaceFree.setTextColor(getResources().getColor(R.color.greenColor));
        }
        parkPlaceFree.setText(String.valueOf(nbPlaces));
    }

    public void setTextPlaceTot(int nbPlaces){
        totalPlaces = nbPlaces;
        parkPlaceTotal.setText("/"+String.valueOf(nbPlaces));
    }

    @Override
    public void onClick(View v) {
        guideActivity.refreshMap();
    }
}