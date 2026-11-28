package estructuras;

import modelo.Sede;


public class GrafoSedes {

    public static final String CRITERIO_DISTANCIA = "DISTANCIA";
    public static final String CRITERIO_TIEMPO = "TIEMPO";
    public static final String CRITERIO_COSTO = "COSTO";

    private NodoSede primerNodo;
    private NodoSede ultimoNodo;
    private int cantidadSedes;

    public GrafoSedes() {
        primerNodo = null;
        ultimoNodo = null;
        cantidadSedes = 0;
    }

    public void agregarSede(Sede sede) {
        if (sede == null) {
            throw new IllegalArgumentException(
                    "La sede no puede ser null.");
        }

        if (buscarSede(sede.getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una sede con ese identificador.");
        }

        NodoSede nuevo = new NodoSede(sede);

        if (primerNodo == null) {
            primerNodo = nuevo;
        } else {
            ultimoNodo.setSiguiente(nuevo);
        }

        ultimoNodo = nuevo;
        cantidadSedes++;
    }

    public Sede buscarSede(String identificador) {
        if (identificador == null) {
            return null;
        }

        NodoSede actual = primerNodo;

        while (actual != null) {
            if (actual.getDato().getIdentificador()
                    .equalsIgnoreCase(identificador.trim())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

   
    public void agregarConexion(String identificadorOrigen,
            String identificadorDestino, double distancia,
            double tiempo, double costo) {

        Sede origen = buscarSede(identificadorOrigen);
        Sede destino = buscarSede(identificadorDestino);

        if (origen == null) {
            throw new IllegalArgumentException(
                    "No existe la sede de origen.");
        }

        if (destino == null) {
            throw new IllegalArgumentException(
                    "No existe la sede de destino.");
        }

        if (origen == destino) {
            throw new IllegalArgumentException(
                    "No se permite conectar una sede consigo misma.");
        }

        origen.agregarArista(
                new Arista(destino, distancia, tiempo, costo));
    }

    public int getCantidadSedes() {
        return cantidadSedes;
    }

    public NodoSede getPrimerNodo() {
        return primerNodo;
    }

    /**
     * Devuelve una representación textual de las listas de adyacencia.
     */
    public String mostrarGrafo() {
        StringBuilder salida = new StringBuilder();
        salida.append("GRAFO DE SEDES\n");

        NodoSede actual = primerNodo;

        while (actual != null) {
            Sede sede = actual.getDato();
            salida.append(sede).append("\n  -> ");

            Arista arista = sede.getPrimeraArista();

            if (arista == null) {
                salida.append("Sin conexiones");
            }

            while (arista != null) {
                salida.append(arista);
                arista = arista.getSiguiente();

                if (arista != null) {
                    salida.append("\n  -> ");
                }
            }

            salida.append("\n");
            actual = actual.getSiguiente();
        }

        return salida.toString();
    }

    
    public String dijkstra(String identificadorOrigen,
            String identificadorDestino, String criterio) {

        String criterioNormalizado = normalizarCriterio(criterio);
        NodoSede origen = buscarNodo(identificadorOrigen);
        NodoSede destino = buscarNodo(identificadorDestino);

        if (origen == null) {
            throw new IllegalArgumentException(
                    "No existe la sede de origen.");
        }

        if (destino == null) {
            throw new IllegalArgumentException(
                    "No existe la sede de destino.");
        }

        reiniciarEstados();
        origen.setDistanciaTemporal(0);

        while (true) {
            NodoSede actual = obtenerNoVisitadoMenor();

            if (actual == null
                    || actual.getDistanciaTemporal()
                    == Double.POSITIVE_INFINITY) {
                break;
            }

            actual.setVisitado(true);

            if (actual == destino) {
                break;
            }

            Arista arista = actual.getDato().getPrimeraArista();

            while (arista != null) {
                NodoSede vecino = buscarNodo(
                        arista.getDestino().getIdentificador());

                if (vecino != null && !vecino.isVisitado()) {
                    double nuevoPeso = actual.getDistanciaTemporal()
                            + obtenerPeso(arista, criterioNormalizado);

                    if (nuevoPeso < vecino.getDistanciaTemporal()) {
                        vecino.setDistanciaTemporal(nuevoPeso);
                        vecino.setAnterior(actual.getDato());
                    }
                }

                arista = arista.getSiguiente();
            }
        }

        if (destino.getDistanciaTemporal()
                == Double.POSITIVE_INFINITY) {
            return "No existe una ruta desde "
                    + origen.getDato().getNombre() + " hasta "
                    + destino.getDato().getNombre() + ".";
        }

        String ruta = construirRuta(origen, destino);

        return "Ruta óptima por " + criterioNormalizado + ":\n"
                + ruta + "\nTotal: "
                + destino.getDistanciaTemporal();
    }

    private String normalizarCriterio(String criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el criterio de búsqueda.");
        }

        if (criterio.equalsIgnoreCase(CRITERIO_DISTANCIA)) {
            return CRITERIO_DISTANCIA;
        }

        if (criterio.equalsIgnoreCase(CRITERIO_TIEMPO)) {
            return CRITERIO_TIEMPO;
        }

        if (criterio.equalsIgnoreCase(CRITERIO_COSTO)) {
            return CRITERIO_COSTO;
        }

        throw new IllegalArgumentException(
                "El criterio debe ser DISTANCIA, TIEMPO o COSTO.");
    }

    private double obtenerPeso(Arista arista, String criterio) {
        if (CRITERIO_DISTANCIA.equals(criterio)) {
            return arista.getDistancia();
        }

        if (CRITERIO_TIEMPO.equals(criterio)) {
            return arista.getTiempo();
        }

        return arista.getCosto();
    }

    private void reiniciarEstados() {
        NodoSede actual = primerNodo;

        while (actual != null) {
            actual.reiniciarDijkstra();
            actual = actual.getSiguiente();
        }
    }

    private NodoSede obtenerNoVisitadoMenor() {
        NodoSede menor = null;
        NodoSede actual = primerNodo;

        while (actual != null) {
            if (!actual.isVisitado()
                    && (menor == null
                    || actual.getDistanciaTemporal()
                    < menor.getDistanciaTemporal())) {
                menor = actual;
            }

            actual = actual.getSiguiente();
        }

        return menor;
    }

    private NodoSede buscarNodo(String identificador) {
        if (identificador == null) {
            return null;
        }

        NodoSede actual = primerNodo;

        while (actual != null) {
            if (actual.getDato().getIdentificador()
                    .equalsIgnoreCase(identificador.trim())) {
                return actual;
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

    private String construirRuta(NodoSede origen, NodoSede destino) {
        String ruta = "";
        NodoSede actual = destino;

        while (actual != null) {
            String nombre = actual.getDato().getNombre();

            if (ruta.isEmpty()) {
                ruta = nombre;
            } else {
                ruta = nombre + " -> " + ruta;
            }

            if (actual == origen) {
                return ruta;
            }

            actual = buscarNodoPorSede(actual.getAnterior());
        }

        return "No fue posible reconstruir la ruta.";
    }

    private NodoSede buscarNodoPorSede(Sede sede) {
        if (sede == null) {
            return null;
        }

        return buscarNodo(sede.getIdentificador());
    }
}
