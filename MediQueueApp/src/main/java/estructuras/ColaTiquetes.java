/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.Tiquete;
import java.util.NoSuchElementException;
/**
 * 
 * 
 * @author aedgell555
 */
/*
 * Cola enlazada de tiquetes de atencion.
 */
public class ColaTiquetes {

    private NodoTiquete frente;
    private NodoTiquete fin;
    private int tamano;

    public ColaTiquetes() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    public void encolar(Tiquete tiquete) {
        if (tiquete == null) {
            throw new IllegalArgumentException("El tiquete no puede ser nulo.");
        }

        NodoTiquete nuevo = new NodoTiquete(tiquete);
        if (fin != null) {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        if (frente == null) {
            frente = nuevo;
        }
        tamano++;
    }

    public Tiquete desencolar() {
        if (frente == null) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        Tiquete tiquete = frente.getTiquete();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return tiquete;
    }

    public Tiquete frente() {
        if (frente == null) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        return frente.getTiquete();
    }

    /** Elimina un tiquete por ID sin alterar el orden de los demas. */
    public boolean removerPorId(String id) {
        if (id == null || id.trim().isEmpty() || frente == null) {
            return false;
        }

        NodoTiquete anterior = null;
        NodoTiquete actual = frente;

        while (actual != null) {
            Tiquete tiquete = actual.getTiquete();
            if (tiquete.getId().equalsIgnoreCase(id)) {
                if (anterior == null) {
                    frente = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }

                if (actual == fin) {
                    fin = anterior;
                }

                tamano--;
                if (tamano == 0) {
                    frente = null;
                    fin = null;
                }
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
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