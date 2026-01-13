package com.pokemap.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.pokemap.model.PokemonInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Kanpoko API bezeroa: PokeAPI kontsultatzeko osagaia.
 * 
 * HTTP GET bidez <code>https://pokeapi.co/api/v2/pokemon/{nameOrId}</code> endpoint-a deitzen du eta
 * aplikazioak behar duen informazio minimoa mapatzen du ({@link com.pokemap.model.PokemonInfo}).
 */
@Component
public class PokeApiClient {

/**
 * PokeAPI-tik Pokémon baten informazioa eskuratzen du (izena edo ID-a erabilita).
 * 
 * @param nameOrId pokemonaren izena edo ID-a
 * @return lortutako {@link com.pokemap.model.PokemonInfo}
 * @throws IllegalArgumentException parametroa hutsik bada
 * @throws RuntimeException PokeAPI-k 200 ez den egoera itzultzen badu
 * @throws Exception I/O edo parsing arazoengatik
 */
    public PokemonInfo fetchPokemon(String nameOrId) throws Exception {
        String key = (nameOrId == null) ? "" : nameOrId.trim().toLowerCase();
        if (key.isEmpty()) throw new IllegalArgumentException("pokemon vacío");

        URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + key);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int code = con.getResponseCode();
        if (code != 200) {
            throw new RuntimeException("No encontrado en PokeAPI (status " + code + ")");
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JSONObject json = new JSONObject(sb.toString());
        int id = json.getInt("id");
        String name = json.getString("name");

        String sprite = null;
        try {
            sprite = json.getJSONObject("sprites")
                    .getJSONObject("other")
                    .getJSONObject("official-artwork")
                    .optString("front_default", null);
        } catch (Exception ignored) {}

        if (sprite == null || sprite.isEmpty()) {
            sprite = json.getJSONObject("sprites").optString("front_default", null);
        }

        return new PokemonInfo(id, name, sprite);
    }

}
