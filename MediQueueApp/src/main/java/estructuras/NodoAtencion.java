/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.Atencion;
/**
 *
 * @author casti
 */
public class NodoAtencion {

    private Atencion dato;
    private NodoAtencion siguiente;

    public NodoAtencion(Atencion dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "La atención no puede ser null.");
        }

        this.dato = dato;
    }

    public Atencion getDato() {
        return dato;
    }

    public void setDato(Atencion dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "La atención no puede ser null.");
        }

        this.dato = dato;
    }

    public NodoAtencion getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoAtencion siguiente) {
        this.siguiente = siguiente;
    }
}