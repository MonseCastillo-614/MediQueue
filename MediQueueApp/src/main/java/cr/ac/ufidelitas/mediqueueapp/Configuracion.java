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

    private String nombreSede;
    private int cantidadSalas;
    private int cantidadConsultorios;
    private int cantidadEmergencias;

    private int salaPreferencial;
    private int consultorioPreferencial;
    private int salaEmergenciaCritica;

    public Configuracion(String nombreSede, int cantidadSalas,
            int cantidadConsultorios, int cantidadEmergencias) {

        validarNombreSede(nombreSede);
        validarCantidad(cantidadSalas);
        validarCantidad(cantidadConsultorios);
        validarCantidad(cantidadEmergencias);

        this.nombreSede = nombreSede.trim();
        this.cantidadSalas = cantidadSalas;
        this.cantidadConsultorios = cantidadConsultorios;
        this.cantidadEmergencias = cantidadEmergencias;

        this.salaPreferencial = 1;
        this.consultorioPreferencial = 1;
        this.salaEmergenciaCritica = 1;
    }

    private void validarNombreSede(String nombreSede) {
        if (nombreSede == null || nombreSede.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la sede es obligatorio.");
        }
    }

    private void validarCantidad(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }
    }

    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        validarNombreSede(nombreSede);
        this.nombreSede = nombreSede.trim();
    }

    public int getCantidadSalas() {
        return cantidadSalas;
    }

    public void setCantidadSalas(int cantidadSalas) {
        validarCantidad(cantidadSalas);

        if (salaPreferencial > cantidadSalas) {
            salaPreferencial = 1;
        }

        this.cantidadSalas = cantidadSalas;
    }

    public int getCantidadConsultorios() {
        return cantidadConsultorios;
    }

    public void setCantidadConsultorios(int cantidadConsultorios) {
        validarCantidad(cantidadConsultorios);

        if (consultorioPreferencial > cantidadConsultorios) {
            consultorioPreferencial = 1;
        }

        this.cantidadConsultorios = cantidadConsultorios;
    }

    public int getCantidadEmergencias() {
        return cantidadEmergencias;
    }

    public void setCantidadEmergencias(int cantidadEmergencias) {
        validarCantidad(cantidadEmergencias);

        if (salaEmergenciaCritica > cantidadEmergencias) {
            salaEmergenciaCritica = 1;
        }

        this.cantidadEmergencias = cantidadEmergencias;
    }

    public int getSalaPreferencial() {
        return salaPreferencial;
    }

    public void setSalaPreferencial(int salaPreferencial) {
        validarIdentificador(salaPreferencial, cantidadSalas);
        this.salaPreferencial = salaPreferencial;
    }

    public int getConsultorioPreferencial() {
        return consultorioPreferencial;
    }

    public void setConsultorioPreferencial(int consultorioPreferencial) {
        validarIdentificador(
                consultorioPreferencial, cantidadConsultorios);

        this.consultorioPreferencial = consultorioPreferencial;
    }

    public int getSalaEmergenciaCritica() {
        return salaEmergenciaCritica;
    }

    public void setSalaEmergenciaCritica(int salaEmergenciaCritica) {
        validarIdentificador(
                salaEmergenciaCritica, cantidadEmergencias);

        this.salaEmergenciaCritica = salaEmergenciaCritica;
    }

    private void validarIdentificador(int identificador, int cantidad) {
        if (identificador < 1 || identificador > cantidad) {
            throw new IllegalArgumentException(
                    "El identificador no corresponde a un espacio existente.");
        }
    }
}