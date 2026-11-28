/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package salas;

/**
 *
 * @author casti
 */
public class NodoConsultorio {

    private Consultorio consultorio;
    private NodoConsultorio siguiente;

    /**
     * Constructor del nodo.
     *
     * @param consultorio consultorio que almacenara el nodo
     */
    public NodoConsultorio(Consultorio consultorio) {

        if (consultorio == null) {
            throw new IllegalArgumentException(
                    "El consultorio no puede ser nulo.");
        }

        this.consultorio = consultorio;
        this.siguiente = null;
    }

    /**
     * Obtiene el consultorio almacenado.
     *
     * @return consultorio del nodo
     */
    public Consultorio getConsultorio() {
        return consultorio;
    }

    /**
     * Modifica el consultorio almacenado.
     *
     * @param consultorio nuevo consultorio
     */
    public void setConsultorio(Consultorio consultorio) {

        if (consultorio == null) {
            throw new IllegalArgumentException(
                    "El consultorio no puede ser nulo.");
        }

        this.consultorio = consultorio;
    }

    /**
     * Obtiene el siguiente nodo.
     *
     * @return siguiente nodo
     */
    public NodoConsultorio getSiguiente() {
        return siguiente;
    }

    /**
     * Establece el siguiente nodo.
     *
     * @param siguiente siguiente nodo de la lista
     */
    public void setSiguiente(NodoConsultorio siguiente) {
        this.siguiente = siguiente;
    }
}