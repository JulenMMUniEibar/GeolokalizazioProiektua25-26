package com.pokemap.controller;

import com.pokemap.dto.CreateSightingRequest;
import com.pokemap.model.Sighting;
import com.pokemap.service.SightingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST kontrolatzailea: begiztatzeen (sightings) API-a.
 * 
 * Endpoint nagusiak eskaintzen ditu:
 * <ul>
 * <li><code>POST /api/sightings</code>: begiztatze berri bat sortu</li>
 * <li><code>GET /api/sightings</code>: begiztatzeak zerrendatu (aukeran, pokemon iragazkiarekin)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/sightings")
public class SightingController {

    private final SightingService service;

    public SightingController(SightingService service) {
        this.service = service;
    }

/**
 * Begiztatze berri bat sortzen du.
 * 
 * @param req sortzeko eskaera (pokemon izena, koordenatuak eta oharra)
 * @return sortutako begiztatzea edo errore-mezua (400)
 */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateSightingRequest req) {
        try {
            return ResponseEntity.ok(service.create(req));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

/**
 * Begiztatze guztiak zerrendatzen ditu, edo Pokémon izenaren arabera filtratuta.
 * 
 * @param pokemon (aukerakoa) pokemon izena; hutsik bada, guztiak itzultzen dira
 * @return begiztatzeen zerrenda (timestamp DESC ordenatuta, repositoryaren arabera)
 */
    @GetMapping
    public List<Sighting> list(@RequestParam(required = false) String pokemon) {
        if (pokemon == null || pokemon.trim().isEmpty()) return service.listAll();
        return service.listByPokemon(pokemon.trim().toLowerCase());
    }
}
