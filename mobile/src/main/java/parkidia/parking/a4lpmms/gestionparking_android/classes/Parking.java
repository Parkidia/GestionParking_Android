package parkidia.parking.a4lpmms.gestionparking_android.classes;

import android.graphics.Bitmap;

public class Parking {
    private String nom;
    private int places;
    private int placeDispo;
    private boolean favoris;
    private double longitude;
    private double latitude;
    private Bitmap miniature;

    public Parking(String nom, int places, int placeDispo) {
        this.nom = nom;
        this.places = places;
        this.placeDispo = placeDispo;
    }
    public Parking(String nom, int places, int placeDispo, double longitude, double latitude) {
        this.nom = nom;
        this.places = places;
        this.placeDispo = placeDispo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNom() {
        return nom;
    }

    public int getPlaces() {
        return places;
    }

    public int getPlaceDispo() {
        return placeDispo;
    }

    public boolean isFavoris() {
        return favoris;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setMiniature(Bitmap miniature) {
        this.miniature = miniature;
    }

    public Bitmap getMiniature() {
        return miniature;
    }

    @Override
    public String toString() {
        return "Parking{" +
                "nom='" + nom + '\'' +
                ", places=" + places +
                ", placeDispo=" + placeDispo +
                ", favoris=" + favoris +
                '}';
    }
}
