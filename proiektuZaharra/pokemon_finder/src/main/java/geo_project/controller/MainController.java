package geo_project.controller;

import geo_project.api.PokeApiClient;
import geo_project.db.SightingDao;
import geo_project.model.Sighting;
import javafx.fxml.FXML;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.time.Instant;
import java.util.List;

public class MainController {

    @FXML
    private WebView webView;

    @FXML
    private TextField pokemonField;
    @FXML
    private TextArea noteArea;
    @FXML
    private TextField latField;
    @FXML
    private TextField lonField;

    @FXML
    private TextField filterField;
    @FXML
    private ListView<Sighting> sightingsList;

    private final SightingDao dao = new SightingDao();
    private final PokeApiClient pokeApi = new PokeApiClient();

    private WebEngine engine;

    // Última coordenada elegida en el mapa
    private Double selectedLat = null;
    private Double selectedLon = null;

    @FXML
    public void initialize() {
        engine = webView.getEngine();

        URL mapUrl = getClass().getResource("/web/map.html");
        if (mapUrl == null) {
            showError("No se encontró /web/map.html en resources");
            return;
        }

        engine.load(mapUrl.toExternalForm());

        engine.getLoadWorker().stateProperty().addListener((obs, old, st) -> {
            if (st == javafx.concurrent.Worker.State.SUCCEEDED) {

                // 1) Inyectar el objeto Java
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("app", new JsBridge());

                // 2) Enganchar click y arreglar tamaño
                engine.executeScript("if (window.bindJavaBridge) window.bindJavaBridge();");
                engine.executeScript("if (window.fixLeaflet) window.fixLeaflet();");

                // 3) IMPORTANTÍSIMO: cuando la ventana ya esté visible, vuelve a recalcular
                Platform.runLater(() -> {
                    engine.executeScript("setTimeout(() => { if(window.fixLeaflet) window.fixLeaflet(); }, 600);");
                });

                // Cargar DB y pintar markers
                reloadAndRenderAll();

                // 4) Cada vez que cambie el tamaño de la ventana, recalcula Leaflet
                Platform.runLater(() -> {
                    if (webView.getScene() != null && webView.getScene().getWindow() != null) {
                        webView.getScene().getWindow().widthProperty().addListener(
                                (a, b, c) -> engine.executeScript("if(window.fixLeaflet) window.fixLeaflet();"));
                        webView.getScene().getWindow().heightProperty().addListener(
                                (a, b, c) -> engine.executeScript("if(window.fixLeaflet) window.fixLeaflet();"));
                    }
                });
            }
        });

        sightingsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Sighting item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        sightingsList.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null)
                centerMap(s.getLat(), s.getLon(), 16);
        });
    }

    @FXML
    public void onSave() {
        String key = pokemonField.getText();
        if (key == null || key.isBlank()) {
            showError("Introduce un Pokémon (nombre o id).");
            return;
        }
        if (selectedLat == null || selectedLon == null) {
            showError("Selecciona una ubicación haciendo click en el mapa.");
            return;
        }

        // Llamada a PokeAPI en hilo aparte para no congelar UI
        new Thread(() -> {
            try {
                PokeApiClient.PokemonInfo info = pokeApi.fetchPokemon(key);

                long now = Instant.now().toEpochMilli();
                String note = noteArea.getText();

                Sighting s = new Sighting(
                        info.name(),
                        info.id(),
                        info.spriteUrl(),
                        selectedLat,
                        selectedLon,
                        now,
                        note);

                long id = dao.insert(s);
                s.setId(id);

                Platform.runLater(() -> {
                    // actualizar UI
                    addMarkerToMap(s);
                    sightingsList.getItems().add(0, s);

                    // limpiar campos (menos lat/lon para añadir varios en zona)
                    pokemonField.clear();
                    noteArea.clear();
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error guardando: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onApplyFilter() {
        String f = filterField.getText() == null ? "" : filterField.getText().trim().toLowerCase();
        if (f.isBlank()) {
            onShowAll();
            return;
        }
        new Thread(() -> {
            try {
                List<Sighting> list = dao.listByPokemon(f);
                Platform.runLater(() -> renderListOnMap(list));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error filtrando: " + ex.getMessage()));
            }
        }).start();
    }

    @FXML
    public void onShowAll() {
        reloadAndRenderAll();
    }

    private void reloadAndRenderAll() {
        new Thread(() -> {
            try {
                List<Sighting> list = dao.listAll();
                Platform.runLater(() -> renderListOnMap(list));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error cargando DB: " + ex.getMessage()));
            }
        }).start();
    }

    private void renderListOnMap(List<Sighting> list) {
        sightingsList.getItems().setAll(list);
        clearMarkers();
        for (Sighting s : list)
            addMarkerToMap(s);
        if (!list.isEmpty())
            centerMap(list.get(0).getLat(), list.get(0).getLon(), 13);
    }

    private void addMarkerToMap(Sighting s) {
        String name = escapeHtml(capitalize(s.getPokemonName()));
        String note = s.getNote() == null ? "" : escapeHtml(s.getNote());
        String when = escapeHtml(Instant.ofEpochMilli(s.getTimestamp()).toString());

        String sprite = s.getSpriteUrl();
        String imgHtml = (sprite == null || sprite.isBlank())
                ? ""
                : "<img src='" + escapeAttr(sprite) + "' alt='sprite'/>";

        String popup = """
                <div class="popup">
                  %s
                  <div>
                    <div class="t">%s (#%d)</div>
                    <div class="s">%s</div>
                    <div class="s">%s</div>
                  </div>
                </div>
                """.formatted(imgHtml, name, s.getPokemonId(), when, note);

        // llamar JS: addSightingMarker(lat, lon, popupHtml)
        String js = "addSightingMarker(" + s.getLat() + "," + s.getLon() + "," + toJsString(popup) + ");";
        engine.executeScript(js);
    }

    private void clearMarkers() {
        engine.executeScript("clearMarkers();");
    }

    private void centerMap(double lat, double lon, int zoom) {
        engine.executeScript("setCenter(" + lat + "," + lon + "," + zoom + ");");
    }

    // Objeto que JS llama: window.app.onMapClick(lat, lon)
    public class JsBridge {
        public void onMapClick(double lat, double lon) {
            selectedLat = lat;
            selectedLon = lon;
            Platform.runLater(() -> {
                latField.setText(String.format("%.6f", lat));
                lonField.setText(String.format("%.6f", lon));
            });
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static String toJsString(String s) {
        // convierte a string JS seguro (comillas + escapes básicos)
        String esc = s.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\"", "\\\"");
        return "\"" + esc + "\"";
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escapeAttr(String s) {
        return escapeHtml(s).replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank())
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}