package parkidia.parking.a4lpmms.gestionparking_android.guidage;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import parkidia.parking.a4lpmms.gestionparking_android.R;
import parkidia.parking.a4lpmms.gestionparking_android.guidage.composants.ParkPlace;

/**
 * Created by matthieubravo on 13/02/2017.
 */

public class GuideActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guide_activity);

        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content_layout);

        final ImageView imageTest = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams paramsIT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        contentLayout.addView(imageTest);

        final SeekBar redBar = (SeekBar) findViewById(R.id.redBar);
        final SeekBar greenBar = (SeekBar) findViewById(R.id.greenBar);
        final SeekBar blueBar = (SeekBar) findViewById(R.id.blueBar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    int red = redBar.getProgress();
                    int green = greenBar.getProgress();
                    int blue = blueBar.getProgress();

                    final ParkPlace parkPlace = new ParkPlace(getApplicationContext(), red, green, blue, 10.0, 10.0, 10.0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageTest.setImageBitmap(parkPlace.getVehicule());
                        }
                    });
                    SystemClock.sleep(200);
                }
            }
        }).start();
    }
}
