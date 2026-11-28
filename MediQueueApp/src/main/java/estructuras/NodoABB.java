/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.Paciente;
/**
 *
 * @author casti
 */
public class NodoABB {

    private Paciente dato;
    private NodoABB izquierdo;
    private NodoABB derecho;

    /**
     * Constructor del nodo.
     */
    public NodoABB(Paciente dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
    }

    public Paciente getDato() {
        return dato;
    }

    public void setDato(Paciente dato) {
        this.dato = dato;
    }

    public NodoABB getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoABB izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoABB getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoABB derecho) {
        this.derecho = derecho;
    }
}