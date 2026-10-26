/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package cr.ac.ufidelitas.mediqueueapp;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JPasswordField;
/**
 *
 * @author casti
 */
public class MediQueueApp {

    public static void main(String[] args) {

        ArchivoConfiguracion archivo = new ArchivoConfiguracion();

        // Recupera la configuracion guardada
        Configuracion configuracion = archivo.cargarConfiguracion();

        // Si no existe una configuracion, solicita una nueva
        if (configuracion == null) {

            configuracion = configurarSede();

            if (configuracion == null) {
                return;
            }

            if (!archivo.guardarConfiguracion(configuracion)) {
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible guardar la configuracion.");
                return;
            }
        }

        // Solicita el inicio de sesion
        Usuario usuarioIngresado = iniciarSesion(archivo);

        if (usuarioIngresado == null) {
            return;
        }

        JOptionPane.showMessageDialog(
                null,
                "Bienvenido, "
                + usuarioIngresado.getNombreUsuario()
                + "\nRol: " + usuarioIngresado.getRol());

        mostrarConfiguracion(configuracion);

        // Solo el administrador puede gestionar configuracion y usuarios
        if (usuarioIngresado.getRol()
                .equalsIgnoreCase("Administrador")) {

            mostrarMenuAdministrador(
                    archivo,
                    usuarioIngresado.getNombreUsuario());
        }
    }

    // Permite iniciar sesion
    public static Usuario iniciarSesion(
            ArchivoConfiguracion archivo) {

        String nombreUsuario = JOptionPane.showInputDialog(
                null, "Ingrese su nombre de usuario:");

        if (nombreUsuario == null) {
            return null;
        }

        JPasswordField campoContrasena = new JPasswordField();

        int resultado = JOptionPane.showConfirmDialog(
                null,
                campoContrasena,
                "Ingrese su contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return null;
        }

        String contrasena = new String(
                campoContrasena.getPassword());

        Usuario usuario = archivo.validarUsuario(
                nombreUsuario.trim(), contrasena);

        if (usuario == null) {
            JOptionPane.showMessageDialog(
                    null, "Usuario o contraseña incorrectos.");
        }

        return usuario;
    }

    // Permite configurar la sede hospitalaria
    public static Configuracion configurarSede() {

        String nombreSede = JOptionPane.showInputDialog(
                null, "Ingrese el nombre de la sede hospitalaria:");

        if (nombreSede == null || nombreSede.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, "El nombre de la sede no puede quedar vacio.");
            return null;
        }

