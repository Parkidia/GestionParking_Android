package parkidia.parking.a4lpmms.gestionparking_android.classes;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Parking implements Serializable {
    private String nom;
    private int places;
    private int placeDispo;
    private int id;
    private boolean favoris;
    private double longitude;
    private double latitude;
    private Bitmap miniature;

    public Parking(String nom, int places, int placeDispo) {
        this.nom = nom;
        this.places = places;
        this.placeDispo = placeDispo;
    }
    public Parking(String nom, int places, int placeDispo, double longitude, double latitude, int id) {
        this.nom = nom;
        this.places = places;
        this.placeDispo = placeDispo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
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
