package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * 
 * @author aedgell555
 */
/**
 * Cola de prioridad dinamica para tiquetes.*/
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

    // Tiquetes pendientes en el orden exacto en que seran atendidos
    public List<Tiquete> aLista() {
        List<Tiquete> lista = new ArrayList<>();
        for (ColaTiquetes cola : colas) {
            lista.addAll(cola.aLista());
        }
        return lista;
    }
}
