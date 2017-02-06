package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import parkidia.parking.a4lpmms.gestionparking_android.R;

public class ParametersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_parameters, container, false);

        return rootView;
    }
}
