/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import estructuras.PilaHistorial;

/**
 *
 * @author casti
 */
public class HistorialPaciente {

    private String identificacion;
    private PilaHistorial registros;

    public HistorialPaciente() {
        registros = new PilaHistorial();
    }

    public HistorialPaciente(String identificacion) {
        setIdentificacion(identificacion);
        registros = new PilaHistorial();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {

        if (identificacion == null || identificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La identificacion es obligatoria.");
        }

        this.identificacion = identificacion.trim();
    }

    public PilaHistorial getRegistros() {
        return registros;
    }

    public void setRegistros(PilaHistorial registros) {

        if (registros == null) {
            throw new IllegalArgumentException(
                    "La pila de historial es obligatoria.");
        }

        this.registros = registros;
    }

    // Agrega un registro al historial del paciente.
    public void agregarRegistro(RegistroHistorial registro) {
        registros.apilar(registro);
    }

    @Override
    public String toString() {
        return "Historial del paciente: " + identificacion
                + " | Registros: " + registros.getCantidad();
    }
}