package modelo;

import estructuras.Arista;

/**
 * Representa una sede de atención: hospital, clínica o EBAIS.
 * Cada sede mantiene la lista enlazada de sus conexiones salientes.
 */
public class Sede {

    public static final String TIPO_HOSPITAL = "Hospital";
    public static final String TIPO_CLINICA = "Clinica";
    public static final String TIPO_EBAIS = "EBAIS";

    private String identificador;
    private String nombre;
    private String tipo;
    private Arista primeraArista;
    private Arista ultimaArista;

    public Sede(String identificador, String nombre, String tipo) {
        validarTexto(identificador, "El identificador de la sede es obligatorio.");
        validarTexto(nombre, "El nombre de la sede es obligatorio.");
        validarTipo(tipo);

        this.identificador = identificador.trim();
        this.nombre = nombre.trim();
        this.tipo = normalizarTipo(tipo);
        this.primeraArista = null;
        this.ultimaArista = null;
    }

    private void validarTexto(String texto, String mensaje) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarTipo(String tipo) {
        validarTexto(tipo, "El tipo de sede es obligatorio.");

        if (!tipo.equalsIgnoreCase(TIPO_HOSPITAL)
                && !tipo.equalsIgnoreCase(TIPO_CLINICA)
                && !tipo.equalsIgnoreCase(TIPO_EBAIS)
                && !tipo.equalsIgnoreCase("Clínica")) {
            throw new IllegalArgumentException(
                    "El tipo debe ser Hospital, Clinica o EBAIS.");
        }
    }

    private String normalizarTipo(String tipo) {
        if (tipo.equalsIgnoreCase(TIPO_HOSPITAL)) {
            return TIPO_HOSPITAL;
        }

        if (tipo.equalsIgnoreCase(TIPO_EBAIS)) {
            return TIPO_EBAIS;
        }

        return TIPO_CLINICA;
    }

    /**
     * Agrega una conexión saliente. La conexión no se duplica
     * si ya existe otra hacia la misma sede.
     */
    public void agregarArista(Arista arista) {
        if (arista == null) {
            throw new IllegalArgumentException(
                    "La conexión no puede ser null.");
        }

        if (buscarArista(arista.getDestino().getIdentificador()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una conexión hacia esa sede.");
        }

        if (primeraArista == null) {
            primeraArista = arista;
        } else {
            ultimaArista.setSiguiente(arista);
        }

        ultimaArista = arista;
    }

    public Arista buscarArista(String identificadorDestino) {
        Arista actual = primeraArista;

        while (actual != null) {
            if (actual.getDestino().getIdentificador()
                    .equalsIgnoreCase(identificadorDestino)) {
                return actual;
            }
            actual = actual.getSiguiente();
        }

        return null;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public Arista getPrimeraArista() {
        return primeraArista;
    }

    @Override
    public String toString() {
        return identificador + " - " + nombre + " (" + tipo + ")";
    }
}

