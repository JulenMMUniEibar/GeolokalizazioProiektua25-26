package com.pokemap.model;

/**
 * PokeAPI-tik lortutako Pokémonaren informazio minimoa.
 *
 * <p>Aplikazioak begiztatzeak sortzeko behar dituen datuak soilik gordetzen ditu:
 * ID-a, izena eta sprite URL-a.</p>
 */
public class PokemonInfo {

    private final int id;
    private final String name;
    private final String spriteUrl;

    /**
     * {@link PokemonInfo} objektu berri bat sortzen du.
     *
     * @param id       PokeAPI-ko ID numerikoa
     * @param name     pokemonaren izena (normalean lowercase)
     * @param spriteUrl irudiaren URL-a (aukerakoa, {@code null} izan daiteke)
     */
    public PokemonInfo(int id, String name, String spriteUrl) {
        this.id = id;
        this.name = name;
        this.spriteUrl = spriteUrl;
    }

    /**
     * @return pokemonaren ID-a
     */
    public int getId() {
        return id;
    }

    /**
     * @return pokemonaren izena
     */
    public String getName() {
        return name;
    }

    /**
     * @return sprite/irudiaren URL-a (aukerakoa, {@code null} izan daiteke)
     */
    public String getSpriteUrl() {
        return spriteUrl;
    }
}
