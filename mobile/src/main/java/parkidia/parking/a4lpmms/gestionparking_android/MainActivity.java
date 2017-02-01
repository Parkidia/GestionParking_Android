package parkidia.parking.a4lpmms.gestionparking_android;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideComponent();

        fillListView();
    }


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

    /**
     * Rempli la listeView avec les données du JSON récupéré
     * TODO Récurérer ces infos du JSON
     */
    private void fillListView() {
        // Liste contenant les items
        ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        // Contient la définition de chaque item
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("nom", "IUT Parking prof");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);

        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);
        map = new HashMap<String, String>();
        map.put("nom", "IUT Parking stade");
        map.put("distance", "1km");
        map.put("refreshTime", "À l'instant");
        items.add(map);

        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), items, R.layout.item_park_nopreview,
                new String[]{"nom", "distance", "refreshTime"},
                new int[]{R.id.nomPark, R.id.distance, R.id.refreshTime});

        this.setListAdapter(adapter);
    }
}
