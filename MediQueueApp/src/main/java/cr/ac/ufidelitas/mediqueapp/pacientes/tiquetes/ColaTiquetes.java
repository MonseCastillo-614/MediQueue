package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author aedgell555
 */
public class ColaTiquetes {

    private NodoTiquete frente; // primer nodo
    private NodoTiquete fin;    // ultimo nodo
    private int tamano;

    public ColaTiquetes() {
        this.frente = null;
        this.fin = null;
        this.tamano = 0;
    }

    // Agrega un tiquete al final de la cola
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

    // Elimina y devuelve el tiquete del frente
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

    // Devuelve el tiquete del frente sin eliminarlo
    public Tiquete frente() {
        if (frente == null) {
            throw new NoSuchElementException("La cola esta vacia.");
        }
        return frente.getTiquete();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamano() {
        return tamano;
    }

    // Copia el contenido en orden de salida (no modifica la cola)
    public List<Tiquete> aLista() {
        List<Tiquete> lista = new ArrayList<>();
        NodoTiquete actual = frente;

        while (actual != null) {
            lista.add(actual.getTiquete());
            actual = actual.getSiguiente();
        }

        return lista;
    }
}
