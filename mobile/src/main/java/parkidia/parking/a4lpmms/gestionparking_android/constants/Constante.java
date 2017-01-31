package parkidia.parking.a4lpmms.gestionparking_android.constants;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class Constante {

    /**taille d'écran*/
    private static int screenHeight = 0;
    private static int screenWidth = 0;

    /**Contexte de l'application*/
    private static Context context;

    /**Exécutable après l'initialisation*/
    public Constante(){
    }

    /**Exècuté en premier pour initialiser les variables*/
    public Constante(Activity actualAcivity){

        context = actualAcivity.getApplicationContext();

        /////////////////////////////////////Taille d'écran/////////////////////////////////////////
        DisplayMetrics metrics = new DisplayMetrics();
        actualAcivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        ////////////////////////////////////////////////////////////////////////////////////////////
    }

    public int getScreenHeight(){
        return screenHeight;
    }
    public int getScreenWidth(){
        return screenWidth;
    }

    public Context getContext(){
        return context;
    }

}
