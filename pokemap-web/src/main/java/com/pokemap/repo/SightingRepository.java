package com.pokemap.repo;

import com.pokemap.model.Sighting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Repository geruza: SQLite datu-baseko <code>sightings</code> taularekin interakzioa.
 * 
 * JdbcTemplate erabiltzen du CRUD oinarrizko eragiketetarako eta hasierako eskema sortzeko.
 */
@Repository
public class SightingRepository {

    private final JdbcTemplate jdbc;

    public SightingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

/**
 * Datu-baseko taulak eta indizeak sortzen ditu aplikazioa abiaraztean.
 * 
 * Metodo hau <code>@PostConstruct</code> bidez exekutatzen da. Taula/indizeak existitzen badira, ez du ezer apurtzen.
 */
    @PostConstruct
    public void init() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS sightings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pokemon_name TEXT NOT NULL," +
                "pokemon_id INTEGER NOT NULL," +
                "sprite_url TEXT," +
                "lat REAL NOT NULL," +
                "lon REAL NOT NULL," +
                "timestamp INTEGER NOT NULL," +
                "note TEXT" +
                ")");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_sightings_pokemon_name ON sightings(pokemon_name)");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_sightings_timestamp ON sightings(timestamp)");
    }

    private final RowMapper<Sighting> mapper = new RowMapper<Sighting>() {
        @Override public Sighting mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Sighting(
                    rs.getLong("id"),
                    rs.getString("pokemon_name"),
                    rs.getInt("pokemon_id"),
                    rs.getString("sprite_url"),
                    rs.getDouble("lat"),
                    rs.getDouble("lon"),
                    rs.getLong("timestamp"),
                    rs.getString("note")
            );
        }
    };

/**
 * Begiztatze berri bat txertatzen du eta sortutako ID-a ezartzen du.
 * @param s txertatu beharreko begiztatzea
 * @return ID-a beteta duen begiztatzea
 */
    public Sighting insert(Sighting s) {
        jdbc.update("INSERT INTO sightings(pokemon_name,pokemon_id,sprite_url,lat,lon,timestamp,note) VALUES (?,?,?,?,?,?,?)",
                s.getPokemonName(), s.getPokemonId(), s.getSpriteUrl(), s.getLat(), s.getLon(), s.getTimestamp(), s.getNote());
        Long id = jdbc.queryForObject("SELECT last_insert_rowid()", Long.class);
        s.setId(id);
        return s;
    }

/**
 * Begiztatze guztiak itzultzen ditu (timestamp DESC).
 * @return begiztatzeen zerrenda
 */
    public List<Sighting> findAll() {
        return jdbc.query("SELECT * FROM sightings ORDER BY timestamp DESC", mapper);
    }

    public List<Sighting> findByPokemon(String pokemonName) {
        return jdbc.query("SELECT * FROM sightings WHERE lower(pokemon_name)=lower(?) ORDER BY timestamp DESC",
                new Object[]{pokemonName}, mapper);
    }
}
