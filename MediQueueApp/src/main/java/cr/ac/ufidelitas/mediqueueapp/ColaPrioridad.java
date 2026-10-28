/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ufidelitas.mediqueueapp;
import java.util.NoSuchElementException;
/**
 * 
 * 
 * @author aedgell555
 */
public class ColaPrioridad {

    private static final int NIVELES = 4;

    private final ColaTiquetes[] colas = new ColaTiquetes[NIVELES * 2];
    private int tamano;

    public ColaPrioridad() {
        for (int i = 0; i < colas.length; i++) {
            colas[i] = new ColaTiquetes();
        }
        this.tamano = 0;
    }

    private int indiceDe(Tiquete tiquete) {
        int nivel = tiquete.obtenerNivelPrioridad();
        return (nivel - 1) * 2 + (tiquete.esPreferencial() ? 0 : 1);
    }

    public void encolar(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException("El tiquete no puede ser nulo.");
        }

        colas[indiceDe(tiquete)].encolar(tiquete);
        tamano++;
    }

    public Tiquete desencolar() {
        for (ColaTiquetes cola : colas) {
            if (!cola.estaVacia()) {
                tamano--;
                return cola.desencolar();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia.");
    }

    public Tiquete frente() {
        for (ColaTiquetes cola : colas) {
            if (!cola.estaVacia()) {
                return cola.frente();
            }
        }
        throw new NoSuchElementException("La cola de prioridad esta vacia.");
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    // Devuelve los tiquetes en el orden de atencion sin modificar la cola.
    public Tiquete obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException(
                    "Posicion fuera de la cola: " + posicion);
        }

        int restante = posicion;
        for (ColaTiquetes cola : colas) {
            if (restante < cola.tamano()) {
                return cola.obtener(restante);
            }
            restante -= cola.tamano();
        }

        throw new IndexOutOfBoundsException(
                "Posicion fuera de la cola: " + posicion);
    }
}
