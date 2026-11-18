/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package configuracion;
import configuracion.Configuracion;
import modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import persistencia.AdaptadorJson;
import persistencia.PersistenciaDatos;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author casti
 */
public class ArchivoConfiguracion {

    private final Gson gson;
    private final PersistenciaDatos persistencia;
    private final AdaptadorJson<DatosGuardados> adaptadorDatos;

    private DatosGuardados datos;

    // Nodo para guardar los usuarios mediante una estructura enlazada
    private static class NodoUsuario {

        private Usuario usuario;
        private NodoUsuario siguiente;

        public NodoUsuario(Usuario usuario) {
            this.usuario = usuario;
            this.siguiente = null;
        }
    }

    // Reune la configuracion y los usuarios del sistema
    private static class DatosGuardados {

        private Configuracion configuracion;
        private NodoUsuario usuarios;

        public DatosGuardados(Configuracion configuracion) {
            this.configuracion = configuracion;
            this.usuarios = null;
        }
    }

    // Prepara la persistencia y el convertidor JSON
    public ArchivoConfiguracion() {

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        persistencia = new PersistenciaDatos(Paths.get("."));

        adaptadorDatos = new AdaptadorJson<DatosGuardados>() {

            @Override
            public String convertirAJson(DatosGuardados datos) {
                return gson.toJson(datos);
            }

            @Override
            public DatosGuardados convertirDesdeJson(String json) {

                JsonElement elemento = new JsonParser().parse(json);

                if (!elemento.isJsonObject()) {
                    throw new IllegalArgumentException(
                            "El JSON no tiene un formato valido.");
                }

                JsonObject objeto = elemento.getAsJsonObject();

                if (objeto.has("configuracion")
                        || objeto.has("usuarios")) {

                    return gson.fromJson(
                            objeto,
                            DatosGuardados.class);

                } else {

                    // Admite el formato anterior de config.json
                    Configuracion configuracion =
                            gson.fromJson(
                                    objeto,
                                    Configuracion.class);

                    return new DatosGuardados(configuracion);
                }
            }
        };

        datos = null;
    }

    // Comprueba si existe el archivo de configuracion
    public boolean existeArchivo() {
        return Files.exists(Paths.get("config.json"));
    }

    // Guarda la configuracion y los usuarios
    private boolean guardarDatos() {

        if (datos == null) {
            return false;
        }

        try {

            persistencia.guardarConfiguracion(
                    datos,
                    adaptadorDatos);

            System.out.println("Datos guardados en config.json.");

            return true;

        } catch (IOException | RuntimeException e) {

            System.out.println(
                    "Error al guardar: " + e.getMessage());

            return false;
        }
    }

    // Guarda una configuracion nueva o actualizada
    public boolean guardarConfiguracion(Configuracion configuracion) {

        if (configuracion == null) {
            return false;
        }

        cargarDatosInternos();

        if (datos == null) {
            datos = new DatosGuardados(configuracion);
        } else {
            datos.configuracion = configuracion;
        }

        return guardarDatos();
    }

    // Recupera la configuracion de la sede
    public Configuracion cargarConfiguracion() {

        cargarDatosInternos();

        if (datos == null) {
            return null;
        }

        return datos.configuracion;
    }

    // Carga los datos desde PersistenciaDatos
    private void cargarDatosInternos() {

        if (datos != null) {
            return;
        }

        try {

            datos = persistencia.cargarConfiguracion(
                    adaptadorDatos);

            if (datos != null && datos.usuarios == null) {

                datos.usuarios = new NodoUsuario(
                        new Usuario(
                                "admin",
                                "prueba123",
                                "Administrador"));

                guardarDatos();
            }

        } catch (IOException | RuntimeException e) {

            datos = null;

            System.out.println(
                    "Error al cargar config.json: "
                    + e.getMessage());
        }
    }

    // Comprueba los datos de inicio de sesion
    public Usuario validarUsuario(
            String nombreUsuario, String contrasena) {

        if (nombreUsuario == null
                || contrasena == null
                || nombreUsuario.trim().isEmpty()
                || contrasena.isEmpty()) {

            return null;
        }

        cargarDatosInternos();

        NodoUsuario actual = obtenerPrimerUsuario();

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getContrasena() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(nombreUsuario.trim())
                    && actual.usuario.getContrasena()
                            .equals(contrasena)) {

                return actual.usuario;
            }

