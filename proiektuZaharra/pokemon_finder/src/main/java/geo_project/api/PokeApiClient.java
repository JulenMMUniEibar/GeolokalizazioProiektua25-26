package geo_project.api;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiClient {

    // ✅ Tiene que estar aquí dentro
    public static record PokemonInfo(int id, String name, String spriteUrl) {}

    private final HttpClient http = HttpClient.newHttpClient();

    public PokemonInfo fetchPokemon(String nameOrId) throws Exception {
        String key = nameOrId.trim().toLowerCase();
        if (key.isBlank()) throw new IllegalArgumentException("Pokemon vacío");

        URI uri = URI.create("https://pokeapi.co/api/v2/pokemon/" + key);
        HttpRequest req = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("No encontrado en PokeAPI (status " + res.statusCode() + ")");
        }

        JSONObject json = new JSONObject(res.body());
        int id = json.getInt("id");
        String name = json.getString("name");

        String sprite = null;
        try {
            sprite = json.getJSONObject("sprites")
                    .getJSONObject("other")
                    .getJSONObject("official-artwork")
                    .optString("front_default", null);
        } catch (Exception ignored) {}

        if (sprite == null) {
            sprite = json.getJSONObject("sprites").optString("front_default", null);
        }

        return new PokemonInfo(id, name, sprite);
    }
}
