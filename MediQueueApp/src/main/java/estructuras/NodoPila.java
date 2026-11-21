/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.RegistroHistorial;
/**
 *
 * @author casti
 */
public class NodoPila {

    private RegistroHistorial dato;
    private NodoPila siguiente;

    public NodoPila(RegistroHistorial dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El registro no puede ser null.");
        }

        this.dato = dato;
    }

    public RegistroHistorial getDato() {
        return dato;
    }

    public void setDato(RegistroHistorial dato) {

        if (dato == null) {
            throw new IllegalArgumentException(
                    "El registro no puede ser null.");
        }

        this.dato = dato;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}