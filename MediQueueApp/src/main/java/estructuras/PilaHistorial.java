/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.RegistroHistorial;
/**
 *
 * @author casti
 */
public class PilaHistorial {

    private NodoPila tope;
    private int cantidad;

    public PilaHistorial() {
        tope = null;
        cantidad = 0;
    }

    // Inserta un registro en la parte superior.
    public void apilar(RegistroHistorial registro) {

        NodoPila nuevo = new NodoPila(registro);

        nuevo.setSiguiente(tope);
        tope = nuevo;

        cantidad++;
    }

    // Retira y devuelve el registro superior.
    public RegistroHistorial desapilar() {

        if (estaVacia()) {
            return null;
        }

        RegistroHistorial registro = tope.getDato();

        tope = tope.getSiguiente();

        cantidad--;

        return registro;
    }

    // Consulta el registro superior sin retirarlo.
    public RegistroHistorial consultarTope() {

        if (estaVacia()) {
            return null;
        }

        return tope.getDato();
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    public NodoPila getTope() {
        return tope;
    }
}