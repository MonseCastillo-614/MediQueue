/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion;
import estructuras.ArbolABB;
import estructuras.NodoAtencion;
import estructuras.NodoHistorialPaciente;
import estructuras.NodoPila;
import modelo.Atencion;
import modelo.HistorialPaciente;
import modelo.Paciente;
import modelo.RegistroHistorial;
import modelo.Tiquete;
/**
 *
 * @author casti
 */
public class GestorBusquedas {

    private ArbolABB indicePacientes;
    private Paciente primerPaciente;
    private Tiquete primerTiquete;
    private NodoAtencion primerAtencion;
    private NodoHistorialPaciente primerHistorial;

    /**
     * Constructor del gestor de busquedas.
     */
    public GestorBusquedas() {

        indicePacientes = new ArbolABB();

        primerPaciente = null;
        primerTiquete = null;
        primerAtencion = null;
        primerHistorial = null;
    }

    /**
     * Carga las listas existentes del sistema
     * y construye el indice centralizado de pacientes.
     */
    public void cargarDatos(
            Paciente pacientes,
            Tiquete tiquetes,
            NodoAtencion atenciones,
            NodoHistorialPaciente historiales) {

        primerPaciente = pacientes;
        primerTiquete = tiquetes;
        primerAtencion = atenciones;
        primerHistorial = historiales;

        construirIndicePacientes();
    }

    /**
     * Construye el indice ABB utilizando la lista
     * enlazada de pacientes registrados.
     */
    public void construirIndicePacientes() {

        indicePacientes = new ArbolABB();

        Paciente actual = primerPaciente;

        while (actual != null) {

            indicePacientes.insertar(actual);

            actual = actual.getSiguientePaciente();
        }
    }

    /**
     * Inserta un paciente nuevo en el indice.
     *
     * @param paciente paciente que se desea insertar
     * @return true si se inserta correctamente
     */
    public boolean insertarPaciente(Paciente paciente) {

        if (paciente == null) {
            return false;
        }

        return indicePacientes.insertar(paciente);
    }

    /**
     * Busca un paciente por identificacion.
     *
     * @param identificacion identificacion del paciente
     * @return paciente encontrado o null
     */
    public Paciente buscarPaciente(String identificacion) {

        return indicePacientes.buscar(identificacion);
    }

    /**
     * Busca un tiquete mediante su identificador.
     *
     * @param idTiquete identificador del tiquete
     * @return tiquete encontrado o null
     */
    public Tiquete buscarTiquete(String idTiquete) {

        if (idTiquete == null
                || idTiquete.trim().isEmpty()) {
            return null;
        }

        Tiquete actual = primerTiquete;

        while (actual != null) {

            if (actual.getId().equalsIgnoreCase(
                    idTiquete.trim())) {

                return actual;
            }

            actual = actual.getSiguienteRegistro();
        }

        return null;
    }

