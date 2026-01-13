package com.pokemap.service;

import com.pokemap.dto.CreateSightingRequest;
import com.pokemap.model.PokemonInfo;
import com.pokemap.model.Sighting;
import com.pokemap.repo.SightingRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


/**
 * Negozio-logika: begiztatzeak sortu eta zerrendatzeko zerbitzua.
 * 
 * Begiztatze bat sortzerakoan, PokeAPI kontsultatzen du pokemonaren datuak egiaztatzeko eta osatzeko.
 */
@Service
public class SightingService {

    private final SightingRepository repo;
    private final PokeApiClient pokeApi;

    public SightingService(SightingRepository repo, PokeApiClient pokeApi) {
        this.repo = repo;
        this.pokeApi = pokeApi;
    }

/**
 * Begiztatze berri bat sortu eta datu-basean gordetzen du.
 * 
 * Prozesua:
 * <ol>
 * <li>Request-aren balidazioa</li>
 * <li>PokeAPI kontsulta (pokemonaren izena/id egiaztatzeko)</li>
 * <li>Entitatearen eraikuntza eta SQLite-ra txertaketa</li>
 * </ol>
 * @param req sortzeko request body-a
 * @return sortutako begiztatzea (ID-a barne)
 * @throws Exception PokeAPI edo datu-baseko arazoengatik
 */
    public Sighting create(CreateSightingRequest req) throws Exception {
        if (req == null) throw new IllegalArgumentException("body vacío");
        if (req.getPokemon() == null || req.getPokemon().trim().isEmpty()) {
            throw new IllegalArgumentException("pokemon requerido");
        }

        PokemonInfo info = pokeApi.fetchPokemon(req.getPokemon());

        Sighting s = new Sighting();
        s.setPokemonName(info.getName());
        s.setPokemonId(info.getId());
        s.setSpriteUrl(info.getSpriteUrl());
        s.setLat(req.getLat());
        s.setLon(req.getLon());
        s.setTimestamp(Instant.now().toEpochMilli());
        s.setNote(req.getNote());

        return repo.insert(s);
    }

/**
 * Begiztatze guztiak zerrendatzen ditu.
 * @return begiztatzeen zerrenda
 */
    public List<Sighting> listAll() { return repo.findAll(); }
    public List<Sighting> listByPokemon(String pokemon) { return repo.findByPokemon(pokemon); }
}
