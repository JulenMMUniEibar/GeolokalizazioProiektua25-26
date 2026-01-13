package geo_project.model;

import java.time.Instant;

public class Sighting {
    private long id;
    private String pokemonName;
    private int pokemonId;
    private String spriteUrl;
    private double lat;
    private double lon;
    private long timestamp;
    private String note;

    public Sighting() {}

    public Sighting(String pokemonName, int pokemonId, String spriteUrl,
                    double lat, double lon, long timestamp, String note) {
        this.pokemonName = pokemonName;
        this.pokemonId = pokemonId;
        this.spriteUrl = spriteUrl;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPokemonName() { return pokemonName; }
    public int getPokemonId() { return pokemonId; }
    public String getSpriteUrl() { return spriteUrl; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public long getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        String when = Instant.ofEpochMilli(timestamp).toString();
        return pokemonName + " (#" + pokemonId + ") @ " + lat + "," + lon + " - " + when;
    }
}