    /**
     * Busca el historial de un paciente por identificacion.
     *
     * @param identificacion identificacion del paciente
     * @return historial encontrado o null
     */
    public HistorialPaciente buscarHistorial(String identificacion) {

        if (identificacion == null
                || identificacion.trim().isEmpty()) {
            return null;
        }

        NodoHistorialPaciente actual = primerHistorial;

        while (actual != null) {

            HistorialPaciente historial = actual.getDato();

            if (historial.getIdentificacion()
                    .equalsIgnoreCase(identificacion.trim())) {

                return historial;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    /**
     * Obtiene los registros del historial de un paciente.
     *
     * @param identificacion identificacion del paciente
     * @return informacion del historial
     */
    public String consultarHistorial(String identificacion) {

        HistorialPaciente historial =
                buscarHistorial(identificacion);

        if (historial == null
                || historial.getRegistros().estaVacia()) {

            return "No se encontraron registros para el paciente.";
        }

        StringBuilder resultado =
                new StringBuilder("HISTORIAL DEL PACIENTE\n\n");

        resultado.append("Identificacion: ")
                .append(historial.getIdentificacion())
                .append("\n\n");

        NodoPila actual =
                historial.getRegistros().getTope();

        while (actual != null) {

            RegistroHistorial registro = actual.getDato();

            resultado.append(registro.toString())
                    .append("\n");

            resultado.append(registro.getAtencion().toString())
                    .append("\n\n");

            actual = actual.getSiguiente();
        }

        return resultado.toString();
    }

    /**
     * Busca las atenciones asignadas a un medico.
     *
     * @param nombreMedico nombre del medico
     * @return atenciones encontradas
     */
    public String buscarPorMedico(String nombreMedico) {

        if (nombreMedico == null
                || nombreMedico.trim().isEmpty()) {

            return "Debe indicar el nombre del medico.";
        }

        StringBuilder resultado = new StringBuilder();

        NodoAtencion actual = primerAtencion;

        int encontrados = 0;

        while (actual != null) {

            Atencion atencion = actual.getDato();

            if (atencion.getMedicoAsignado()
                    .equalsIgnoreCase(nombreMedico.trim())) {

                resultado.append(atencion.toString())
                        .append("\n\n");

                encontrados++;
            }

            actual = actual.getSiguiente();
        }

        if (encontrados == 0) {
            return "No se encontraron atenciones para ese medico.";
        }

        return "ATENCIONES POR MEDICO\n\n"
                + resultado.toString();
    }

    /**
     * Busca las atenciones por fecha.
     * Permite buscar usando una fecha completa o una parte de ella.
     *
     * @param fecha fecha que se desea consultar
     * @return atenciones encontradas
     */
    public String buscarPorFecha(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return "Debe indicar una fecha.";
        }

        StringBuilder resultado = new StringBuilder();

        NodoAtencion actual = primerAtencion;

        int encontrados = 0;

        while (actual != null) {

            Atencion atencion = actual.getDato();

            Tiquete tiquete = atencion.getTiquete();

            if (tiquete != null) {

                String fechaIngreso =
                        tiquete.getFechaHoraIngreso();

                String fechaAtencion =
                        tiquete.getFechaHoraAtencion();

                boolean coincideIngreso =
                        fechaIngreso != null
                        && fechaIngreso.toLowerCase().contains(
                                fecha.trim().toLowerCase());

                boolean coincideAtencion =
                        fechaAtencion != null
                        && fechaAtencion.toLowerCase().contains(
                                fecha.trim().toLowerCase());

                if (coincideIngreso || coincideAtencion) {

                    resultado.append(atencion.toString())
                            .append("\n\n");

                    encontrados++;
                }
            }

            actual = actual.getSiguiente();
        }

        if (encontrados == 0) {
            return "No se encontraron atenciones para esa fecha.";
        }

        return "ATENCIONES POR FECHA\n\n"
                + resultado.toString();
    }

    /**
     * Busca atenciones realizadas en una sala o consultorio.
     *
     * @param espacio nombre de la sala o consultorio
     * @return atenciones encontradas
     */
    public String buscarPorEspacio(String espacio) {

        if (espacio == null || espacio.trim().isEmpty()) {
            return "Debe indicar el nombre de la sala o consultorio.";
        }

        StringBuilder resultado = new StringBuilder();

        NodoAtencion actual = primerAtencion;

        int encontrados = 0;

        while (actual != null) {

            Atencion atencion = actual.getDato();

            if (atencion.getEspacioAsignado()
                    .equalsIgnoreCase(espacio.trim())) {

                resultado.append(atencion.toString())
                        .append("\n\n");

                encontrados++;
            }

            actual = actual.getSiguiente();
        }

        if (encontrados == 0) {
            return "No se encontraron atenciones en ese espacio.";
        }

        return "ATENCIONES POR SALA O CONSULTORIO\n\n"
                + resultado.toString();
    }

    /**
     * Devuelve los pacientes ordenados por identificacion.
     */
    public String listarPacientesOrdenados() {

        if (indicePacientes.estaVacio()) {
            return "No hay pacientes registrados en el indice.";
        }

        return indicePacientes.listarEnOrden();
    }

    /**
     * Devuelve los pacientes mediante el recorrido inorden.
     */
    public String listarPacientesInorden() {

        return indicePacientes.listarEnOrden();
    }

    /**
     * Devuelve los pacientes mediante el recorrido preorden.
     */
    public String listarPacientesPreorden() {

        return indicePacientes.listarPreorden();
    }

    /**
     * Devuelve los pacientes mediante el recorrido postorden.
     */
    public String listarPacientesPostorden() {

        return indicePacientes.listarPostorden();
    }

    /**
     * Devuelve la cantidad de nodos del arbol.
     */
    public int contarNodos() {

        return indicePacientes.cuentaNodos();
    }

    /**
     * Devuelve la altura del arbol.
     */
    public int obtenerAltura() {

        return indicePacientes.getAltura();
    }

    /**
     * Muestra los pacientes almacenados en un nivel especifico.
     *
     * @param nivel nivel que se desea consultar
     * @return pacientes encontrados en el nivel
     */
    public String mostrarNivel(int nivel) {

        return indicePacientes.imprimeNivel(nivel);
    }

    /**
     * Comprueba si el indice cumple las reglas de un ABB.
     */
    public boolean validarArbolABB() {

        return indicePacientes.esArbolBinarioBusqueda();
    }

    /**
     * Devuelve la cantidad de pacientes indexados.
     */
    public int getCantidadPacientes() {

        return indicePacientes.getCantidad();
    }

    /**
     * Permite consultar el arbol utilizado por el gestor.
     */
    public ArbolABB getIndicePacientes() {

        return indicePacientes;
    }
}