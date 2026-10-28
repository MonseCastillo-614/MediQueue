/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package cr.ac.ufidelitas.mediqueueapp;
import java.io.IOException;
import java.nio.file.Paths;
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

    // Registros guardados en listas enlazadas.
    private static Paciente primerPaciente;
    private static Paciente ultimoPaciente;
    private static Tiquete primerTiquete;
    private static Tiquete ultimoTiquete;

    // Administra las colas de prioridad de los tiquetes pendientes.
    private static final GestorTiquetes gestorTiquetes = new GestorTiquetes();

    private static final PersistenciaDatos persistenciaDatos =
            new PersistenciaDatos(Paths.get("."));

    // Adaptador para guardar y recuperar pacientes.
    private static final AdaptadorJson<Paciente> ADAPTADOR_PACIENTES =
            new AdaptadorJson<Paciente>() {
        @Override
        public String convertirAJson(Paciente datos) {
            return pacientesAJson(datos);
        }

        @Override
        public Paciente convertirDesdeJson(String json) {
            return pacientesDesdeJson(json);
        }
    };

    // Adaptador para guardar y recuperar tiquetes.
    private static final AdaptadorJson<Tiquete> ADAPTADOR_TIQUETES =
            new AdaptadorJson<Tiquete>() {
        @Override
        public String convertirAJson(Tiquete datos) {
            return tiquetesAJson(datos);
        }

        @Override
        public Tiquete convertirDesdeJson(String json) {
            return tiquetesDesdeJson(json);
        }
    };

    public static void main(String[] args) {

        ArchivoConfiguracion archivo = new ArchivoConfiguracion();

        // Carga los datos guardados.
        if (!cargarDatosPersistentes()) {
            return;
        }

        // Carga la configuracion.
        Configuracion configuracion = archivo.cargarConfiguracion();

        // Si no hay configuracion, la pide al usuario.
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

        // Inicia sesion.
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

        // Muestra el menu si el usuario es administrador.
        if (usuarioIngresado.getRol()
                .equalsIgnoreCase("Administrador")) {

            mostrarMenuAdministrador(
                    archivo,
                    usuarioIngresado.getNombreUsuario());
        }
    }

    // Inicia sesion.
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

    // Configura la sede.
    public static Configuracion configurarSede() {

        String nombreSede = JOptionPane.showInputDialog(
                null, "Ingrese el nombre de la sede hospitalaria:");

        if (nombreSede == null || nombreSede.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null, "El nombre de la sede no puede quedar vacio.");
            return null;
        }

        // Selecciona las cantidades de cada espacio.
        JComboBox<String> salas = crearOpcionesEspacios(999);
        JComboBox<String> consultorios = crearOpcionesEspacios(999);
        JComboBox<String> emergencias = crearOpcionesEspacios(999);

        salas.setPrototypeDisplayValue("999");
        consultorios.setPrototypeDisplayValue("999");
        emergencias.setPrototypeDisplayValue("999");

        salas.setSelectedItem("1");
        consultorios.setSelectedItem("1");
        emergencias.setSelectedItem("1");

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
                    leerCantidadSeleccionada(salas, "salas medicas"),
                    leerCantidadSeleccionada(consultorios, "consultorios"),
                    leerCantidadSeleccionada(emergencias, "salas de emergencia"));
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

    // Selecciona los espacios especiales.
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

        // Muestra los selectores de espacios especiales.
        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 10));
        panel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14));

        salaPreferencial.setPrototypeDisplayValue("999");
        consultorioPreferencial.setPrototypeDisplayValue("999");
        salaCritica.setPrototypeDisplayValue("999");

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

    // Crea las opciones de cantidad.
    public static JComboBox<String> crearOpcionesEspacios(int cantidad) {

        JComboBox<String> opciones = new JComboBox<String>();

        for (int i = 1; i <= cantidad; i++) {
            opciones.addItem(String.valueOf(i));
        }

        return opciones;
    }

    // Muestra los datos de la sede.
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

    // Menu de administracion.
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

            } else if (opcion.equals("9")) {

                registrarPaciente();

            } else if (opcion.equals("10")) {

                generarTiquete();

            } else if (opcion.equals("11")) {

                consultarPacientes();

            } else if (opcion.equals("12")) {

                consultarTiquetes();

            } else if (opcion.equals("13")) {

                consultarColasPrioridad();

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
                        try {
                            // Se elimina tambien el archivo de pacientes.
                            
                            persistenciaDatos.eliminarPacientes();
                            primerPaciente = null;
                            ultimoPaciente = null;
                            primerTiquete = null;
                            ultimoTiquete = null;

                            JOptionPane.showMessageDialog(
                                    null,
                                    "El sistema se reinicio correctamente.\n"
                                    + "La aplicacion se cerrara para comenzar "
                                    + "desde cero.");

                            System.exit(0);

                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "La configuracion se reinicio, pero no se "
                                    + "pudo eliminar pacientes.json.\n"
                                    + ex.getMessage(),
                                    "Error al reiniciar",
                                    JOptionPane.ERROR_MESSAGE);
                        }

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

    // Arma el menu de administracion.
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

        agregarOpcionMenu(opciones, "GESTION DE PACIENTES Y TIQUETES", true);
        agregarOpcionMenu(opciones, "9. Registrar paciente/cliente", false);
        agregarOpcionMenu(opciones, "10. Generar tiquete de atencion", false);
        agregarOpcionMenu(opciones, "11. Consultar pacientes", false);
        agregarOpcionMenu(opciones, "12. Consultar tiquetes", false);
        agregarOpcionMenu(opciones, "13. Consultar colas de prioridad", false);
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

    // Carga los pacientes primero para poder enlazarlos con sus tiquetes.
    private static boolean cargarDatosPersistentes() {
        try {
            primerPaciente = persistenciaDatos.cargarPacientes(ADAPTADOR_PACIENTES);
            ultimoPaciente = obtenerUltimoPaciente(primerPaciente);

            primerTiquete = persistenciaDatos.cargarTiquetes(ADAPTADOR_TIQUETES);
            ultimoTiquete = obtenerUltimoTiquete(primerTiquete);

            // Al iniciar, las colas se reconstruyen con los tiquetes pendientes.
            Tiquete actual = primerTiquete;
            while (actual != null) {
                if (!actual.estaAtendido()) {
                    gestorTiquetes.agregarTiquete(actual);
                }
                actual = actual.getSiguienteRegistro();
            }

            return true;

        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudieron cargar los pacientes o tiquetes guardados.\n"
                    + "Para evitar sobrescribir datos, la aplicacion se cerrara.\n\n"
                    + ex.getMessage(),
                    "Error al recuperar datos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean guardarPacientes() {
        try {
            persistenciaDatos.guardarPacientes(primerPaciente, ADAPTADOR_PACIENTES);
            return true;
        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    null, "No se pudieron guardar los pacientes.\n" + ex.getMessage(),
                    "Error al guardar pacientes", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static boolean guardarTiquetes() {
        try {
            persistenciaDatos.guardarTiquetes(primerTiquete, ADAPTADOR_TIQUETES);
            return true;
        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    null, "No se pudieron guardar los tiquetes.\n" + ex.getMessage(),
                    "Error al guardar tiquetes", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String pacientesAJson(Paciente datos) {
        StringBuilder json = new StringBuilder("{\"pacientes\":[");
        boolean primero = true;

        for (Paciente actual = datos; actual != null;
                actual = actual.getSiguientePaciente()) {
            if (!primero) {
                json.append(',');
            }
            primero = false;
            json.append('{')
                    .append("\"identificacion\":")
                    .append(EscritorJson.cadena(actual.getIdentificacion()))
                    .append(",\"nombre\":")
                    .append(EscritorJson.cadena(actual.getNombre()))
                    .append(",\"edad\":")
                    .append(actual.getEdad())
                    .append(",\"tipoSeguro\":")
                    .append(EscritorJson.cadena(actual.getTipoSeguro()))
                    .append('}');
        }
        return json.append("]}").toString();
    }

    private static Paciente pacientesDesdeJson(String json) {
        JsonValor raiz = new LectorJson(json).leer();
        raiz.exigirTipo(JsonValor.OBJETO);
        JsonValor arreglo = raiz.campo("pacientes");
        arreglo.exigirTipo(JsonValor.ARREGLO);

        Paciente primero = null;
        Paciente ultimo = null;
        JsonValor actual = arreglo.getPrimerHijo();
        while (actual != null) {
            actual.exigirTipo(JsonValor.OBJETO);
            Paciente paciente = new Paciente(
                    actual.campo("identificacion").comoTexto(),
                    actual.campo("nombre").comoTexto(),
                    actual.campo("edad").comoEntero(),
                    actual.campo("tipoSeguro").comoTexto());

            if (primero == null) {
                primero = paciente;
            } else {
                ultimo.setSiguientePaciente(paciente);
            }
            ultimo = paciente;
            actual = actual.getSiguiente();
        }
        return primero;
    }

    private static String tiquetesAJson(Tiquete datos) {
        StringBuilder json = new StringBuilder("{\"tiquetes\":[");
        boolean primero = true;

        for (Tiquete actual = datos; actual != null;
                actual = actual.getSiguienteRegistro()) {
            if (actual.getPaciente() == null) {
                throw new IllegalArgumentException("Hay un tiquete sin paciente asociado.");
            }
            if (!primero) {
                json.append(',');
            }
            primero = false;
            json.append('{')
                    .append("\"id\":")
                    .append(EscritorJson.cadena(actual.getId()))
                    .append(",\"identificacionPaciente\":")
                    .append(EscritorJson.cadena(actual.getPaciente().getIdentificacion()))
                    .append(",\"fechaHoraIngreso\":")
                    .append(EscritorJson.cadena(actual.getFechaHoraIngreso()))
                    .append(",\"fechaHoraAtencion\":")
                    .append(EscritorJson.cadena(actual.getFechaHoraAtencion()))
                    .append(",\"prioridad\":")
                    .append(EscritorJson.cadena(actual.getPrioridad()))
                    .append(",\"tipoAtencion\":")
                    .append(EscritorJson.cadena(actual.getTipoAtencion()))
                    .append(",\"atencion\":")
                    .append(EscritorJson.cadena(actual.getAtencion()))
                    .append('}');
        }
        return json.append("]}").toString();
    }

    private static Tiquete tiquetesDesdeJson(String json) {
        JsonValor raiz = new LectorJson(json).leer();
        raiz.exigirTipo(JsonValor.OBJETO);
        JsonValor arreglo = raiz.campo("tiquetes");
        arreglo.exigirTipo(JsonValor.ARREGLO);

        Tiquete primero = null;
        Tiquete ultimo = null;
        JsonValor actual = arreglo.getPrimerHijo();
        while (actual != null) {
            actual.exigirTipo(JsonValor.OBJETO);
            String id = actual.campo("id").comoTexto();
            String identificacionPaciente = actual.campo("identificacionPaciente").comoTexto();
            Paciente paciente = buscarPacientePorIdentificacion(identificacionPaciente);
            if (paciente == null) {
                throw new IllegalArgumentException(
                        "El tiquete " + id + " referencia a un paciente que no existe.");
            }

            Tiquete tiquete = new Tiquete(
                    id, paciente,
                    actual.campo("fechaHoraIngreso").comoTexto(),
                    actual.campo("fechaHoraAtencion").comoTexto(),
                    actual.campo("prioridad").comoTexto(),
                    actual.campo("tipoAtencion").comoTexto(),
                    actual.campo("atencion").comoTexto());
            if (primero == null) {
                primero = tiquete;
            } else {
                ultimo.setSiguienteRegistro(tiquete);
            }
            ultimo = tiquete;
            actual = actual.getSiguiente();
        }
        return primero;
    }

    private static Paciente buscarPacientePorIdentificacion(String identificacion) {
        Paciente actual = primerPaciente;
        while (actual != null) {
            if (actual.getIdentificacion().equalsIgnoreCase(identificacion)) {
                return actual;
            }
            actual = actual.getSiguientePaciente();
        }
        return null;
    }

    private static Paciente buscarPacientePorIndice(int indice) {
        Paciente actual = primerPaciente;
        int posicion = 0;
        while (actual != null) {
            if (posicion == indice) {
                return actual;
            }
            posicion++;
            actual = actual.getSiguientePaciente();
        }
        return null;
    }

    private static Paciente obtenerUltimoPaciente(Paciente primero) {
        Paciente actual = primero;
        if (actual == null) return null;
        while (actual.getSiguientePaciente() != null) {
            actual = actual.getSiguientePaciente();
        }
        return actual;
    }

    private static Tiquete obtenerUltimoTiquete(Tiquete primero) {
        Tiquete actual = primero;
        if (actual == null) return null;
        while (actual.getSiguienteRegistro() != null) {
            actual = actual.getSiguienteRegistro();
        }
        return actual;
    }

    private static int contarTiquetes() {
        int cantidad = 0;
        Tiquete actual = primerTiquete;
        while (actual != null) {
            cantidad++;
            actual = actual.getSiguienteRegistro();
        }
        return cantidad;
    }

    private static void agregarPaciente(Paciente paciente) {
        paciente.setSiguientePaciente(null);
        if (primerPaciente == null) {
            primerPaciente = paciente;
        } else {
            ultimoPaciente.setSiguientePaciente(paciente);
        }
        ultimoPaciente = paciente;
    }

    private static void quitarUltimoPaciente() {
        if (primerPaciente == null) return;
        if (primerPaciente == ultimoPaciente) {
            primerPaciente = null;
            ultimoPaciente = null;
            return;
        }
        Paciente actual = primerPaciente;
        while (actual.getSiguientePaciente() != ultimoPaciente) {
            actual = actual.getSiguientePaciente();
        }
        actual.setSiguientePaciente(null);
        ultimoPaciente = actual;
    }

    private static void agregarTiquete(Tiquete tiquete) {
        tiquete.setSiguienteRegistro(null);
        if (primerTiquete == null) {
            primerTiquete = tiquete;
        } else {
            ultimoTiquete.setSiguienteRegistro(tiquete);
        }
        ultimoTiquete = tiquete;
    }

    private static void quitarUltimoTiquete() {
        if (primerTiquete == null) return;
        if (primerTiquete == ultimoTiquete) {
            primerTiquete = null;
            ultimoTiquete = null;
            return;
        }
        Tiquete actual = primerTiquete;
        while (actual.getSiguienteRegistro() != ultimoTiquete) {
            actual = actual.getSiguienteRegistro();
        }
        actual.setSiguienteRegistro(null);
        ultimoTiquete = actual;
    }

    // Registra un paciente.
    private static void registrarPaciente() {
        JTextField identificacion = new JTextField(18);
        JTextField nombre = new JTextField(18);
        JTextField edad = new JTextField(18);
        JComboBox<String> seguro = new JComboBox<>(new String[]{
            "CCSS", "Privado", "No asegurado", "Otro"
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JLabel("Identificacion:"));
        panel.add(identificacion);
        panel.add(new JLabel("Nombre completo:"));
        panel.add(nombre);
        panel.add(new JLabel("Edad:"));
        panel.add(edad);
        panel.add(new JLabel("Tipo de seguro:"));
        panel.add(seguro);

        int resultado = JOptionPane.showConfirmDialog(
                null, panel, "Registrar paciente/cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;

        String id = identificacion.getText().trim();
        String nombrePaciente = nombre.getText().trim();
        if (id.isEmpty() || nombrePaciente.isEmpty() || edad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Complete todos los campos.");
            return;
        }

        Paciente existente = primerPaciente;
        while (existente != null) {
            if (existente.getIdentificacion().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(null, "Ya existe un paciente con esa identificacion.");
                return;
            }
            existente = existente.getSiguientePaciente();
        }

        try {
            int edadPaciente = Integer.parseInt(edad.getText().trim());
            Paciente paciente = new Paciente(id, nombrePaciente, edadPaciente,
                    String.valueOf(seguro.getSelectedItem()));
            agregarPaciente(paciente);

            if (!guardarPacientes()) {
                quitarUltimoPaciente();
                return;
            }
            JOptionPane.showMessageDialog(
                    null, "Paciente registrado correctamente.\n" + paciente);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "La edad debe ser un numero entero.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    // Genera un tiquete para un paciente registrado.
    private static void generarTiquete() {
        if (primerPaciente == null) {
            JOptionPane.showMessageDialog(null, "Primero registre un paciente.");
            return;
        }

        JComboBox<String> pacienteCombo = new JComboBox<>();
        Paciente pacienteActual = primerPaciente;
        while (pacienteActual != null) {
            pacienteCombo.addItem(pacienteActual.getIdentificacion() + " - "
                    + pacienteActual.getNombre());
            pacienteActual = pacienteActual.getSiguientePaciente();
        }

        JTextField idTiquete = new JTextField("T" + (contarTiquetes() + 1), 15);
        JComboBox<String> tipo = new JComboBox<>(new String[]{
            "Consulta externa (C)", "Sala (S)", "Emergencia (E)"
        });
        JComboBox<String> atencion = new JComboBox<>(new String[]{
            "Normal (N)", "Preferencial (P)"
        });
        JComboBox<String> prioridad = new JComboBox<>(new String[]{
            Tiquete.PRIORIDAD_CRITICA, Tiquete.PRIORIDAD_URGENTE,
            Tiquete.PRIORIDAD_REGULAR, Tiquete.PRIORIDAD_CONTROL
        });

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JLabel("Paciente:"));
        panel.add(pacienteCombo);
        panel.add(new JLabel("ID del tiquete:"));
        panel.add(idTiquete);
        panel.add(new JLabel("Tipo de atencion:"));
        panel.add(tipo);
        panel.add(new JLabel("Categoria:"));
        panel.add(atencion);
        panel.add(new JLabel("Prioridad medica:"));
        panel.add(prioridad);

        int resultado = JOptionPane.showConfirmDialog(
                null, panel, "Generar tiquete de atencion",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resultado != JOptionPane.OK_OPTION) return;

        String id = idTiquete.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El ID del tiquete es obligatorio.");
            return;
        }

        Tiquete existente = primerTiquete;
        while (existente != null) {
            if (existente.getId().equalsIgnoreCase(id)) {
                JOptionPane.showMessageDialog(null, "Ya existe un tiquete con ese ID.");
                return;
            }
            existente = existente.getSiguienteRegistro();
        }

        Paciente paciente = buscarPacientePorIndice(pacienteCombo.getSelectedIndex());
        if (paciente == null) {
            JOptionPane.showMessageDialog(null, "Seleccione un paciente valido.");
            return;
        }

        if (gestorTiquetes.tienePendiente(paciente.getIdentificacion())) {
            JOptionPane.showMessageDialog(null,
                    "Este paciente ya tiene un tiquete pendiente.");
            return;
        }

        String tipoSeleccionado = String.valueOf(tipo.getSelectedItem());
        String tipoCodigo = tipoSeleccionado.contains("(C)") ? Tiquete.TIPO_CONSULTA
                : tipoSeleccionado.contains("(S)") ? Tiquete.TIPO_SALA : Tiquete.TIPO_EMERGENCIA;
        String atencionCodigo = String.valueOf(atencion.getSelectedItem()).contains("(P)")
                ? Tiquete.ATENCION_PREFERENCIAL : Tiquete.ATENCION_NORMAL;

        try {
            Tiquete tiquete = new Tiquete(id, paciente,
                    String.valueOf(prioridad.getSelectedItem()), tipoCodigo, atencionCodigo);
            agregarTiquete(tiquete);

            if (!guardarTiquetes()) {
                quitarUltimoTiquete();
                return;
            }

            // El tiquete guardado tambien se agrega a su cola de prioridad.
            try {
                gestorTiquetes.agregarTiquete(tiquete);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                quitarUltimoTiquete();
                guardarTiquetes();
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(
                    null, "Tiquete generado correctamente.\n" + tiquete);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private static void consultarPacientes() {
        if (primerPaciente == null) {
            JOptionPane.showMessageDialog(null, "Todavia no hay pacientes registrados.");
            return;
        }
        StringBuilder texto = new StringBuilder("PACIENTES REGISTRADOS\n\n");
        Paciente actual = primerPaciente;
        while (actual != null) {
            texto.append(actual).append("\n");
            actual = actual.getSiguientePaciente();
        }
        JOptionPane.showMessageDialog(null, texto.toString(),
                "Consultar pacientes", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void consultarTiquetes() {
        if (primerTiquete == null) {
            JOptionPane.showMessageDialog(null, "Todavia no hay tiquetes registrados.");
            return;
        }
        StringBuilder texto = new StringBuilder("TIQUETES REGISTRADOS\n\n");
        Tiquete actual = primerTiquete;
        while (actual != null) {
            texto.append(actual).append("\n\n");
            actual = actual.getSiguienteRegistro();
        }
        JOptionPane.showMessageDialog(null, texto.toString(),
                "Consultar tiquetes", JOptionPane.INFORMATION_MESSAGE);
    }

    // Muestra los tiquetes pendientes segun el orden de cada cola.
    private static void consultarColasPrioridad() {
        if (gestorTiquetes.totalPendientes() == 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay tiquetes pendientes en las colas.");
            return;
        }

        StringBuilder texto = new StringBuilder("COLAS DE PRIORIDAD\n\n");
        agregarColaAlTexto(texto, "E", "EMERGENCIAS");
        agregarColaAlTexto(texto, "S", "SALAS");
        agregarColaAlTexto(texto, "C", "CONSULTAS");

        JOptionPane.showMessageDialog(null, texto.toString(),
                "Colas de prioridad", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void agregarColaAlTexto(StringBuilder texto,
            String tipo, String titulo) {
        texto.append(titulo).append("\n");
        int cantidad = gestorTiquetes.cantidadPendientes(tipo);

        if (cantidad == 0) {
            texto.append("  Sin pacientes pendientes.\n\n");
            return;
        }

        for (int i = 0; i < cantidad; i++) {
            Tiquete tiquete = gestorTiquetes.obtenerPendiente(tipo, i);
            texto.append("  ").append(i + 1).append(". ")
                    .append(tiquete.getId()).append(" - ")
                    .append(tiquete.getNombrePaciente())
                    .append(" | Prioridad: ").append(tiquete.getPrioridad())
                    .append(" | Atencion: ").append(tiquete.getAtencion())
                    .append("\n");
        }
        texto.append("\n");
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

    // Permite modificar la configuracion.
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

    // Cambia el nombre de la sede.
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

    // Lee la cantidad seleccionada.
    private static int leerCantidadSeleccionada(
            JComboBox<String> campo, String descripcion) {
        try {
            int cantidad = Integer.parseInt(campo.getSelectedItem().toString());
            if (cantidad < 1) {
                throw new NumberFormatException();
            }
            return cantidad;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(
                    "La cantidad de " + descripcion
                    + " debe ser un numero entero mayor o igual a 1.");
        }
    }

    // Modifica las cantidades de espacios.
    public static void modificarCantidades(
            ArchivoConfiguracion archivo) {

        Configuracion actual = archivo.cargarConfiguracion();

        if (actual == null) {
            return;
        }

        // Permite seleccionar las nuevas cantidades.
        JComboBox<String> salas = crearOpcionesEspacios(999);
        JComboBox<String> consultorios = crearOpcionesEspacios(999);
        JComboBox<String> emergencias = crearOpcionesEspacios(999);

        salas.setPrototypeDisplayValue("999");
        consultorios.setPrototypeDisplayValue("999");
        emergencias.setPrototypeDisplayValue("999");

        salas.setSelectedItem(String.valueOf(actual.getCantidadSalas()));
        consultorios.setSelectedItem(String.valueOf(actual.getCantidadConsultorios()));
        emergencias.setSelectedItem(String.valueOf(actual.getCantidadEmergencias()));

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

        // Guarda las cantidades actuales.
        int salasAnteriores = actual.getCantidadSalas();
        int consultoriosAnteriores = actual.getCantidadConsultorios();
        int emergenciasAnteriores = actual.getCantidadEmergencias();

        int nuevasSalas;
        int nuevosConsultorios;
        int nuevasEmergencias;

        try {
            nuevasSalas = leerCantidadSeleccionada(salas, "salas medicas");
            nuevosConsultorios = leerCantidadSeleccionada(consultorios, "consultorios");
            nuevasEmergencias = leerCantidadSeleccionada(
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

            // Si se cancela, deja las cantidades anteriores.
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

    // Modifica los espacios especiales.
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

    // Elimina la configuracion de la sede.
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

    // Registra un usuario.
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

    // Crea la lista de roles.
    public static JComboBox<String> crearOpcionesRoles() {

        JComboBox<String> roles = new JComboBox<String>();

        roles.addItem("Administrador");
        roles.addItem("Recepcionista");
        roles.addItem("Medico");
        roles.addItem("Enfermero");

        return roles;
    }

    // Edita un usuario.
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

    // Elimina un usuario.
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