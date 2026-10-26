
package cr.ac.ufidelitas.mediqueueapp.persistencia;


public class JsonValor {

    public static final int OBJETO = 1;
    public static final int ARREGLO = 2;
    public static final int CADENA = 3;
    public static final int NUMERO = 4;
    public static final int BOOLEANO = 5;
    public static final int NULO = 6;

    private final int tipo;
    private final String valor;
    private String clave;

    private JsonValor primerHijo;
    private JsonValor ultimoHijo;
    private JsonValor siguiente;

    public JsonValor(int tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    
    public void agregarHijo(String nombre, JsonValor hijo) {
        hijo.clave = nombre;

        if (primerHijo == null) {
            primerHijo = hijo;
        } else {
            ultimoHijo.siguiente = hijo;
        }

        ultimoHijo = hijo;
    }

    
    public JsonValor campo(String nombre) {
        if (tipo != OBJETO) {
            throw new IllegalArgumentException(
                "Se esperaba objeto JSON."
            );
        }

        JsonValor actual = primerHijo;

        while (actual != null) {
            if (nombre.equals(actual.clave)) {
                return actual;
            }
            actual = actual.siguiente;
        }

        throw new IllegalArgumentException(
            "Falta el campo: " + nombre
        );
    }

    public String comoTexto() {
        if (tipo != CADENA) {
            throw new IllegalArgumentException(
                "Se esperaba texto JSON."
            );
        }
        return valor;
    }

    public int comoEntero() {
        if (tipo != NUMERO) {
            throw new IllegalArgumentException(
                "Se esperaba numero JSON."
            );
        }
        return Integer.parseInt(valor);
    }

    public void exigirTipo(int esperado) {
        if (tipo != esperado) {
            throw new IllegalArgumentException(
                "Tipo JSON invalido."
            );
        }
    }

    public int getTipo() {
        return tipo;
    }

    public JsonValor getPrimerHijo() {
        return primerHijo;
    }

    public JsonValor getSiguiente() {
        return siguiente;
    }
}
