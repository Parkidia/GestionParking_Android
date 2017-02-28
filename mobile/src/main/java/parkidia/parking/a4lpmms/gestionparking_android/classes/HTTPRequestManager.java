package parkidia.parking.a4lpmms.gestionparking_android.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Gère les requetes http de l'application
 */
public class HTTPRequestManager {

    /** racine de l'adresse du serveur */
    public static final String URL_SERVEUR = "http://192.168.1.10:8080/GestionParking_war_exploded/rest/";

    /**
     * Singleton
     */
    private HTTPRequestManager() {
    }

    /**
     * Récupère les informations concernant un parking dans une JSON
     *
     * @return Le JSON récupéré depuis le serveur JEE
     * @throws IOException S'il y a un problème lors de la communication
     */
    public static String getListeParkings() throws IOException {
        URL url = null;
        HttpURLConnection http = null;

        try {
            url = new URL(URL_SERVEUR + "parking");
            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String ligne = "";
            StringBuilder reponse = new StringBuilder();

            while ((ligne = reader.readLine()) != null) {
                reponse.append(ligne);
            }
            return reponse.toString();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }


    /**
     * Récupère les informations concernant un parking dans une JSON
     *
     * @return Le JSON récupéré depuis le serveur JEE
     * @throws IOException S'il y a un problème lors de la communication
     */
    public static String getInfoParking(int parkingId) throws IOException {
        URL url = null;
        HttpURLConnection http = null;

        try {
            url = new URL(URL_SERVEUR + "parking/" + parkingId);
            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String ligne = "";
            StringBuilder reponse = new StringBuilder();

            while ((ligne = reader.readLine()) != null) {
                reponse.append(ligne);
            }
            return reponse.toString();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Récupère une miniature du parking
     * @param parkingId numero de parking
     * @return l'image en Bitmap
     * @throws IOException
     */
    public static Bitmap getMiniature(int parkingId) throws IOException {
        URL url = null;
        HttpURLConnection http = null;

        try {
            url = new URL(URL_SERVEUR + "parking/photo/" + parkingId);
            http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);

            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(http.getInputStream()), 675, 550, false);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}
