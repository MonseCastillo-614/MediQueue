package estructuras;

import modelo.Tiquete;
import java.util.NoSuchElementException;

public class ColaPrioridad {

    private NodoTiquete frente; // primer nodo
    private NodoTiquete fin;    // ultimo nodo
    private int tamano;

    public ColaPrioridad() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    // Calcula el valor de prioridad manteniendo tu lógica original: 
    private int obtenerValorPrioridad(Tiquete tiquete) {
        int nivel = tiquete.obtenerNivelPrioridad();
        return (nivel - 1) * 2 + (tiquete.esPreferencial() ? 0 : 1);
    }

    public void encolar(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException("El tiquete no puede ser nulo.");
        }

        NodoTiquete nuevo = new NodoTiquete(tiquete);
        int prioridadNuevo = obtenerValorPrioridad(tiquete);

        
        if (frente == null) {
            frente = nuevo;
            fin = nuevo;
        } 
        
        else if (obtenerValorPrioridad(frente.getTiquete()) > prioridadNuevo) {
            nuevo.setSiguiente(frente);
            frente = nuevo;
        } 
        
        else {
            NodoTiquete actual = frente;
            
            
            while (actual.getSiguiente() != null && 
                   obtenerValorPrioridad(actual.getSiguiente().getTiquete()) <= prioridadNuevo) {
                actual = actual.getSiguiente();
            }
            
            // Insertamos el nuevo nodo justo después del nodo 
            nuevo.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevo);
            
            // Si el nodo se insertó en la última posición, actualizamos el puntero fin
            if (nuevo.getSiguiente() == null) {
                fin = nuevo;
            }
        }
        tamano++;
    }

    public Tiquete desencolar() {
        if (frente == null) {
            throw new NoSuchElementException("La cola de prioridad esta vacia.");
        }

        Tiquete tiquete = frente.getTiquete();
        frente = frente.getSiguiente();

        if (frente == null) {
            fin = null; // Si la cola quedó vacía, el final también es nulo
        }

        tamano--;
        return tiquete;
    }

    public Tiquete frente() {
        if (frente == null) {
            throw new NoSuchElementException("La cola de prioridad esta vacia.");
        }
        return frente.getTiquete();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamano() {
        return tamano;
    }

    public Tiquete obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException("Posicion fuera de la cola: " + posicion);
        }

        NodoTiquete actual = frente;
        int indice = 0;

        while (indice < posicion) {
            actual = actual.getSiguiente();
            indice++;
        }

        return actual.getTiquete();
    }
}