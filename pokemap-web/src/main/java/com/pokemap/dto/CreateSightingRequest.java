package com.pokemap.dto;
/**
 * DTO: begiztatze bat sortzeko eskaeraren gorputza (request body).
 * 
 * Frontend-etik (Leaflet/JS) bidaltzen diren datuak jasotzen ditu.
 */


public class CreateSightingRequest {
    private String pokemon;
    private double lat;
    private double lon;
    private String note;

    public CreateSightingRequest() {}

/**
 * @return erabiltzaileak sartutako Pokemon identifikatzailea (izena edo id)
 */
    public String getPokemon() { return pokemon; }
    public void setPokemon(String pokemon) { this.pokemon = pokemon; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

/**
 * @return longitudea
 */
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
