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

    // Nombre de la sede
    private String nombreSede;

    // Cantidad de salas
    private int cantidadSalas;

    // Cantidad de consultorios
    private int cantidadConsultorios;

    // Cantidad de salas de emergencia
    private int cantidadEmergencias;

    // Constructor
    public Configuracion(String nombreSede, int cantidadSalas,
            int cantidadConsultorios, int cantidadEmergencias) {
        this.nombreSede = nombreSede;
        this.cantidadSalas = cantidadSalas;
        this.cantidadConsultorios = cantidadConsultorios;
        this.cantidadEmergencias = cantidadEmergencias;
    }
    
    public String getNombreSede() {
        return nombreSede;
    }

    public void setNombreSede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public int getCantidadSalas() {
        return cantidadSalas;
    }

    public void setCantidadSalas(int cantidadSalas) {
        this.cantidadSalas = cantidadSalas;
    }

    public int getCantidadConsultorios() {
        return cantidadConsultorios;
    }

    public void setCantidadConsultorios(int cantidadConsultorios) {
        this.cantidadConsultorios = cantidadConsultorios;
    }

    public int getCantidadEmergencias() {
        return cantidadEmergencias;
    }

    public void setCantidadEmergencias(int cantidadEmergencias) {
        this.cantidadEmergencias = cantidadEmergencias;
    }

}
