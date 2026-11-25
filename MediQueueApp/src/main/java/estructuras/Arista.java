package estructuras;

import modelo.Sede;

public class Arista {

    private Sede destino;
    private double distancia;
    private double tiempo;
    private double costo;
    private Arista siguiente;

    public Arista(Sede destino, double distancia,
            double tiempo, double costo) {

        if (destino == null) {
            throw new IllegalArgumentException(
                    "La sede destino no puede ser null.");
        }

        validarPeso(distancia, "La distancia");
        validarPeso(tiempo, "El tiempo");
        validarPeso(costo, "El costo");

        this.destino = destino;
        this.distancia = distancia;
        this.tiempo = tiempo;
        this.costo = costo;
        this.siguiente = null;
    }

    private void validarPeso(double peso, String nombre) {
        if (Double.isNaN(peso) || Double.isInfinite(peso) || peso <= 0) {
            throw new IllegalArgumentException(
                    nombre + " debe ser un número positivo.");
        }
    }

    public Sede getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getTiempo() {
        return tiempo;
    }

    public double getCosto() {
        return costo;
    }

    public Arista getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Arista siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return destino.getNombre()
                + " [Distancia: " + distancia
                + ", Tiempo: " + tiempo
                + ", Costo: " + costo + "]";
    }
}
