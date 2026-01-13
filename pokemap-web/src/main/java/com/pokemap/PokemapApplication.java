package com.pokemap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Spring Boot aplikazioaren abiapuntua.
 * 
 * Klase honek Spring Boot-en auto-konfigurazioa aktibatzen du eta aplikazioa exekutatzen du.
 */
@SpringBootApplication
public class PokemapApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokemapApplication.class, args);
    }
}
