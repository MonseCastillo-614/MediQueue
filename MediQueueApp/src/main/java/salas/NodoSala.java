/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package salas;

/**
 *
 * @author casti
 */
public class NodoSala {

    private Sala sala;
    private NodoSala siguiente;

    /**
     * Constructor del nodo.
     *
     * @param sala sala que almacenara el nodo
     */
    public NodoSala(Sala sala) {

        if (sala == null) {
            throw new IllegalArgumentException(
                    "La sala no puede ser nula.");
        }

        this.sala = sala;
        this.siguiente = null;
    }

    /**
     * Obtiene la sala almacenada.
     *
     * @return sala del nodo
     */
    public Sala getSala() {
        return sala;
    }

    /**
     * Modifica la sala almacenada.
     *
     * @param sala nueva sala
     */
    public void setSala(Sala sala) {

        if (sala == null) {
            throw new IllegalArgumentException(
                    "La sala no puede ser nula.");
        }

        this.sala = sala;
    }

    /**
     * Obtiene el siguiente nodo.
     *
     * @return siguiente nodo
     */
    public NodoSala getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el siguiente nodo.
     *
     * @param siguiente siguiente nodo de la lista
     */
    public void setSiguiente(NodoSala siguiente) {
        this.siguiente = siguiente;
    }
}