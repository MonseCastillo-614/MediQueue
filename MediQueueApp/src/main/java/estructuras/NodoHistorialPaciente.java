/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.HistorialPaciente;
/**
 *
 * @author casti
 */
public class NodoHistorialPaciente {

    private HistorialPaciente dato;
    private NodoHistorialPaciente siguiente;

    public NodoHistorialPaciente(HistorialPaciente dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El historial no puede ser null.");
        }

        this.dato = dato;
    }

    public HistorialPaciente getDato() {
        return dato;
    }

    public void setDato(HistorialPaciente dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El historial no puede ser null.");
        }

        this.dato = dato;
    }

    public NodoHistorialPaciente getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoHistorialPaciente siguiente) {
        this.siguiente = siguiente;
    }
}