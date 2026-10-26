/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ufidelitas.mediqueueapp;

/**
 *
 * @author casti
 */
public class Configuracion {

    // Guarda el nombre de la sede hospitalaria
    private String nombreSede;

    // Almacena la cantidad de salas medicas
    private int cantidadSalas;

    // Indica cuantos consultorios tiene la sede
    private int cantidadConsultorios;

    // Registra la cantidad de salas de emergencia
    private int cantidadEmergencias;

    // Identifica la sala medica preferencial
    private int salaPreferencial;

    // Identifica el consultorio preferencial
    private int consultorioPreferencial;

    // Identifica la sala de emergencia critica
    private int salaEmergenciaCritica;

    // Crea la configuracion inicial de la sede
    public Configuracion(String nombreSede, int cantidadSalas,
            int cantidadConsultorios, int cantidadEmergencias) {

        if (nombreSede == null || nombreSede.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la sede es obligatorio.");
        }

        validarCantidad(cantidadSalas);
        validarCantidad(cantidadConsultorios);
        validarCantidad(cantidadEmergencias);

        this.nombreSede = nombreSede.trim();
        this.cantidadSalas = cantidadSalas;
        this.cantidadConsultorios = cantidadConsultorios;
        this.cantidadEmergencias = cantidadEmergencias;

        // Asigna inicialmente el primer espacio de cada tipo
        this.salaPreferencial = 1;
        this.consultorioPreferencial = 1;
        this.salaEmergenciaCritica = 1;
    }

    // Comprueba que la cantidad este entre 1 y 10
    private void validarCantidad(int cantidad) {
        if (cantidad < 1 || cantidad > 10) {
            throw new IllegalArgumentException(
                    "La cantidad debe estar entre 1 y 10.");
        }
    }

    // Devuelve el nombre de la sede
    public String getNombreSede() {
        return nombreSede;
    }

    // Permite cambiar el nombre de la sede
    public void setNombreSede(String nombreSede) {
        if (nombreSede == null || nombreSede.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la sede es obligatorio.");
        }

        this.nombreSede = nombreSede.trim();
    }

    // Retorna la cantidad de salas medicas
    public int getCantidadSalas() {
        return cantidadSalas;
    }

    // Actualiza la cantidad de salas medicas
    public void setCantidadSalas(int cantidadSalas) {
        validarCantidad(cantidadSalas);

        if (salaPreferencial > cantidadSalas) {
            salaPreferencial = 1;
        }

        this.cantidadSalas = cantidadSalas;
    }

    // Retorna la cantidad de consultorios
    public int getCantidadConsultorios() {
        return cantidadConsultorios;
    }

    // Actualiza la cantidad de consultorios
    public void setCantidadConsultorios(int cantidadConsultorios) {
        validarCantidad(cantidadConsultorios);

        if (consultorioPreferencial > cantidadConsultorios) {
            consultorioPreferencial = 1;
        }

        this.cantidadConsultorios = cantidadConsultorios;
    }

    // Retorna la cantidad de salas de emergencia
    public int getCantidadEmergencias() {
        return cantidadEmergencias;
    }

    // Actualiza la cantidad de salas de emergencia
    public void setCantidadEmergencias(int cantidadEmergencias) {
        validarCantidad(cantidadEmergencias);

        if (salaEmergenciaCritica > cantidadEmergencias) {
            salaEmergenciaCritica = 1;
        }

        this.cantidadEmergencias = cantidadEmergencias;
    }

    // Devuelve el numero de la sala preferencial
    public int getSalaPreferencial() {
        return salaPreferencial;
    }

    // Cambia la sala preferencial
    public void setSalaPreferencial(int salaPreferencial) {
        validarIdentificador(salaPreferencial, cantidadSalas);
        this.salaPreferencial = salaPreferencial;
    }

    // Devuelve el numero del consultorio preferencial
    public int getConsultorioPreferencial() {
        return consultorioPreferencial;
    }

    // Cambia el consultorio preferencial
    public void setConsultorioPreferencial(int consultorioPreferencial) {
        validarIdentificador(
                consultorioPreferencial, cantidadConsultorios);

        this.consultorioPreferencial = consultorioPreferencial;
    }

    // Devuelve el numero de la sala de emergencia critica
    public int getSalaEmergenciaCritica() {
        return salaEmergenciaCritica;
    }

    // Cambia la sala de emergencia critica
    public void setSalaEmergenciaCritica(int salaEmergenciaCritica) {
        validarIdentificador(
                salaEmergenciaCritica, cantidadEmergencias);

        this.salaEmergenciaCritica = salaEmergenciaCritica;
    }

    // Comprueba que el identificador corresponda a un espacio existente
    private void validarIdentificador(int identificador, int cantidad) {
        if (identificador < 1 || identificador > cantidad) {
            throw new IllegalArgumentException(
                    "El identificador no corresponde a un espacio existente.");
        }
    }
}