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
public class Sala {

    // Tipos de sala
    public static final String TIPO_NORMAL = "Normal";
    public static final String TIPO_PREFERENCIAL = "Preferencial";
    public static final String TIPO_EMERGENCIA = "Emergencia";

    // Atributos de la sala
    private int identificador;
    private int capacidad;
    private ColaTiquetes colaEspera;
    private String medicoResponsable;
    private String enfermeraCargo;
    private String tipo;

    /**
     * Constructor de la sala.
     *
     * @param identificador numero identificador de la sala
     * @param capacidad capacidad maxima de espera
     * @param tipo tipo de sala
     */
    public Sala(int identificador, int capacidad, String tipo) {

        validarIdentificador(identificador);
        validarCapacidad(capacidad);
        validarTipo(tipo);

        this.identificador = identificador;
        this.capacidad = capacidad;
        this.tipo = normalizarTipo(tipo);

        this.colaEspera = new ColaTiquetes();
        this.medicoResponsable = "";
        this.enfermeraCargo = "";
    }

    /**
     * Valida el identificador de la sala.
     */
    private void validarIdentificador(int identificador) {
        if (identificador < 1) {
            throw new IllegalArgumentException(
                    "El identificador debe ser mayor que cero.");
        }
    }

    /**
     * Valida la capacidad de la sala.
     */
    private void validarCapacidad(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor que cero.");
        }
    }

    /**
     * Valida el tipo de sala.
     */
    private void validarTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El tipo de sala es obligatorio.");
        }

        if (!tipo.equalsIgnoreCase(TIPO_NORMAL)
                && !tipo.equalsIgnoreCase(TIPO_PREFERENCIAL)
                && !tipo.equalsIgnoreCase(TIPO_EMERGENCIA)) {

            throw new IllegalArgumentException(
                    "Tipo de sala invalido.");
        }
    }

    /**
     * Normaliza el tipo de sala.
     */
    private String normalizarTipo(String tipo) {
        if (tipo.equalsIgnoreCase(TIPO_NORMAL)) {
            return TIPO_NORMAL;
        }

        if (tipo.equalsIgnoreCase(TIPO_PREFERENCIAL)) {
            return TIPO_PREFERENCIAL;
        }

        return TIPO_EMERGENCIA;
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
                    "La sala ha alcanzado su capacidad.");
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
     * Comprueba si la sala tiene espacio disponible
     * en su cola de espera.
     */
    public boolean estaDisponible() {
        return colaEspera.tamano() < capacidad;
    }

    /**
     * Comprueba si la cola de espera esta vacia.
     */
    public boolean tieneColaVacia() {
        return colaEspera.estaVacia();
    }

    /**
     * Obtiene el identificador de la sala.
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Obtiene la capacidad de la sala.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Modifica la capacidad de la sala.
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
     * Obtiene la enfermera a cargo.
     */
    public String getEnfermeraCargo() {
        return enfermeraCargo;
    }

    /**
     * Asigna la enfermera a cargo.
     */
    public void setEnfermeraCargo(String enfermeraCargo) {
        if (enfermeraCargo == null) {
            throw new IllegalArgumentException(
                    "La enfermera no puede ser nula.");
        }

        this.enfermeraCargo = enfermeraCargo.trim();
    }

    /**
     * Obtiene el tipo de sala.
     */
    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "Sala " + identificador
                + " | Tipo: " + tipo
                + " | Capacidad: " + capacidad
                + " | Pacientes en espera: " + colaEspera.tamano()
                + " | Medico: " + medicoResponsable
                + " | Enfermera: " + enfermeraCargo;
    }
}