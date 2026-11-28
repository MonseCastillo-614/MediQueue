package estructuras;

import modelo.Sede;


public class NodoSede {

    private Sede dato;
    private NodoSede siguiente;

    private double distanciaTemporal;
    private boolean visitado;
    private Sede anterior;

    public NodoSede(Sede dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                    "La sede no puede ser null.");
        }

        this.dato = dato;
        this.siguiente = null;
        reiniciarDijkstra();
    }

    public void reiniciarDijkstra() {
        distanciaTemporal = Double.POSITIVE_INFINITY;
        visitado = false;
        anterior = null;
    }

    public Sede getDato() {
        return dato;
    }

    public NodoSede getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoSede siguiente) {
        this.siguiente = siguiente;
    }

    public double getDistanciaTemporal() {
        return distanciaTemporal;
    }

    public void setDistanciaTemporal(double distanciaTemporal) {
        this.distanciaTemporal = distanciaTemporal;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    public Sede getAnterior() {
        return anterior;
    }

    public void setAnterior(Sede anterior) {
        this.anterior = anterior;
    }
}
