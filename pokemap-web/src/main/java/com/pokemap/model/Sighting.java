package com.pokemap.model;
/**
 * Entitatea / model-a: Pokémon begiztatze bat (sighting).
 * 
 * SQLite datu-baseko <code>sightings</code> taularekin lerrokatuta dago.
 */


public class Sighting {
    private Long id;
    private String pokemonName;
    private int pokemonId;
    private String spriteUrl;
    private double lat;
    private double lon;
    private long timestamp;
    private String note;

/**
 * Sighting huts bat sortzen du (Spring/JDBC-rako).
 */
    public Sighting() {}

    public Sighting(Long id, String pokemonName, int pokemonId, String spriteUrl,
                    double lat, double lon, long timestamp, String note) {
        this.id = id;
        this.pokemonName = pokemonName;
        this.pokemonId = pokemonId;
        this.spriteUrl = spriteUrl;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPokemonName() { return pokemonName; }
    public void setPokemonName(String pokemonName) { this.pokemonName = pokemonName; }

    public int getPokemonId() { return pokemonId; }
    public void setPokemonId(int pokemonId) { this.pokemonId = pokemonId; }

    public String getSpriteUrl() { return spriteUrl; }
    public void setSpriteUrl(String spriteUrl) { this.spriteUrl = spriteUrl; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
