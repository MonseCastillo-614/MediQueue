package cr.ac.ufidelitas.mediqueapp.pacientes.tiquetes;

/**
 * 
 * 
 * @author aedgell555
 */
public class NodoTiquete {

    private final Tiquete tiquete;
    private NodoTiquete siguiente;

    public NodoTiquete(Tiquete tiquete) {
        this.tiquete = tiquete;
        this.siguiente = null;
    }

    public Tiquete getTiquete() {
        return tiquete;
    }

    public NodoTiquete getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoTiquete siguiente) {
        this.siguiente = siguiente;
    }
}
