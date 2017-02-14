/*
 * "ParametersFragment.java"     02/2017
 * Parkidia 2017
 * lpmms 2016-2017
 */
package parkidia.parking.a4lpmms.gestionparking_android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import parkidia.parking.a4lpmms.gestionparking_android.R;

/**
 * Fragment de l'activité principale, dernière page
 * Propose à l'utilisateur de changer certains réglages
 * - Proximité pour les parkings proches de sa position
 * - Fréquence de rafraichissement des parkings
 * @author Guillaume BERNARD
 */
public class ParametersFragment extends Fragment {

    /**
     * Initialise le fragment
     * @param inflater inflater
     * @param container conteneur
     * @param savedInstanceState Etat de l'instance
     * @return le fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_parameters, container, false);

        return rootView;
    }
}
