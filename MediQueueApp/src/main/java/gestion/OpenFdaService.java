package gestion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class OpenFdaService {

    private static final String BASE_URL = "https://api.fda.gov/drug/label.json";
    private final HttpClient httpClient;

    public OpenFdaService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public Medicamento consultarMedicamento(String nombreMedicamento) {
        try {
            String query = "openfda.brand_name:\"" + nombreMedicamento + "\" OR openfda.generic_name:\"" + nombreMedicamento + "\"";
            String queryCodificado = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = BASE_URL + "?search=" + queryCodificado + "&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return null;
            }

            return convertirAMedicamento(response.body(), nombreMedicamento);

        } catch (IOException e) {
            System.err.println("Error de red consultando OpenFDA: " + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Consulta a OpenFDA interrumpida: " + e.getMessage());
            return null;
        }
    }

    private Medicamento convertirAMedicamento(String jsonTexto, String nombreOriginal) {
        try {
            JsonObject raiz = JsonParser.parseString(jsonTexto).getAsJsonObject();
            
            if (raiz.has("results")) {
                JsonArray resultados = raiz.getAsJsonArray("results");
                if (resultados != null && !resultados.isEmpty()) {
                    JsonObject item = resultados.get(0).getAsJsonObject();
                    
                    String usoRecomendado = obtenerCampoTexto(item, "indications_and_usage");
                    String efectosSecundarios = obtenerCampoTexto(item, "adverse_reactions");
                    String advertencias = obtenerCampoTexto(item, "warnings");
                    
                    return new Medicamento(nombreOriginal, usoRecomendado, efectosSecundarios, advertencias);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando el JSON de FDA: " + e.getMessage());
        }
        return null;
    }

    private String obtenerCampoTexto(JsonObject item, String campo) {
        if (item.has(campo)) {
            JsonElement elemento = item.get(campo);
            if (elemento.isJsonArray()) {
                JsonArray arreglo = elemento.getAsJsonArray();
                if (!arreglo.isEmpty()) {
                    return arreglo.get(0).getAsString();
                }
            } else {
                return elemento.getAsString();
            }
        }
        return "No especificado";
    }
}