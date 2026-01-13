package geo_project.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import geo_project.model.Sighting;

public class SightingDao {

    public long insert(Sighting s) throws Exception {
        String sql = "INSERT INTO sightings(pokemon_name,pokemon_id,sprite_url,lat,lon,timestamp,note) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getPokemonName());
            ps.setInt(2, s.getPokemonId());
            ps.setString(3, s.getSpriteUrl());
            ps.setDouble(4, s.getLat());
            ps.setDouble(5, s.getLon());
            ps.setLong(6, s.getTimestamp());
            ps.setString(7, s.getNote());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return -1;
        }
    }

    public List<Sighting> listAll() throws Exception {
        String sql = "SELECT * FROM sightings ORDER BY timestamp DESC";
        return query(sql, null);
    }

    public List<Sighting> listByPokemon(String pokemonNameLower) throws Exception {
        String sql = "SELECT * FROM sightings WHERE lower(pokemon_name)=? ORDER BY timestamp DESC";
        return query(sql, pokemonNameLower.toLowerCase());
    }

    private List<Sighting> query(String sql, String param) throws Exception {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (param != null) ps.setString(1, param);

            try (ResultSet rs = ps.executeQuery()) {
                List<Sighting> out = new ArrayList<>();
                while (rs.next()) {
                    Sighting s = new Sighting(
                            rs.getString("pokemon_name"),
                            rs.getInt("pokemon_id"),
                            rs.getString("sprite_url"),
                            rs.getDouble("lat"),
                            rs.getDouble("lon"),
                            rs.getLong("timestamp"),
                            rs.getString("note")
                    );
                    s.setId(rs.getLong("id"));
                    out.add(s);
                }
                return out;
            }
        }
    }
}
