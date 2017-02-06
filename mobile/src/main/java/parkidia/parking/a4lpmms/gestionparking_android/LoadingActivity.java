package parkidia.parking.a4lpmms.gestionparking_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import parkidia.parking.a4lpmms.gestionparking_android.constants.Constante;

import static java.lang.Math.round;

public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadind_layout);

        hideComponent();

        //création des constantes
        Constante constante = new Constante(this);

        ImageView logoTXT = (ImageView) findViewById(R.id.logo_text);
        int widthLT = (int)round(constante.getScreenWidth()*0.7);
        RelativeLayout.LayoutParams paramsCL = new RelativeLayout.LayoutParams(widthLT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsCL.addRule(RelativeLayout.CENTER_IN_PARENT);
        logoTXT.setLayoutParams(paramsCL);

        //point du i représenté par un logo de chargement
        ImageView loadingPoint = (ImageView) findViewById(R.id.loading_point);
        int widthLP = (int)round(widthLT*0.066);
        int xOffset = (int)round(widthLT*0.58 + (constante.getScreenWidth()-widthLT)/2);
        int yOffset = (int)round(constante.getScreenWidth()*0.7);
        RelativeLayout.LayoutParams paramsLP = new RelativeLayout.LayoutParams(widthLP, widthLP);
        paramsLP.setMargins(xOffset, yOffset, 0, 0);
        loadingPoint.setLayoutParams(paramsLP);

        //animer le chargemnt
        animLoading(loadingPoint);

        Intent intent = new Intent(this, ScreenSlidePagerActivity.class);
        startActivity(intent);
    }

    /**
     * faire tourner l'objet envoyé, en l'occurance le point de chargement
     * @param sender point de chargement
     */
    private void animLoading(final ImageView sender){
        sender.animate().rotation(359).setDuration(1000).setInterpolator(new LinearInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                sender.setRotation(0);
                animLoading(sender);
            }
        });
    }


    //////////////////////////////////

    /**
     * Cacher les barres d'interface android
     */
    private void hideComponent(){
        View decorView = getWindow().getDecorView();
        // Cache la barre de menu par défaut d'un application
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);
    }
}
