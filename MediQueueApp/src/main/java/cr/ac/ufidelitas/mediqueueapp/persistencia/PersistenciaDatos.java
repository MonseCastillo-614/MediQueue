
package cr.ac.ufidelitas.mediqueueapp.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public final class PersistenciaDatos {

    private final Path carpetaDatos;

    public PersistenciaDatos(Path carpetaDatos) {
        if (carpetaDatos == null) {
            throw new IllegalArgumentException(
                "La carpeta de datos es obligatoria."
            );
        }
        this.carpetaDatos = carpetaDatos;
    }

    
    public String cargarConfiguracion() throws IOException {
        return leer("config.json");
    }

  
    public void guardarConfiguracion(String json)
            throws IOException {
        escribir("config.json", json);
    }

   
    public String cargarTiquetes() throws IOException {
        return leer("tiquetes.json");
    }

   
    public void guardarTiquetes(String json)
            throws IOException {
        escribir("tiquetes.json", json);
    }

   
    public <T> T cargarConfiguracion(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
            cargarConfiguracion(),
            adaptador,
            "config.json"
        );
    }

    
    public <T> void guardarConfiguracion(
            T configuracion,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarConfiguracion(
            convertir(configuracion, adaptador)
        );
    }

    
    public <T> T cargarTiquetes(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
            cargarTiquetes(),
            adaptador,
            "tiquetes.json"
        );
    }

   
    public <T> void guardarTiquetes(
            T pendientes,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarTiquetes(
            convertir(pendientes, adaptador)
        );
    }

    
    private String leer(String nombreArchivo)
            throws IOException {

        Path archivo = carpetaDatos.resolve(nombreArchivo);

        if (!Files.exists(archivo)) {
            return null;
        }

       
    String contenido = new String(
    Files.readAllBytes(archivo),
    StandardCharsets.UTF_8
    );


        validarJson(contenido, nombreArchivo);

        return contenido;
    }

  
    private void escribir(String nombreArchivo,
                          String contenido) throws IOException {

        validarJson(contenido, nombreArchivo);

        Path archivo = carpetaDatos.resolve(nombreArchivo)
                .toAbsolutePath();

        Files.createDirectories(archivo.getParent());

        Path temporal = Files.createTempFile(
            archivo.getParent(),
            "mediqueue-",
            ".tmp"
        );

        try {
        Files.write(
         temporal,
          contenido.getBytes(StandardCharsets.UTF_8)
        );


            try {
                Files.move(
                    temporal,
                    archivo,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(
                    temporal,
                    archivo,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    
    private static void validarJson(
            String json,
            String nombreArchivo) throws IOException {

        
    if (json == null || json.trim().isEmpty()) {
            throw new IOException(
                "El contenido de " + nombreArchivo
                + " esta vacio."
            );
        }

        try {
            JsonValor raiz = new LectorJson(json).leer();
            raiz.exigirTipo(JsonValor.OBJETO);

        } catch (IllegalArgumentException ex) {
            throw new IOException(
                nombreArchivo + " contiene JSON invalido: "
                + ex.getMessage(), ex
            );
        }
    }

    
    private static <T> String convertir(
            T datos,
            AdaptadorJson<T> adaptador) throws IOException {

        if (adaptador == null) {
            throw new IllegalArgumentException(
                "Falta el adaptador JSON."
            );
        }

        try {
            return adaptador.convertirAJson(datos);

        } catch (RuntimeException ex) {
            throw new IOException(
                "No se pudieron serializar los datos: "
                + ex.getMessage(), ex
            );
        }
    }

   
    private static <T> T reconstruir(
            String json,
            AdaptadorJson<T> adaptador,
            String nombreArchivo) throws IOException {

        if (json == null) {
            return null;
        }

        if (adaptador == null) {
            throw new IllegalArgumentException(
                "Falta el adaptador JSON."
            );
        }

        try {
            return adaptador.convertirDesdeJson(json);

        } catch (RuntimeException ex) {
            throw new IOException(
                "No se pudo recuperar " + nombreArchivo
                + ": " + ex.getMessage(), ex
            );
        }
    }
}
