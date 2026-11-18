package persistencia;
import persistencia.LectorJson;
import persistencia.JsonValor;
import persistencia.AdaptadorJson;
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
                    "La carpeta de datos es obligatoria.");
        }

        this.carpetaDatos = carpetaDatos;
    }

    // Carga el archivo de configuracion como texto JSON
    public String cargarConfiguracion() throws IOException {
        return leer("config.json");
    }

    // Guarda la configuracion como texto JSON
    public void guardarConfiguracion(String json)
            throws IOException {

        escribir("config.json", json);
    }

    // Carga el archivo de pacientes como texto JSON
    public String cargarPacientes() throws IOException {
        return leer("pacientes.json");
    }

    // Guarda los pacientes como texto JSON
    public void guardarPacientes(String json) throws IOException {
        escribir("pacientes.json", json);
    }

    // Elimina el archivo de pacientes durante el reinicio del sistema
    public void eliminarPacientes() throws IOException {
        Files.deleteIfExists(carpetaDatos.resolve("pacientes.json"));
    }

    // Carga el archivo de tiquetes como texto JSON
    public String cargarTiquetes() throws IOException {
        return leer("tiquetes.json");
    }

    // Guarda los tiquetes como texto JSON
    public void guardarTiquetes(String json)
            throws IOException {

        escribir("tiquetes.json", json);
    }

    // Carga los datos de configuracion mediante un adaptador
    public <T> T cargarConfiguracion(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarConfiguracion(),
                adaptador,
                "config.json");
    }

    // Guarda los datos de configuracion mediante un adaptador
    public <T> void guardarConfiguracion(
            T configuracion,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarConfiguracion(
                convertir(configuracion, adaptador));
    }

    // Carga los pacientes mediante un adaptador
    public <T> T cargarPacientes(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarPacientes(),
                adaptador,
                "pacientes.json");
    }

    // Guarda los pacientes mediante un adaptador
    public <T> void guardarPacientes(
            T pacientes,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarPacientes(convertir(pacientes, adaptador));
    }

    // Carga los tiquetes mediante un adaptador
    public <T> T cargarTiquetes(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarTiquetes(),
                adaptador,
                "tiquetes.json");
    }

    // Guarda los tiquetes mediante un adaptador
    public <T> void guardarTiquetes(
            T pendientes,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarTiquetes(
                convertir(pendientes, adaptador));
    }

    // Lee un archivo existente y valida su contenido
    private String leer(String nombreArchivo)
            throws IOException {

        Path archivo = carpetaDatos.resolve(nombreArchivo);

        if (!Files.exists(archivo)) {
            return null;
        }

        if (!Files.isRegularFile(archivo)) {
            throw new IOException(
                    "La ruta no corresponde a un archivo: "
                    + nombreArchivo);
        }

        String contenido = new String(
                Files.readAllBytes(archivo),
                StandardCharsets.UTF_8);

        validarJson(contenido, nombreArchivo);

        return contenido;
    }

    // Escribe el archivo usando un archivo temporal
    private void escribir(
            String nombreArchivo,
            String contenido) throws IOException {

        validarJson(contenido, nombreArchivo);

        Path archivo = carpetaDatos.resolve(nombreArchivo)
                .toAbsolutePath();

        Path carpeta = archivo.getParent();

        if (carpeta == null) {
            throw new IOException(
                    "No se pudo determinar la carpeta de destino.");
        }

        Files.createDirectories(carpeta);

        Path temporal = Files.createTempFile(
                carpeta,
                "mediqueue-",
                ".tmp");

        try {

            Files.write(
                    temporal,
                    contenido.getBytes(StandardCharsets.UTF_8));

            try {

                Files.move(
                        temporal,
                        archivo,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException ex) {

                Files.move(
                        temporal,
                        archivo,
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } finally {

            Files.deleteIfExists(temporal);
        }
    }

    // Valida que el contenido sea un objeto JSON correcto
    private static void validarJson(
            String json,
            String nombreArchivo) throws IOException {

        if (json == null || json.trim().isEmpty()) {
            throw new IOException(
                    "El contenido de "
                    + nombreArchivo
                    + " esta vacio.");
        }

        try {

            JsonValor raiz = new LectorJson(json).leer();

            raiz.exigirTipo(JsonValor.OBJETO);

        } catch (IllegalArgumentException ex) {

            throw new IOException(
                    nombreArchivo
                    + " contiene JSON invalido: "
                    + ex.getMessage(),
                    ex);
        }
    }

    // Convierte los datos a JSON mediante el adaptador
    private static <T> String convertir(
            T datos,
            AdaptadorJson<T> adaptador) throws IOException {

        if (adaptador == null) {
            throw new IllegalArgumentException(
                    "Falta el adaptador JSON.");
        }

        try {

            return adaptador.convertirAJson(datos);

        } catch (RuntimeException ex) {

            throw new IOException(
                    "No se pudieron serializar los datos: "
                    + ex.getMessage(),
                    ex);
        }
    }

    // Reconstruye los datos desde JSON mediante el adaptador
    private static <T> T reconstruir(
            String json,
            AdaptadorJson<T> adaptador,
            String nombreArchivo) throws IOException {

        if (json == null) {
            return null;
        }

        if (adaptador == null) {
            throw new IllegalArgumentException(
                    "Falta el adaptador JSON.");
        }

        try {

            return adaptador.convertirDesdeJson(json);

        } catch (RuntimeException ex) {

            throw new IOException(
                    "No se pudo recuperar "
                    + nombreArchivo
                    + ": "
                    + ex.getMessage(),
                    ex);
        }
    }
}