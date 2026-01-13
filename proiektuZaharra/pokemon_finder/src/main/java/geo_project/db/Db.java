package geo_project.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Db {
    private static final String DB_URL = "jdbc:sqlite:pokemap.db";

    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL);
        init(conn);
        return conn;
    }

    private static void init(Connection conn) throws Exception {
    String sql = """
        CREATE TABLE IF NOT EXISTS sightings (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          pokemon_name TEXT NOT NULL,
          pokemon_id INTEGER NOT NULL,
          sprite_url TEXT,
          lat REAL NOT NULL,
          lon REAL NOT NULL,
          timestamp INTEGER NOT NULL,
          note TEXT
        );
        """;

    try (Statement st = conn.createStatement()) {
        st.execute(sql);
    }
}
}
