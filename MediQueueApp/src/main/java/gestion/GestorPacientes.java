/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion;
import java.io.IOException;
import modelo.Paciente;
import persistencia.AdaptadorJson;
import persistencia.PersistenciaDatos;
/**
 *
 * @author casti
 */
public class GestorPacientes {

    private Paciente primerPaciente;
    private Paciente ultimoPaciente;
    private final PersistenciaDatos persistencia;
    private final AdaptadorJson<Paciente> adaptador;

    public GestorPacientes(PersistenciaDatos persistencia,
            AdaptadorJson<Paciente> adaptador) {
        if (persistencia == null || adaptador == null) {
            throw new IllegalArgumentException(
                    "La persistencia y el adaptador son obligatorios.");
        }
        this.persistencia = persistencia;
        this.adaptador = adaptador;
    }

    public void cargarPacientes() throws IOException {
        primerPaciente = persistencia.cargarPacientes(adaptador);
        ultimoPaciente = obtenerUltimo(primerPaciente);
    }

    public void guardarPacientes() throws IOException {
        persistencia.guardarPacientes(primerPaciente, adaptador);
    }

    public void registrarPaciente(Paciente paciente) throws IOException {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo.");
        }
        if (buscarPorIdentificacion(paciente.getIdentificacion()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un paciente con esa identificacion.");
        }

        paciente.setSiguientePaciente(null);
        if (primerPaciente == null) {
            primerPaciente = paciente;
        } else {
            ultimoPaciente.setSiguientePaciente(paciente);
        }
        ultimoPaciente = paciente;

        try {
            guardarPacientes();
        } catch (IOException | RuntimeException ex) {
            quitarUltimoPaciente();
            throw ex;
        }
    }

    public Paciente buscarPorIdentificacion(String identificacion) {
        if (identificacion == null || identificacion.trim().isEmpty()) {
            return null;
        }
        Paciente actual = primerPaciente;
        while (actual != null) {
            if (actual.getIdentificacion().equalsIgnoreCase(identificacion.trim())) {
                return actual;
            }
            actual = actual.getSiguientePaciente();
        }
        return null;
    }

    public Paciente buscarPorIndice(int indice) {
        if (indice < 0) return null;
        Paciente actual = primerPaciente;
        int posicion = 0;
        while (actual != null) {
            if (posicion == indice) return actual;
            posicion++;
            actual = actual.getSiguientePaciente();
        }
        return null;
    }

    public Paciente getPrimerPaciente() {
        return primerPaciente;
    }

    public boolean hayPacientes() {
        return primerPaciente != null;
    }

    public int contarPacientes() {
        int cantidad = 0;
        Paciente actual = primerPaciente;
        while (actual != null) {
            cantidad++;
            actual = actual.getSiguientePaciente();
        }
        return cantidad;
    }

    public void limpiarPacientes() {
        primerPaciente = null;
        ultimoPaciente = null;
    }

    private void quitarUltimoPaciente() {
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

    private Paciente obtenerUltimo(Paciente primero) {
        Paciente actual = primero;
        if (actual == null) return null;
        while (actual.getSiguientePaciente() != null) {
            actual = actual.getSiguientePaciente();
        }
        return actual;
    }
}