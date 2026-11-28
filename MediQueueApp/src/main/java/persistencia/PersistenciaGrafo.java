package persistencia;

import estructuras.Arista;
import estructuras.GrafoSedes;
import estructuras.NodoSede;
import modelo.Sede;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Adaptador JSON y coordinador de persistencia del grafo de sedes.
 * Utiliza PersistenciaDatos para leer y escribir grafo.json.
 */
public class PersistenciaGrafo implements AdaptadorJson<GrafoSedes> {

    private final PersistenciaDatos persistenciaDatos;

    public PersistenciaGrafo(PersistenciaDatos persistenciaDatos) {
        if (persistenciaDatos == null) {
            throw new IllegalArgumentException(
                    "La persistencia de datos no puede ser null.");
        }

        this.persistenciaDatos = persistenciaDatos;
    }

    public PersistenciaGrafo(Path carpetaDatos) {
        this(new PersistenciaDatos(carpetaDatos));
    }

    public void guardar(GrafoSedes grafo) throws IOException {
        persistenciaDatos.guardarGrafo(grafo, this);
    }

    public GrafoSedes cargar() throws IOException {
        GrafoSedes grafo = persistenciaDatos.cargarGrafo(this);

        if (grafo == null) {
            return new GrafoSedes();
        }

        return grafo;
    }

    @Override
    public String convertirAJson(GrafoSedes grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException(
                    "El grafo no puede ser null.");
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"sedes\":[");

        NodoSede nodo = grafo.getPrimerNodo();
        boolean primeraSede = true;

        while (nodo != null) {
            Sede sede = nodo.getDato();

            if (!primeraSede) {
                json.append(",");
            }

            json.append("{")
                    .append("\"identificador\":")
                    .append(EscritorJson.cadena(sede.getIdentificador()))
                    .append(",\"nombre\":")
                    .append(EscritorJson.cadena(sede.getNombre()))
                    .append(",\"tipo\":")
                    .append(EscritorJson.cadena(sede.getTipo()))
                    .append("}");

            primeraSede = false;
            nodo = nodo.getSiguiente();
        }

        json.append("],\"conexiones\":[");
        nodo = grafo.getPrimerNodo();
        boolean primeraConexion = true;

        while (nodo != null) {
            Sede origen = nodo.getDato();
            Arista arista = origen.getPrimeraArista();

            while (arista != null) {
                if (!primeraConexion) {
                    json.append(",");
                }

                json.append("{")
                        .append("\"origen\":")
                        .append(EscritorJson.cadena(
                                origen.getIdentificador()))
                        .append(",\"destino\":")
                        .append(EscritorJson.cadena(
                                arista.getDestino().getIdentificador()))
                        .append(",\"distancia\":")
                        .append(Double.toString(arista.getDistancia()))
                        .append(",\"tiempo\":")
                        .append(Double.toString(arista.getTiempo()))
                        .append(",\"costo\":")
                        .append(Double.toString(arista.getCosto()))
                        .append("}");

                primeraConexion = false;
                arista = arista.getSiguiente();
            }

            nodo = nodo.getSiguiente();
        }

        json.append("]}");
        return json.toString();
    }

    @Override
    public GrafoSedes convertirDesdeJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El JSON no puede estar vacío.");
        }

        JsonValor raiz = new LectorJson(json).leer();
        raiz.exigirTipo(JsonValor.OBJETO);

        GrafoSedes grafo = new GrafoSedes();

        JsonValor sedes = raiz.campo("sedes");
        sedes.exigirTipo(JsonValor.ARREGLO);

        JsonValor actual = sedes.getPrimerHijo();

        while (actual != null) {
            actual.exigirTipo(JsonValor.OBJETO);

            String identificador = actual.campo(
                    "identificador").comoTexto();
            String nombre = actual.campo("nombre").comoTexto();
            String tipo = actual.campo("tipo").comoTexto();

            grafo.agregarSede(
                    new Sede(identificador, nombre, tipo));

            actual = actual.getSiguiente();
        }

        JsonValor conexiones = raiz.campo("conexiones");
        conexiones.exigirTipo(JsonValor.ARREGLO);

        actual = conexiones.getPrimerHijo();

        while (actual != null) {
            actual.exigirTipo(JsonValor.OBJETO);

            String origen = actual.campo("origen").comoTexto();
            String destino = actual.campo("destino").comoTexto();
            double distancia = leerNumero(actual, "distancia");
            double tiempo = leerNumero(actual, "tiempo");
            double costo = leerNumero(actual, "costo");

            grafo.agregarConexion(
                    origen, destino, distancia, tiempo, costo);

            actual = actual.getSiguiente();
        }

        return grafo;
    }

    private double leerNumero(JsonValor objeto, String campo) {
        JsonValor valor = objeto.campo(campo);
        valor.exigirTipo(JsonValor.NUMERO);

        double numero = Double.parseDouble(valor.comoNumeroTexto());

        if (Double.isNaN(numero) || Double.isInfinite(numero)) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser un número finito.");
        }

        return numero;
    }
}