        JTextField salas = crearCampoCantidad("1");
        JTextField consultorios = crearCampoCantidad("1");
        JTextField emergencias = crearCampoCantidad("1");

        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(new JLabel("Cantidad de salas medicas:"));
        panel.add(salas);
        panel.add(new JLabel("Cantidad de consultorios:"));
        panel.add(consultorios);
        panel.add(new JLabel("Cantidad de salas de emergencia:"));
        panel.add(emergencias);

        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Configuracion de la sede",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return null;
        }

        Configuracion configuracion;

        try {
            configuracion = new Configuracion(
                    nombreSede.trim(),
                    leerCantidad(salas, "salas medicas"),
                    leerCantidad(consultorios, "consultorios"),
                    leerCantidad(emergencias, "salas de emergencia"));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage(), "Cantidad no valida",
                    JOptionPane.WARNING_MESSAGE);
            return configurarSede();
        }

        if (!seleccionarEspaciosEspeciales(configuracion)) {
            return null;
        }

        return configuracion;
    }

    // Crea un campo de texto para escribir cantidades directamente
    public static JTextField crearCampoCantidad(String valorInicial) {
        return new JTextField(valorInicial, 8);
    }

    // Valida que la cantidad sea un numero entero mayor o igual a uno
    private static int leerCantidad(
            JTextField campo, String descripcion) {

        String texto = campo.getText().trim();

        try {
            int cantidad = Integer.parseInt(texto);

            if (cantidad < 1) {
                throw new NumberFormatException();
            }

            return cantidad;

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "La cantidad de " + descripcion
                    + " debe ser un numero entero mayor o igual a 1.");
        }
    }

    // Permite seleccionar los espacios especiales
    public static boolean seleccionarEspaciosEspeciales(
            Configuracion configuracion) {

        JComboBox<String> salaPreferencial =
                crearOpcionesEspacios(configuracion.getCantidadSalas());

        JComboBox<String> consultorioPreferencial =
                crearOpcionesEspacios(
                        configuracion.getCantidadConsultorios());

        JComboBox<String> salaCritica =
                crearOpcionesEspacios(
                        configuracion.getCantidadEmergencias());

        salaPreferencial.setSelectedItem(
                String.valueOf(configuracion.getSalaPreferencial()));

        consultorioPreferencial.setSelectedItem(
                String.valueOf(configuracion.getConsultorioPreferencial()));

        salaCritica.setSelectedItem(
                String.valueOf(configuracion.getSalaEmergenciaCritica()));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("Numero de la sala preferencial:"));
        panel.add(salaPreferencial);

        panel.add(new JLabel("Numero del consultorio preferencial:"));
        panel.add(consultorioPreferencial);

        panel.add(new JLabel("Numero de la sala de emergencia critica:"));
        panel.add(salaCritica);

        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Asignacion de espacios especiales",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return false;
        }

        configuracion.setSalaPreferencial(
                Integer.parseInt(
                        salaPreferencial.getSelectedItem().toString()));

        configuracion.setConsultorioPreferencial(
                Integer.parseInt(
                        consultorioPreferencial.getSelectedItem().toString()));

        configuracion.setSalaEmergenciaCritica(
                Integer.parseInt(
                        salaCritica.getSelectedItem().toString()));

        return true;
    }

    // Crea opciones segun la cantidad de espacios
    public static JComboBox<String> crearOpcionesEspacios(int cantidad) {

        JComboBox<String> opciones = new JComboBox<String>();

        for (int i = 1; i <= cantidad; i++) {
            opciones.addItem(String.valueOf(i));
        }

        return opciones;
    }

    // Muestra la configuracion en consola
    public static void mostrarConfiguracion(
            Configuracion configuracion) {

        if (configuracion == null) {
            System.out.println("La sede no tiene configuracion.");
            return;
        }

        System.out.println();
        System.out.println("===== CONFIGURACION DE LA SEDE =====");

        System.out.println("Sede hospitalaria: "
                + configuracion.getNombreSede());

        System.out.println("Cantidad de salas medicas: "
                + configuracion.getCantidadSalas());

        System.out.println("Cantidad de consultorios: "
                + configuracion.getCantidadConsultorios());

        System.out.println("Cantidad de salas de emergencia: "
                + configuracion.getCantidadEmergencias());

        System.out.println("Sala preferencial: "
                + configuracion.getSalaPreferencial());

        System.out.println("Consultorio preferencial: "
                + configuracion.getConsultorioPreferencial());

        System.out.println("Sala de emergencia critica: "
                + configuracion.getSalaEmergenciaCritica());

        System.out.println("====================================");
        System.out.println();
    }

    // Muestra el menu de administracion
    public static void mostrarMenuAdministrador(
            ArchivoConfiguracion archivo, String usuarioActual) {

        boolean continuar = true;

        while (continuar) {

            String opcion = solicitarOpcionMenuAdministrador();

            if (opcion == null || opcion.equals("7")) {

                continuar = false;

            } else if (opcion.equals("1")) {

                registrarUsuario(archivo);

            } else if (opcion.equals("2")) {

                JOptionPane.showMessageDialog(
                        null, archivo.consultarUsuarios());

            } else if (opcion.equals("3")) {

                editarUsuario(archivo);

            } else if (opcion.equals("4")) {

                eliminarUsuario(archivo, usuarioActual);

            } else if (opcion.equals("5")) {

                modificarConfiguracion(archivo);

            } else if (opcion.equals("6")) {

                restablecerConfiguracion(archivo);

            } else if (opcion.equals("8")) {

                int confirmacion = JOptionPane.showConfirmDialog(
                        null,
                        "Se reiniciara el sistema.\n\n"
                        + "Se eliminara la configuracion de la sede,\n"
                        + "los usuarios registrados y los tiquetes.\n\n"
                        + "Se conservara el administrador inicial:\n"
                        + "Usuario: admin\n"
                        + "Contrasena: prueba123\n\n"
                        + "Esta accion no se puede deshacer.\n"
                        + "¿Desea continuar?",
                        "Confirmar reinicio",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirmacion == JOptionPane.YES_OPTION) {

                    if (archivo.reiniciarSistema()) {

                        JOptionPane.showMessageDialog(
                                null,
                                "El sistema se reinicio correctamente.\n"
                                + "La aplicacion se cerrara para comenzar "
                                + "desde cero.");

                        System.exit(0);

                    } else {

                        JOptionPane.showMessageDialog(
                                null,
                                "No se pudo completar el reinicio.\n"
                                + "Revise los mensajes de la consola.");
                    }
                }

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Seleccione una opcion valida.");
            }
        }
    }

    // Muestra el menu de administracion con alineacion uniforme
    private static String solicitarOpcionMenuAdministrador() {
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 12));
        panel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));

        JLabel lineaSuperior = new JLabel("================================");
        lineaSuperior.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("MEDIQUEUE");
        titulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));

        JLabel subtitulo = new JLabel("SISTEMA DE GESTION HOSPITALARIA");
        subtitulo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        subtitulo.setFont(subtitulo.getFont().deriveFont(java.awt.Font.BOLD, 12f));

        JLabel lineaInferior = new JLabel("================================");
        lineaInferior.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        encabezado.add(lineaSuperior);
        encabezado.add(javax.swing.Box.createVerticalStrut(5));
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        encabezado.add(javax.swing.Box.createVerticalStrut(5));
        encabezado.add(lineaInferior);

        JPanel opciones = new JPanel();
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));

        agregarOpcionMenu(opciones, "GESTION DE USUARIOS", true);
        agregarOpcionMenu(opciones, "1. Registrar usuario", false);
        agregarOpcionMenu(opciones, "2. Consultar usuarios", false);
        agregarOpcionMenu(opciones, "3. Editar usuario", false);
        agregarOpcionMenu(opciones, "4. Eliminar usuario", false);
        opciones.add(javax.swing.Box.createVerticalStrut(10));

        agregarOpcionMenu(opciones, "CONFIGURACION DE LA SEDE", true);
        agregarOpcionMenu(opciones, "5. Modificar configuracion", false);
        agregarOpcionMenu(opciones, "6. Eliminar configuracion", false);
        opciones.add(javax.swing.Box.createVerticalStrut(10));

        agregarOpcionMenu(opciones, "SISTEMA", true);
        agregarOpcionMenu(opciones, "7. Cerrar sesion", false);
        agregarOpcionMenu(opciones, "8. Reiniciar sistema", false);

        JPanel inferior = new JPanel(new java.awt.BorderLayout(0, 5));
        inferior.add(new JLabel("Seleccione una opcion:"),
                java.awt.BorderLayout.NORTH);
        JTextField campoOpcion = new JTextField(18);
        inferior.add(campoOpcion, java.awt.BorderLayout.CENTER);

        panel.add(encabezado, java.awt.BorderLayout.NORTH);
        panel.add(opciones, java.awt.BorderLayout.CENTER);
        panel.add(inferior, java.awt.BorderLayout.SOUTH);

        int resultado = JOptionPane.showConfirmDialog(
                null, panel, "MEDIQUEUE - Administracion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return null;
        }

        return campoOpcion.getText().trim();
    }

    private static void agregarOpcionMenu(
            JPanel panel, String texto, boolean encabezado) {

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        if (encabezado) {
            etiqueta.setFont(
                    etiqueta.getFont().deriveFont(java.awt.Font.BOLD));
        }

        panel.add(etiqueta);
    }

    // Permite escoger que parte de la configuracion modificar
    public static void modificarConfiguracion(
            ArchivoConfiguracion archivo) {

        boolean continuar = true;

        while (continuar) {

            Configuracion actual = archivo.cargarConfiguracion();

            if (actual == null) {
                JOptionPane.showMessageDialog(
                        null,
                        "No hay una configuracion de sede registrada.");
                return;
            }

            String opcion = JOptionPane.showInputDialog(
                    null,
                    "MODIFICAR CONFIGURACION\n\n"
                    + "1. Cambiar nombre de la sede\n"
                    + "2. Modificar cantidades de salas y consultorios\n"
                    + "3. Modificar espacios especiales\n"
                    + "4. Volver al menu de administracion\n\n"
                    + "Seleccione una opcion:");

            if (opcion == null || opcion.equals("4")) {

                continuar = false;

            } else if (opcion.equals("1")) {

                modificarNombreSede(archivo);

            } else if (opcion.equals("2")) {

                modificarCantidades(archivo);

            } else if (opcion.equals("3")) {

                modificarEspaciosEspeciales(archivo);

            } else {

                JOptionPane.showMessageDialog(
                        null, "Seleccione una opcion valida.");
            }
        }
    }

    // Cambia unicamente el nombre de la sede
    public static void modificarNombreSede(
            ArchivoConfiguracion archivo) {

        Configuracion actual = archivo.cargarConfiguracion();

        if (actual == null) {
            return;
        }

        String nombre = JOptionPane.showInputDialog(
                null,
                "Ingrese el nuevo nombre de la sede:",
                actual.getNombreSede());

        if (nombre == null) {
            return;
        }

        if (nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, "El nombre no puede quedar vacio.");
            return;
        }

        actual.setNombreSede(nombre.trim());

        if (archivo.guardarConfiguracion(actual)) {

            JOptionPane.showMessageDialog(
                    null, "Nombre de la sede actualizado correctamente.");

            mostrarConfiguracion(actual);

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudo guardar el cambio.");
        }
    }

    // Modifica las cantidades sin cambiar el nombre de la sede
    public static void modificarCantidades(
            ArchivoConfiguracion archivo) {

        Configuracion actual = archivo.cargarConfiguracion();

        if (actual == null) {
            return;
        }

        JTextField salas = crearCampoCantidad(
                String.valueOf(actual.getCantidadSalas()));
        JTextField consultorios = crearCampoCantidad(
                String.valueOf(actual.getCantidadConsultorios()));
        JTextField emergencias = crearCampoCantidad(
                String.valueOf(actual.getCantidadEmergencias()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(new JLabel("Cantidad de salas medicas:"));
        panel.add(salas);
        panel.add(new JLabel("Cantidad de consultorios:"));
        panel.add(consultorios);
        panel.add(new JLabel("Cantidad de salas de emergencia:"));
        panel.add(emergencias);

        int resultado = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Modificar cantidades",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        // Guarda las cantidades anteriores para poder recuperarlas
        int salasAnteriores = actual.getCantidadSalas();
        int consultoriosAnteriores = actual.getCantidadConsultorios();
        int emergenciasAnteriores = actual.getCantidadEmergencias();

        int nuevasSalas;
        int nuevosConsultorios;
        int nuevasEmergencias;

        try {
            nuevasSalas = leerCantidad(salas, "salas medicas");
            nuevosConsultorios = leerCantidad(consultorios, "consultorios");
            nuevasEmergencias = leerCantidad(
                    emergencias, "salas de emergencia");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage(), "Cantidad no valida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        actual.setCantidadSalas(nuevasSalas);
        actual.setCantidadConsultorios(nuevosConsultorios);
        actual.setCantidadEmergencias(nuevasEmergencias);

        if (!seleccionarEspaciosEspeciales(actual)) {

            // Recupera las cantidades anteriores si se cancela
            actual.setCantidadSalas(salasAnteriores);
            actual.setCantidadConsultorios(consultoriosAnteriores);
            actual.setCantidadEmergencias(emergenciasAnteriores);

            return;
        }

        if (archivo.guardarConfiguracion(actual)) {

            JOptionPane.showMessageDialog(
                    null, "Cantidades actualizadas correctamente.");

            mostrarConfiguracion(actual);

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudieron guardar las cantidades.");
        }
    }

    // Modifica unicamente los espacios especiales
    public static void modificarEspaciosEspeciales(
            ArchivoConfiguracion archivo) {

        Configuracion actual = archivo.cargarConfiguracion();

        if (actual == null) {
            return;
        }

        if (!seleccionarEspaciosEspeciales(actual)) {
            return;
        }

        if (archivo.guardarConfiguracion(actual)) {

            JOptionPane.showMessageDialog(
                    null, "Espacios especiales actualizados correctamente.");

            mostrarConfiguracion(actual);

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudieron guardar los cambios.");
        }
    }

    // Elimina la configuracion sin borrar usuarios ni solicitar otra
    public static void restablecerConfiguracion(
            ArchivoConfiguracion archivo) {

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "Se eliminara completamente la configuracion de la sede.\n"
                + "Los usuarios registrados se conservaran.\n"
                + "No se solicitara una nueva configuracion ahora.\n\n"
                + "¿Desea continuar?",
                "Eliminar configuracion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        if (archivo.eliminarConfiguracion()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Configuracion eliminada correctamente.\n"
                    + "Los usuarios se conservaron.\n"
                    + "La sede se podra configurar nuevamente "
                    + "al iniciar el sistema.");

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudo eliminar la configuracion.");
        }
    }

    // Registra un usuario nuevo
    public static void registrarUsuario(
            ArchivoConfiguracion archivo) {

        String nombreUsuario = JOptionPane.showInputDialog(
                null, "Ingrese el nombre del nuevo usuario:");

        if (nombreUsuario == null) {
            return;
        }

        JPasswordField campoContrasena = new JPasswordField();

        int resultado = JOptionPane.showConfirmDialog(
                null,
                campoContrasena,
                "Ingrese la contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String contrasena = new String(
                campoContrasena.getPassword());

        JComboBox<String> roles = crearOpcionesRoles();

        int seleccion = JOptionPane.showConfirmDialog(
                null,
                roles,
                "Seleccione el rol",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (seleccion != JOptionPane.OK_OPTION) {
            return;
        }

        String rol = roles.getSelectedItem().toString();

        if (archivo.agregarUsuario(
                nombreUsuario.trim(), contrasena, rol)) {

            JOptionPane.showMessageDialog(
                    null, "Usuario registrado correctamente.");

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudo registrar el usuario.");
        }
    }

    // Crea las opciones de roles permitidos
    public static JComboBox<String> crearOpcionesRoles() {

        JComboBox<String> roles = new JComboBox<String>();

        roles.addItem("Administrador");
        roles.addItem("Recepcionista");
        roles.addItem("Medico");
        roles.addItem("Enfermero");

        return roles;
    }

    // Permite editar un usuario
    public static void editarUsuario(
            ArchivoConfiguracion archivo) {

        JOptionPane.showMessageDialog(
                null, archivo.consultarUsuarios());

        String usuarioAnterior = JOptionPane.showInputDialog(
                null, "Ingrese el usuario que desea editar:");

        if (usuarioAnterior == null) {
            return;
        }

        String nombreNuevo = JOptionPane.showInputDialog(
                null, "Ingrese el nuevo nombre de usuario:");

        if (nombreNuevo == null) {
            return;
        }

        JPasswordField campoContrasena = new JPasswordField();

        int resultado = JOptionPane.showConfirmDialog(
                null,
                campoContrasena,
                "Ingrese la nueva contraseña",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        String contrasenaNueva = new String(
                campoContrasena.getPassword());

        JComboBox<String> roles = crearOpcionesRoles();

        int seleccion = JOptionPane.showConfirmDialog(
                null,
                roles,
                "Seleccione el nuevo rol",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (seleccion != JOptionPane.OK_OPTION) {
            return;
        }

        String rolNuevo = roles.getSelectedItem().toString();

        if (archivo.editarUsuario(
                usuarioAnterior.trim(),
                nombreNuevo.trim(),
                contrasenaNueva,
                rolNuevo)) {

            JOptionPane.showMessageDialog(
                    null, "Usuario actualizado correctamente.");

        } else {

            JOptionPane.showMessageDialog(
                    null, "No se pudo actualizar el usuario.");
        }
    }

    // Elimina un usuario con confirmacion
    public static void eliminarUsuario(
            ArchivoConfiguracion archivo, String usuarioActual) {

        JOptionPane.showMessageDialog(
                null, archivo.consultarUsuarios());

        String nombreUsuario = JOptionPane.showInputDialog(
                null, "Ingrese el usuario que desea eliminar:");

        if (nombreUsuario == null) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Esta seguro de eliminar a " + nombreUsuario + "?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {

            if (archivo.eliminarUsuario(
                    nombreUsuario.trim(), usuarioActual)) {

                JOptionPane.showMessageDialog(
                        null, "Usuario eliminado correctamente.");

            } else {

                JOptionPane.showMessageDialog(
                        null, "No se pudo eliminar el usuario.");
            }
        }
    }
}