            actual = actual.siguiente;
        }

        return null;
    }

    // Devuelve el primer nodo de usuarios
    private NodoUsuario obtenerPrimerUsuario() {

        if (datos == null) {
            return null;
        }

        return datos.usuarios;
    }

    // Comprueba si ya existe un nombre de usuario
    public boolean existeUsuario(String nombreUsuario) {

        if (nombreUsuario == null
                || nombreUsuario.trim().isEmpty()) {
            return false;
        }

        cargarDatosInternos();

        NodoUsuario actual = obtenerPrimerUsuario();

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(nombreUsuario.trim())) {

                return true;
            }

            actual = actual.siguiente;
        }

        return false;
    }

    // Registra un usuario nuevo
    public boolean agregarUsuario(
            String nombreUsuario, String contrasena, String rol) {

        cargarDatosInternos();

        if (datos == null
                || nombreUsuario == null
                || contrasena == null
                || rol == null
                || nombreUsuario.trim().isEmpty()
                || contrasena.isEmpty()
                || !rolValido(rol)
                || existeUsuario(nombreUsuario.trim())) {

            System.out.println(
                    "No se pudo registrar el usuario. Revise los datos.");

            return false;
        }

        Usuario nuevoUsuario = new Usuario(
                nombreUsuario.trim(),
                contrasena,
                rol.trim());

        NodoUsuario nuevo = new NodoUsuario(nuevoUsuario);

        if (datos.usuarios == null) {

            datos.usuarios = nuevo;

        } else {

            NodoUsuario actual = datos.usuarios;

            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }

            actual.siguiente = nuevo;
        }

        if (guardarDatos()) {
            return true;
        }

        eliminarNodo(nombreUsuario.trim());

        return false;
    }

    // Devuelve la lista de usuarios y sus roles
    public String consultarUsuarios() {

        cargarDatosInternos();

        if (datos == null || datos.usuarios == null) {
            return "No hay usuarios registrados.";
        }

        StringBuilder resultado =
                new StringBuilder("USUARIOS REGISTRADOS\n\n");

        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario != null) {
                resultado.append("Usuario: ")
                        .append(actual.usuario.getNombreUsuario())
                        .append("\nRol: ")
                        .append(actual.usuario.getRol())
                        .append("\n\n");
            }

            actual = actual.siguiente;
        }

        return resultado.toString();
    }

    // Modifica los datos de un usuario existente
    public boolean editarUsuario(
            String usuarioAnterior,
            String nombreNuevo,
            String contrasenaNueva,
            String rolNuevo) {

        cargarDatosInternos();

        if (datos == null
                || usuarioAnterior == null
                || nombreNuevo == null
                || contrasenaNueva == null
                || rolNuevo == null
                || nombreNuevo.trim().isEmpty()
                || contrasenaNueva.isEmpty()
                || !rolValido(rolNuevo)) {

            return false;
        }

        NodoUsuario actual = datos.usuarios;
        Usuario encontrado = null;

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(usuarioAnterior.trim())) {

                encontrado = actual.usuario;
                break;
            }

            actual = actual.siguiente;
        }

        if (encontrado == null) {
            System.out.println("El usuario indicado no existe.");
            return false;
        }

        if (!encontrado.getNombreUsuario()
                .equalsIgnoreCase(nombreNuevo.trim())
                && existeUsuario(nombreNuevo.trim())) {

            System.out.println("Ese nombre de usuario ya existe.");
            return false;
        }

        if (encontrado.getRol() != null
                && encontrado.getRol().equalsIgnoreCase("Administrador")
                && !rolNuevo.trim().equalsIgnoreCase("Administrador")
                && contarAdministradores() <= 1) {

            System.out.println(
                    "No se puede quitar el ultimo administrador.");

            return false;
        }

        String nombreOriginal = encontrado.getNombreUsuario();
        String contrasenaOriginal = encontrado.getContrasena();
        String rolOriginal = encontrado.getRol();

        encontrado.setNombreUsuario(nombreNuevo.trim());
        encontrado.setContrasena(contrasenaNueva);
        encontrado.setRol(rolNuevo.trim());

        if (guardarDatos()) {
            return true;
        }

        encontrado.setNombreUsuario(nombreOriginal);
        encontrado.setContrasena(contrasenaOriginal);
        encontrado.setRol(rolOriginal);

        return false;
    }

    // Elimina un usuario sin permitir que se borre a si mismo
    public boolean eliminarUsuario(
            String nombreUsuario, String usuarioActual) {

        cargarDatosInternos();

        if (datos == null
                || nombreUsuario == null
                || usuarioActual == null) {

            return false;
        }

        if (nombreUsuario.trim().equalsIgnoreCase(usuarioActual.trim())) {

            System.out.println(
                    "No puede eliminar su propia cuenta.");

            return false;
        }

        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(nombreUsuario.trim())) {

                if (actual.usuario.getRol() != null
                        && actual.usuario.getRol()
                                .equalsIgnoreCase("Administrador")
                        && contarAdministradores() <= 1) {

                    System.out.println(
                            "No se puede eliminar el ultimo administrador.");

                    return false;
                }

                break;
            }

            actual = actual.siguiente;
        }

        if (actual == null) {
            System.out.println("El usuario no existe.");
            return false;
        }

        NodoUsuario anterior = null;
        actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(nombreUsuario.trim())) {

                if (anterior == null) {
                    datos.usuarios = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }

                if (guardarDatos()) {
                    return true;
                }

                // Restaura el nodo si no se pudo guardar
                if (anterior == null) {
                    actual.siguiente = datos.usuarios;
                    datos.usuarios = actual;
                } else {
                    actual.siguiente = anterior.siguiente;
                    anterior.siguiente = actual;
                }

                return false;
            }

            anterior = actual;
            actual = actual.siguiente;
        }

        return false;
    }

    // Elimina un nodo por nombre de usuario
    private void eliminarNodo(String nombreUsuario) {

        if (datos == null || nombreUsuario == null) {
            return;
        }

        NodoUsuario anterior = null;
        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getNombreUsuario() != null
                    && actual.usuario.getNombreUsuario()
                            .equalsIgnoreCase(nombreUsuario)) {

                if (anterior == null) {
                    datos.usuarios = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }

                return;
            }

            anterior = actual;
            actual = actual.siguiente;
        }
    }

    // Cuenta cuantos administradores existen
    private int contarAdministradores() {

        int cantidad = 0;
        NodoUsuario actual = obtenerPrimerUsuario();

        while (actual != null) {

            if (actual.usuario != null
                    && actual.usuario.getRol() != null
                    && actual.usuario.getRol()
                            .equalsIgnoreCase("Administrador")) {

                cantidad++;
            }

            actual = actual.siguiente;
        }

        return cantidad;
    }

    // Comprueba que el rol sea uno de los permitidos
    private boolean rolValido(String rol) {

        if (rol == null) {
            return false;
        }

        return rol.equalsIgnoreCase("Administrador")
                || rol.equalsIgnoreCase("Recepcionista")
                || rol.equalsIgnoreCase("Medico")
                || rol.equalsIgnoreCase("Enfermero");
    }

    // Elimina la configuracion de la sede sin borrar los usuarios
    public boolean eliminarConfiguracion() {

        cargarDatosInternos();

        if (datos == null || datos.configuracion == null) {
            return false;
        }

        Configuracion configuracionAnterior = datos.configuracion;

        datos.configuracion = null;

        if (guardarDatos()) {
            return true;
        }

        datos.configuracion = configuracionAnterior;

        return false;
    }

    // Reinicia el sistema y conserva la cuenta administradora inicial
    public boolean reiniciarSistema() {

        cargarDatosInternos();

        DatosGuardados datosAnteriores = datos;

        datos = new DatosGuardados(null);

        datos.usuarios = new NodoUsuario(
                new Usuario(
                        "admin",
                        "prueba123",
                        "Administrador"));

        if (!guardarDatos()) {
            datos = datosAnteriores;
            return false;
        }

        try {

            Files.deleteIfExists(Paths.get("tiquetes.json"));

            System.out.println("Sistema reiniciado correctamente.");

            return true;

        } catch (IOException e) {

            System.out.println(
                    "No se pudieron eliminar los tiquetes: "
                    + e.getMessage());

            return false;
        }
    }
}