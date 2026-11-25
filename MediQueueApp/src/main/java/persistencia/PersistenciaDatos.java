
package persistencia;

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

    // ================= CONFIGURACION =================

    // Carga la configuracion como texto JSON.
    public String cargarConfiguracion() throws IOException {
        return leer("config.json");
    }

    // Guarda la configuracion como texto JSON.
    public void guardarConfiguracion(String json) throws IOException {
        escribir("config.json", json);
    }

    // Carga la configuracion mediante un adaptador.
    public <T> T cargarConfiguracion(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarConfiguracion(),
                adaptador,
                "config.json");
    }

    // Guarda la configuracion mediante un adaptador.
    public <T> void guardarConfiguracion(
            T configuracion,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarConfiguracion(convertir(configuracion, adaptador));
    }

    // ================= PACIENTES =================

    // Carga los pacientes como texto JSON.
    public String cargarPacientes() throws IOException {
        return leer("pacientes.json");
    }

    // Guarda los pacientes como texto JSON.
    public void guardarPacientes(String json) throws IOException {
        escribir("pacientes.json", json);
    }

    // Elimina el archivo de pacientes.
    public void eliminarPacientes() throws IOException {
        Files.deleteIfExists(carpetaDatos.resolve("pacientes.json"));
    }

    // Carga los pacientes mediante un adaptador.
    public <T> T cargarPacientes(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarPacientes(),
                adaptador,
                "pacientes.json");
    }

    // Guarda los pacientes mediante un adaptador.
    public <T> void guardarPacientes(
            T pacientes,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarPacientes(convertir(pacientes, adaptador));
    }

    // ================= TIQUETES =================

    // Carga los tiquetes como texto JSON.
    public String cargarTiquetes() throws IOException {
        return leer("tiquetes.json");
    }

    // Guarda los tiquetes como texto JSON.
    public void guardarTiquetes(String json) throws IOException {
        escribir("tiquetes.json", json);
    }

    // Carga los tiquetes mediante un adaptador.
    public <T> T cargarTiquetes(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarTiquetes(),
                adaptador,
                "tiquetes.json");
    }

    // Guarda los tiquetes mediante un adaptador.
    public <T> void guardarTiquetes(
            T pendientes,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarTiquetes(convertir(pendientes, adaptador));
    }

    // ================= ATENCIONES =================

    // Carga las atenciones como texto JSON.
    public String cargarAtenciones() throws IOException {
        return leer("atendidos.json");
    }

    // Guarda las atenciones como texto JSON.
    public void guardarAtenciones(String json) throws IOException {
        escribir("atendidos.json", json);
    }

    // Carga las atenciones mediante un adaptador.
    public <T> T cargarAtenciones(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarAtenciones(),
                adaptador,
                "atendidos.json");
    }

    // Guarda las atenciones mediante un adaptador.
    public <T> void guardarAtenciones(
            T atenciones,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarAtenciones(convertir(atenciones, adaptador));
    }

    // ================= HISTORIAL =================

    // Carga el historial como texto JSON.
    public String cargarHistorial() throws IOException {
        return leer("historial.json");
    }

    // Guarda el historial como texto JSON.
    public void guardarHistorial(String json) throws IOException {
        escribir("historial.json", json);
    }

    // Carga el historial mediante un adaptador.
    public <T> T cargarHistorial(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarHistorial(),
                adaptador,
                "historial.json");
    }

    // Guarda el historial mediante un adaptador.
    public <T> void guardarHistorial(
            T historial,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarHistorial(convertir(historial, adaptador));
    }

    // ================= SALAS =================

    // Carga las salas como texto JSON.
    public String cargarSalas() throws IOException {
        return leer("salas.json");
    }

    // Guarda las salas como texto JSON.
    public void guardarSalas(String json) throws IOException {
        escribir("salas.json", json);
    }

    // Carga las salas mediante un adaptador.
    public <T> T cargarSalas(
            AdaptadorJson<T> adaptador) throws IOException {

        return reconstruir(
                cargarSalas(),
                adaptador,
                "salas.json");
    }

    // Guarda las salas mediante un adaptador.
    public <T> void guardarSalas(
            T salas,
            AdaptadorJson<T> adaptador) throws IOException {

        guardarSalas(convertir(salas, adaptador));
    }
// ================= GRAFO DE SEDES =================

public String cargarGrafo() throws IOException {
    return leer("grafo.json");
}

public void guardarGrafo(String json) throws IOException {
    escribir("grafo.json", json);
}

public <T> T cargarGrafo(
        AdaptadorJson<T> adaptador) throws IOException {

    return reconstruir(
            cargarGrafo(),
            adaptador,
            "grafo.json");
}

public <T> void guardarGrafo(
        T grafo,
        AdaptadorJson<T> adaptador) throws IOException {

    guardarGrafo(convertir(grafo, adaptador));
}
    // ================= LECTURA Y ESCRITURA =================

    // Lee un archivo existente y valida su contenido.
    private String leer(String nombreArchivo) throws IOException {

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

    // Escribe el archivo utilizando un archivo temporal.
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

    // ================= VALIDACION JSON =================

    // Comprueba que el contenido sea un objeto JSON válido.
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

    // ================= CONVERSION JSON =================

    // Convierte los datos a JSON mediante un adaptador.
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

    // Reconstruye los datos desde JSON mediante un adaptador.
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