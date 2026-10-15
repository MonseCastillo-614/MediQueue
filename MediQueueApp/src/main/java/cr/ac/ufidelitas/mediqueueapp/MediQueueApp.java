/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package cr.ac.ufidelitas.mediqueueapp;

/**
 *
 * @author casti
 */
public class MediQueueApp {

    public static void main(String[] args) {

        // Crear la configuracion de la sede
        Configuracion configuracion = new Configuracion(
                "Sede Heredia", 5, 4, 1);

        // Mostrar los datos de la configuracion
        System.out.println("Nombre de la sede: "
                + configuracion.getNombreSede());

        System.out.println("Cantidad de salas: "
                + configuracion.getCantidadSalas());

        System.out.println("Cantidad de consultorios: "
                + configuracion.getCantidadConsultorios());

        System.out.println("Cantidad de emergencias: "
                + configuracion.getCantidadEmergencias());
    }
}
