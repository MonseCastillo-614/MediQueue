package cr.ac.ufidelitas.mediqueueapp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final Path rutaArchivo;
    private final Gson gson;
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

    // Prepara el archivo y el convertidor JSON
    public ArchivoConfiguracion() {
        rutaArchivo = Paths.get("config.json");
        gson = new GsonBuilder().setPrettyPrinting().create();
        datos = null;
    }

    // Comprueba si existe el archivo
    public boolean existeArchivo() {
        return Files.exists(rutaArchivo);
    }

    // Guarda la configuracion y los usuarios
    private boolean guardarDatos() {

        if (datos == null) {
            return false;
        }

        try (FileWriter escritor =
                new FileWriter(rutaArchivo.toFile())) {

            gson.toJson(datos, escritor);
            System.out.println("Datos guardados en config.json.");
            return true;

        } catch (IOException e) {

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

    // Carga los datos y admite el formato anterior de config.json
    private void cargarDatosInternos() {

        if (datos != null) {
            return;
        }

        if (!existeArchivo()) {
            return;
        }

        try (Reader lector =
                new FileReader(rutaArchivo.toFile())) {

            JsonElement elemento = JsonParser.parseReader(lector);

            if (!elemento.isJsonObject()) {
                System.out.println("El JSON no tiene un formato valido.");
                return;
            }

            JsonObject objeto = elemento.getAsJsonObject();

            if (objeto.has("configuracion")) {

                // Carga el formato que contiene configuracion y usuarios
                datos = gson.fromJson(objeto, DatosGuardados.class);

            } else {

                // Recupera la configuracion guardada con el formato anterior
                Configuracion configuracion =
                        gson.fromJson(objeto, Configuracion.class);

                datos = new DatosGuardados(configuracion);
            }

            if (datos != null && datos.usuarios == null) {

                // Crea una cuenta inicial si aun no hay usuarios
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
                    "Error al cargar config.json: " + e.getMessage());
        }
    }

    // Comprueba los datos de inicio de sesion
    public Usuario validarUsuario(
            String nombreUsuario, String contrasena) {

        cargarDatosInternos();

        NodoUsuario actual = obtenerPrimerUsuario();

        while (actual != null) {

            if (actual.usuario.getNombreUsuario()
                    .equalsIgnoreCase(nombreUsuario)
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

        cargarDatosInternos();

        NodoUsuario actual = obtenerPrimerUsuario();

        while (actual != null) {

            if (actual.usuario.getNombreUsuario()
                    .equalsIgnoreCase(nombreUsuario)) {
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
                nombreUsuario.trim(), contrasena, rol);

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

        // Revierte el registro si no se pudo guardar
        eliminarNodo(nombreUsuario.trim());
        return false;
    }

    // Devuelve la lista de usuarios y sus roles
    public String consultarUsuarios() {

        cargarDatosInternos();

        if (datos == null || datos.usuarios == null) {
            return "No hay usuarios registrados.";
        }

        String resultado = "USUARIOS REGISTRADOS\n\n";
        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            resultado += "Usuario: "
                    + actual.usuario.getNombreUsuario()
                    + "\nRol: "
                    + actual.usuario.getRol()
                    + "\n\n";

            actual = actual.siguiente;
        }

        return resultado;
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

            if (actual.usuario.getNombreUsuario()
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

        if (encontrado.getRol().equalsIgnoreCase("Administrador")
                && !rolNuevo.equalsIgnoreCase("Administrador")
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
        encontrado.setRol(rolNuevo);

        if (guardarDatos()) {
            return true;
        }

        // Restaura los datos si falla el guardado
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

        if (nombreUsuario.trim().equalsIgnoreCase(usuarioActual)) {
            System.out.println(
                    "No puede eliminar su propia cuenta.");
            return false;
        }

        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario.getNombreUsuario()
                    .equalsIgnoreCase(nombreUsuario.trim())) {

                if (actual.usuario.getRol()
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

            if (actual.usuario.getNombreUsuario()
                    .equalsIgnoreCase(nombreUsuario.trim())) {

                if (anterior == null) {
                    datos.usuarios = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }

                if (guardarDatos()) {
                    return true;
                }

                // Si falla el guardado, vuelve a enlazar el nodo
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

        NodoUsuario anterior = null;
        NodoUsuario actual = datos.usuarios;

        while (actual != null) {

            if (actual.usuario.getNombreUsuario()
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

            if (actual.usuario.getRol()
                    .equalsIgnoreCase("Administrador")) {
                cantidad++;
            }

            actual = actual.siguiente;
        }

        return cantidad;
    }

    // Comprueba que el rol sea uno de los permitidos
    private boolean rolValido(String rol) {

        return rol.equalsIgnoreCase("Administrador")
                || rol.equalsIgnoreCase("Recepcionista")
                || rol.equalsIgnoreCase("Medico")
                || rol.equalsIgnoreCase("Enfermero");
    }
}