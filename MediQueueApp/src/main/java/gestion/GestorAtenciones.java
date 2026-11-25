package gestion;

import estructuras.NodoAtencion;
import modelo.Atencion;
import modelo.Tiquete;
import persistencia.PersistenciaAtenciones;
import java.io.IOException;

public class GestorAtenciones {

    private NodoAtencion primero;
    private final PersistenciaAtenciones persistencia;

    // Precios base sugeridos para el cálculo de costos
    private static final double COSTO_CONSULTA = 15000.0;
    private static final double COSTO_SALA = 25000.0;
    private static final double COSTO_EMERGENCIA = 50000.0;
    private static final double RECARGO_PREFERENCIAL = 5000.0;

    public GestorAtenciones(PersistenciaAtenciones persistencia) {
        if (persistencia == null) {
            throw new IllegalArgumentException("La persistencia de atenciones no puede ser nula.");
        }

        this.persistencia = persistencia;
        this.primero = null;
        cargarDatos();
    }

    public Atencion registrarNuevaAtencion(Tiquete tiquete, String medicoAsignado, String espacioAsignado) {
        if (tiquete == null) {
            throw new IllegalArgumentException("El tiquete es obligatorio para iniciar la atención.");
        }

        Atencion nuevaAtencion = new Atencion(
                tiquete,
                medicoAsignado,
                "Pendiente",
                "Pendiente",
                "Pendiente",
                "Pendiente",
                espacioAsignado,
                0.0,
                Atencion.EN_ATENCION
        );

        agregarAlFinal(nuevaAtencion);
        tiquete.registrarAtencion();

        guardarDatos();
        return nuevaAtencion;
    }

    public void finalizarAtencion(String idTiquete, String diagnostico, String nombreMedicamento,
                                  String observaciones, String procedimientos) {

        Atencion atencionActiva = buscarAtencionActivaPorTiquete(idTiquete);

        if (atencionActiva == null) {
            throw new IllegalStateException("No se encontró una atención activa para el tiquete: " + idTiquete);
        }

        atencionActiva.setDiagnostico(diagnostico);
        atencionActiva.setMedicamentos(nombreMedicamento);
        atencionActiva.setObservaciones(observaciones);
        atencionActiva.setProcedimientos(procedimientos);
        atencionActiva.setCosto(calcularCosto(atencionActiva.getTiquete()));
        atencionActiva.setEstado(Atencion.FINALIZADA);

        guardarDatos();
    }

    public void cancelarAtencion(String idTiquete) {
        Atencion atencionActiva = buscarAtencionActivaPorTiquete(idTiquete);

        if (atencionActiva == null) {
            throw new IllegalStateException("No se encontró una atención activa para el tiquete: " + idTiquete);
        }

        atencionActiva.setEstado(Atencion.CANCELADA);
        guardarDatos();
    }

    private double calcularCosto(Tiquete tiquete) {
        double costoBase = 0.0;

        switch (tiquete.getTipoAtencion()) {
            case Tiquete.TIPO_CONSULTA:
                costoBase = COSTO_CONSULTA;
                break;
            case Tiquete.TIPO_SALA:
                costoBase = COSTO_SALA;
                break;
            case Tiquete.TIPO_EMERGENCIA:
                costoBase = COSTO_EMERGENCIA;
                break;
            default:
                costoBase = COSTO_CONSULTA;
        }

        if (tiquete.esPreferencial()) {
            costoBase += RECARGO_PREFERENCIAL;
        }

        return costoBase;
    }

    public Atencion buscarAtencionActivaPorTiquete(String idTiquete) {
        NodoAtencion actual = primero;

        while (actual != null) {
            Atencion atencion = actual.getDato();

            if (atencion.getTiquete().getId().equalsIgnoreCase(idTiquete)
                    && atencion.getEstado().equals(Atencion.EN_ATENCION)) {
                return atencion;
            }

            actual = actual.getSiguiente();
        }

        return null;
    }

    private void agregarAlFinal(Atencion atencion) {
        NodoAtencion nuevo = new NodoAtencion(atencion);

        if (primero == null) {
            primero = nuevo;
        } else {
            NodoAtencion actual = primero;

            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }

            actual.setSiguiente(nuevo);
        }
    }

    public NodoAtencion getPrimero() {
        return primero;
    }

    // Métodos de Persistencia

    private void cargarDatos() {
        try {
            this.primero = persistencia.cargar();
        } catch (IOException e) {
            System.err.println("Error al cargar el historial de atenciones: " + e.getMessage());
            this.primero = null;
        }
    }

    private void guardarDatos() {
        try {
            persistencia.guardar(primero);
        } catch (IOException e) {
            System.err.println("Error al guardar las atenciones: " + e.getMessage());
        }
    }
}