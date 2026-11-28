/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;
import modelo.Paciente;
/**
 *
 * @author casti
 */
public class ArbolABB {

    private NodoABB raiz;
    private int cantidad;

    /**
     * Constructor del arbol.
     */
    public ArbolABB() {
        raiz = null;
        cantidad = 0;
    }

    /**
     * Inserta un paciente en el arbol.
     *
     * @param paciente paciente que se desea insertar
     * @return true si se inserta correctamente
     */
    public boolean insertar(Paciente paciente) {

        if (paciente == null) {
            return false;
        }

        if (buscar(paciente.getIdentificacion()) != null) {
            return false;
        }

        raiz = insertarRecursivo(raiz, paciente);
        cantidad++;

        return true;
    }

    /**
     * Insercion recursiva segun la identificacion.
     */
    private NodoABB insertarRecursivo(
            NodoABB actual, Paciente paciente) {

        if (actual == null) {
            return new NodoABB(paciente);
        }

        int comparacion = paciente.getIdentificacion()
                .compareToIgnoreCase(
                        actual.getDato().getIdentificacion());

        if (comparacion < 0) {
            actual.setIzquierdo(
                    insertarRecursivo(
                            actual.getIzquierdo(), paciente));
        } else if (comparacion > 0) {
            actual.setDerecho(
                    insertarRecursivo(
                            actual.getDerecho(), paciente));
        }

        return actual;
    }

    /**
     * Busca un paciente mediante su identificacion.
     *
     * @param identificacion identificacion del paciente
     * @return paciente encontrado o null si no existe
     */
    public Paciente buscar(String identificacion) {

        if (identificacion == null
                || identificacion.trim().isEmpty()) {
            return null;
        }

        return buscarRecursivo(raiz, identificacion.trim());
    }

    /**
     * Busqueda recursiva dentro del arbol.
     */
    private Paciente buscarRecursivo(
            NodoABB actual, String identificacion) {

        if (actual == null) {
            return null;
        }

        int comparacion = identificacion.compareToIgnoreCase(
                actual.getDato().getIdentificacion());

        if (comparacion == 0) {
            return actual.getDato();
        }

        if (comparacion < 0) {
            return buscarRecursivo(
                    actual.getIzquierdo(), identificacion);
        }

        return buscarRecursivo(
                actual.getDerecho(), identificacion);
    }

    /**
     * Recorrido inorden: izquierdo, raiz y derecho.
     * Devuelve los pacientes ordenados por identificacion.
     */
    public String listarEnOrden() {

        if (raiz == null) {
            return "El arbol esta vacio.";
        }

        StringBuilder resultado = new StringBuilder();

        inordenRecursivo(raiz, resultado);

        return resultado.toString();
    }

    /**
     * Metodo recursivo del recorrido inorden.
     */
    private void inordenRecursivo(
            NodoABB actual, StringBuilder resultado) {

        if (actual != null) {

            inordenRecursivo(
                    actual.getIzquierdo(), resultado);

            resultado.append(actual.getDato().toString())
                    .append("\n");

            inordenRecursivo(
                    actual.getDerecho(), resultado);
        }
    }

    /**
     * Recorrido preorden: raiz, izquierdo y derecho.
     */
    public String listarPreorden() {

        if (raiz == null) {
            return "El arbol esta vacio.";
        }

        StringBuilder resultado = new StringBuilder();

        preordenRecursivo(raiz, resultado);

        return resultado.toString();
    }

    /**
     * Metodo recursivo del recorrido preorden.
     */
    private void preordenRecursivo(
            NodoABB actual, StringBuilder resultado) {

        if (actual != null) {

            resultado.append(actual.getDato().toString())
                    .append("\n");

            preordenRecursivo(
                    actual.getIzquierdo(), resultado);

            preordenRecursivo(
                    actual.getDerecho(), resultado);
        }
    }

    /**
     * Recorrido postorden: izquierdo, derecho y raiz.
     */
    public String listarPostorden() {

        if (raiz == null) {
            return "El arbol esta vacio.";
        }

        StringBuilder resultado = new StringBuilder();

        postordenRecursivo(raiz, resultado);

        return resultado.toString();
    }

