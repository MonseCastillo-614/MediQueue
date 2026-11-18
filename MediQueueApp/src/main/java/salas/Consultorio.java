/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package salas;
import estructuras.ColaTiquetes;
import modelo.Tiquete;
/**
 *
 * @author casti
 */
public class Consultorio {

    // Atributos del consultorio
    private int identificador;
    private int capacidad;
    private ColaTiquetes colaEspera;
    private String especialidad;
    private String medicoResponsable;
    private boolean preferencial;

    /**
     * Constructor del consultorio.
     *
     * @param identificador numero identificador del consultorio
     * @param capacidad capacidad maxima de espera
     * @param especialidad especialidad medica
     * @param preferencial indica si es el consultorio preferencial
     */
    public Consultorio(int identificador, int capacidad,
            String especialidad, boolean preferencial) {

        validarIdentificador(identificador);
        validarCapacidad(capacidad);
        validarEspecialidad(especialidad);

        this.identificador = identificador;
        this.capacidad = capacidad;
        this.especialidad = especialidad.trim();
        this.preferencial = preferencial;

        this.colaEspera = new ColaTiquetes();
        this.medicoResponsable = "";
    }

    /**
     * Valida el identificador del consultorio.
     */
    private void validarIdentificador(int identificador) {
        if (identificador < 1) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero.");
        }
    }

    /**
     * Valida la capacidad del consultorio.
     */
    private void validarCapacidad(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero.");
        }
    }

    /**
     * Valida la especialidad del consultorio.
     */
    private void validarEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La especialidad es obligatoria.");
        }
    }

    /**
     * Agrega un tiquete a la cola de espera.
     */
    public void agregarTiquete(Tiquete tiquete) {

        if (tiquete == null) {
            throw new IllegalArgumentException(
                    "El tiquete no puede ser nulo.");
        }

        if (!estaDisponible()) {
            throw new IllegalStateException(
                    "El consultorio ha alcanzado su capacidad.");
        }

        colaEspera.encolar(tiquete);
    }

    /**
     * Consulta la cantidad de pacientes en espera.
     */
    public int getCantidadEnEspera() {
        return colaEspera.tamano();
    }

    /**
     * Comprueba si hay espacio disponible en la cola.
     */
    public boolean estaDisponible() {
        return colaEspera.tamano() < capacidad;
    }

    /**
     * Comprueba si la cola esta vacia.
     */
    public boolean tieneColaVacia() {
        return colaEspera.estaVacia();
    }

    /**
     * Obtiene el identificador.
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Obtiene la capacidad.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Modifica la capacidad.
     */
    public void setCapacidad(int capacidad) {
        validarCapacidad(capacidad);

        if (capacidad < colaEspera.tamano()) {
            throw new IllegalArgumentException(
                    "La capacidad no puede ser menor "
                    + "que la cantidad de pacientes en espera.");
        }

        this.capacidad = capacidad;
    }

    /**
     * Obtiene la cola de espera.
     */
    public ColaTiquetes getColaEspera() {
        return colaEspera;
    }

    /**
     * Obtiene la especialidad.
     */
    public String getEspecialidad() {
        return especialidad;
    }

    /**
     * Modifica la especialidad.
     */
    public void setEspecialidad(String especialidad) {
        validarEspecialidad(especialidad);
        this.especialidad = especialidad.trim();
    }

    /**
     * Obtiene el medico responsable.
     */
    public String getMedicoResponsable() {
        return medicoResponsable;
    }

    /**
     * Asigna el medico responsable.
     */
    public void setMedicoResponsable(String medicoResponsable) {
        if (medicoResponsable == null) {
            throw new IllegalArgumentException(
                    "El medico responsable no puede ser nulo.");
        }

        this.medicoResponsable = medicoResponsable.trim();
    }

    /**
     * Indica si es preferencial.
     */
    public boolean esPreferencial() {
        return preferencial;
    }

    @Override
    public String toString() {
        return "Consultorio " + identificador
                + " | Especialidad: " + especialidad
                + " | Capacidad: " + capacidad
                + " | Pacientes en espera: " + colaEspera.tamano()
                + " | Medico: " + medicoResponsable
                + " | Preferencial: " + preferencial;
    }
}