    /**
     * Metodo recursivo del recorrido postorden.
     */
    private void postordenRecursivo(
            NodoABB actual, StringBuilder resultado) {

        if (actual != null) {

            postordenRecursivo(
                    actual.getIzquierdo(), resultado);

            postordenRecursivo(
                    actual.getDerecho(), resultado);

            resultado.append(actual.getDato().toString())
                    .append("\n");
        }
    }

    /**
     * Cuenta los nodos del arbol de manera recursiva.
     *
     * @return cantidad total de nodos
     */
    public int cuentaNodos() {
        return cuentaNodosRecursivo(raiz);
    }

    /**
     * Metodo recursivo para contar los nodos.
     */
    private int cuentaNodosRecursivo(NodoABB actual) {

        if (actual == null) {
            return 0;
        }

        return 1
                + cuentaNodosRecursivo(actual.getIzquierdo())
                + cuentaNodosRecursivo(actual.getDerecho());
    }

    /**
     * Devuelve la altura del arbol.
     * Un arbol vacio tiene altura -1.
     */
    public int getAltura() {
        return calculaAltura(raiz);
    }

    /**
     * Calcula recursivamente la altura del arbol.
     */
    private int calculaAltura(NodoABB actual) {

        if (actual == null) {
            return -1;
        }

        int alturaIzquierda =
                calculaAltura(actual.getIzquierdo());

        int alturaDerecha =
                calculaAltura(actual.getDerecho());

        return mayor(alturaIzquierda, alturaDerecha) + 1;
    }

    /**
     * Compara dos alturas y devuelve la mayor.
     */
    private int mayor(int a, int b) {

        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Imprime los pacientes de un nivel especifico.
     *
     * @param nivel nivel que se desea consultar, empezando en 0
     * @return pacientes encontrados en ese nivel
     */
    public String imprimeNivel(int nivel) {

        if (raiz == null) {
            return "El arbol esta vacio.";
        }

        if (nivel < 0) {
            return "El nivel no puede ser negativo.";
        }

        StringBuilder resultado = new StringBuilder();

        resultado.append("Nivel ")
                .append(nivel)
                .append(":\n");

        imprimeNivelRecursivo(
                raiz, nivel, 0, resultado);

        if (resultado.toString().endsWith(":\n")) {
            resultado.append("No hay nodos en este nivel.");
        }

        return resultado.toString();
    }

    /**
     * Metodo recursivo para recorrer un nivel.
     */
    private void imprimeNivelRecursivo(
            NodoABB actual,
            int nivelObjetivo,
            int nivelActual,
            StringBuilder resultado) {

        if (actual != null) {

            if (nivelActual == nivelObjetivo) {

                resultado.append(actual.getDato().toString())
                        .append("\n");
            }

            imprimeNivelRecursivo(
                    actual.getIzquierdo(),
                    nivelObjetivo,
                    nivelActual + 1,
                    resultado);

            imprimeNivelRecursivo(
                    actual.getDerecho(),
                    nivelObjetivo,
                    nivelActual + 1,
                    resultado);
        }
    }

    /**
     * Comprueba si el arbol esta vacio.
     */
    public boolean estaVacio() {
        return raiz == null;
    }

    /**
     * Devuelve la cantidad de pacientes almacenados.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Verifica si la estructura cumple las reglas de un ABB.
     */
    public boolean esArbolBinarioBusqueda() {

        return esABBRecursivo(
                raiz, null, null);
    }

    /**
     * Verifica recursivamente los limites de identificacion.
     */
    private boolean esABBRecursivo(
            NodoABB actual,
            String minimo,
            String maximo) {

        if (actual == null) {
            return true;
        }

        String identificacion =
                actual.getDato().getIdentificacion();

        if (minimo != null
                && identificacion.compareToIgnoreCase(minimo) <= 0) {
            return false;
        }

        if (maximo != null
                && identificacion.compareToIgnoreCase(maximo) >= 0) {
            return false;
        }

        return esABBRecursivo(
                actual.getIzquierdo(),
                minimo,
                identificacion)
                && esABBRecursivo(
                        actual.getDerecho(),
                        identificacion,
                        maximo);
    }
}